package lib.play.phonevcodelogin

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import javax.inject._
import lib.play.{ShortMessageService, VerificationCodeGenerator}
import org.pac4j.core.context.WebContext
import org.pac4j.core.credentials.UsernamePasswordCredentials
import org.pac4j.core.credentials.authenticator.Authenticator
import org.pac4j.core.exception.CredentialsException
import org.pac4j.core.profile.CommonProfile
import play.api._
import play.api.cache.SyncCacheApi


@Singleton
class VcodeAuthenticator @Inject() (
  cache: SyncCacheApi,
  helper: VcodeLoginHelper,
  sms: ShortMessageService, // TODO use lib.alisms instead
  vcodeGen: VerificationCodeGenerator
)(implicit ec: ExecutionContext)
    extends Authenticator[UsernamePasswordCredentials] {

  private val logger = Logger(this.getClass)

  override def validate(credentials: UsernamePasswordCredentials, context: WebContext) = {
    val sessionId = context.getSessionIdentifier
    val cacheKey = vcodeCacheKey(sessionId)

    cache.get[(String, String, CommonProfile)](cacheKey)
      .filter { case (phoneNumber, vcode, _) =>
        phoneNumber == credentials.getUsername && vcode == credentials.getPassword
      }
      .map { case (_, _, profile) =>
        credentials.setUserProfile(profile)
        cache.remove(cacheKey)
      }
      .getOrElse(throw new CredentialsException(""))
  }

  private def vcodeCacheKey(sessionId: String) = s"phonevcodelogin.vcode.$sessionId"

  def sendVcode(phoneNumber: String, sessionId: String) = {
    val vcode = vcodeGen.next()
    logger.debug(s"Send vcode $vcode to $phoneNumber")

    for {
      profile <- helper.findProfileByPhoneNumber(phoneNumber)
      _ <- sms.send(phoneNumber, "loginPassword", vcode) // TODO conf
    } yield cache.set(vcodeCacheKey(sessionId), (phoneNumber, vcode, profile), 5.minutes)
  }

}
