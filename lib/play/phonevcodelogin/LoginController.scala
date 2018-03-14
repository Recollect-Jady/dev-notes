package lib.play.phonevcodelogin

import scala.concurrent.ExecutionContext

import javax.inject.{Inject, Singleton}
import lib.play.WsCallException
import org.pac4j.play.CallbackController
import play.api.mvc.{AbstractController, ControllerComponents, Request}


@Singleton
class LoginController @Inject() (cc: ControllerComponents, authenticator: VcodeAuthenticator,
    callbackController: CallbackController)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def login(username: Option[String], error: Option[String]) = Action { implicit request =>
    sessionId
      .map(_ => Ok(views.html.Login(username, error)))
      .getOrElse(Redirect(callbackController.getDefaultUrl)) // trigger login for pac4jSessionId
  }

  private def sessionId(implicit request: Request[_]) = request.session.get("pac4jSessionId")

  def sendVcode(phoneNumber: String) = Action.async { implicit request =>
    authenticator.sendVcode(phoneNumber, sessionId.get)
      .map(_ => Ok)
      .recover {
        case WsCallException(404, _) =>
          NotFound
        case ex =>
          BadRequest(ex.getMessage)
      }
  }
}
