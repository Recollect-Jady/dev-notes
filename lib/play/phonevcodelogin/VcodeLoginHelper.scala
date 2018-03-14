package lib.play.phonevcodelogin

import scala.concurrent.Future

import org.pac4j.core.profile.CommonProfile


trait VcodeLoginHelper {
  def findProfileByPhoneNumber(phoneNumber: String): Future[CommonProfile]
}
