package lib.akkahttp

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import scala.concurrent.Future

import akka.http.scaladsl.unmarshalling.Unmarshaller


trait Unmarshallers {
  protected implicit val localDateTimeUnmarshaller = Unmarshaller[String, LocalDateTime](
    implicit ec => text => Future {
      LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
  )

  protected implicit val localDateUnmarshaller = Unmarshaller[String, LocalDate](
    implicit ec => text => Future {
      LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE)
    }
  )

  protected def enumFromEntityUnmarshaller[E <: Enumeration](enum: E) =
    Unmarshaller.stringUnmarshaller.map(enum.withName(_))

  protected def enumUnmarshaller[E <: Enumeration](enum: E) = Unmarshaller[String, enum.Value](
    implicit ec => text => Future {
      enum.withName(text)
    }
  )
}
