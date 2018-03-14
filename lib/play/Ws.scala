package lib.play

import scala.concurrent.{ExecutionContext, Future}

import play.api.libs.ws.WSResponse


case class WsCallException(status: Int, body: String) extends Exception(s"$status: $body")

trait Ws {
  implicit class ResponseFutureOps(responseFuture: Future[WSResponse]) {
    def ok[T](callback: WSResponse => T = (response: WSResponse) => Unit)(implicit ec: ExecutionContext): Future[T] =
      responseFuture.flatMap { response =>
        if (response.status >= 200 && response.status < 400)
          Future(callback(response))
        else
          Future.failed(new WebServiceCallException(response.status, response.body))
      }
  }
}
