package lib.akkahttp

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import lib.Page
import lib.idhashing._


trait Completes extends akka.http.scaladsl.server.Directives {
  protected implicit def intFutureToLongFuture(intFuture: Future[Int])(implicit ec: ExecutionContext) =
    intFuture.map(_.toLong)

  protected def findOneToComplete[T](findOne: Future[Option[T]])
      (implicit marshaller: ToResponseMarshaller[T]) = onComplete(findOne) {
    case Success(Some(value)) => complete(value)
    case Success(None) => complete(StatusCodes.NotFound)
    case Failure(ex) => complete(StatusCodes.InternalServerError -> ex.getMessage)
  }

  protected def findSomeToComplete[T](findSome: Future[Seq[T]])
      (implicit marshaller: ToResponseMarshaller[Seq[T]]) = onComplete(findSome) {
    case Success(value) => complete(value)
    case Failure(ex) => complete(StatusCodes.InternalServerError -> ex.getMessage)
  }

  protected def findPageToComplete[T](findPage: Future[Page[T]])
      (implicit marshaller: ToResponseMarshaller[Page[T]]) = onComplete(findPage) {
    case Success(value) => complete(value)
    case Failure(ex) => complete(StatusCodes.InternalServerError -> ex.getMessage)
  }

  protected def deleteToComplete(delete: Future[_]) = onComplete(delete) {
    case Success(_) =>
      complete(StatusCodes.NoContent)
    case Failure(ex) =>
      complete(StatusCodes.BadRequest -> ex.getMessage)
  }

  protected def createToComplete(create: Future[Long])(implicit idHashing: IdHashing = null) = onComplete(create) {
    case Success(id) =>
      val idStr = Option(idHashing).map(_ => id.hash).getOrElse(id.toString)
      respondWithHeader(RawHeader("X-LKB-Entity-Id", idStr)) {
        complete(StatusCodes.Created)
      }
    case Failure(ex) =>
      complete(StatusCodes.BadRequest -> ex.getMessage)
  }

  protected def updateToComplete(update: Future[_]) = onComplete(update) {
    case Success(_) =>
      complete(StatusCodes.NoContent)
    case Failure(ex) =>
      complete(StatusCodes.BadRequest -> ex.getMessage)
  }

  protected def okWithOr500[T](future: Future[T])(implicit marshaller: ToResponseMarshaller[T]) =
    onComplete(future) {
      case Success(value) => complete(value)
      case Failure(ex) => complete(StatusCodes.InternalServerError -> ex.getMessage)
    }

  protected def okOr400(future: Future[_]) = onComplete(future) {
    case Success(_) => complete(StatusCodes.OK)
    case Failure(ex) => complete(StatusCodes.BadRequest -> ex.getMessage)
  }

  protected def okWithOr400[T](future: Future[T])(implicit marshaller: ToResponseMarshaller[T]) =
    onComplete(future) {
      case Success(value) => complete(value)
      case Failure(ex) => complete(StatusCodes.BadRequest -> ex.getMessage)
    }
}
