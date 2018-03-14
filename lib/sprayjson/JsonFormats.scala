package lib.sprayjson

import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import lib.Page
import spray.json._


trait JsonFormats extends DefaultJsonProtocol {
  implicit object localTimeFormat extends JsonFormat[LocalTime] {
    val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    def write(time: LocalTime) = JsString(time.format(formatter))

    def read(value: JsValue) = value match {
      case JsString(text) => LocalTime.parse(text, formatter)
      case _ => deserializationError("ISO_LOCAL_TIME expected")
    }
  }

  implicit object localDateTimeFormat extends JsonFormat[LocalDateTime] {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    def write(datetime: LocalDateTime) = JsString(datetime.format(formatter))

    def read(value: JsValue) = value match {
      case JsString(text) => LocalDateTime.parse(text, formatter)
      case _ => deserializationError("ISO_LOCAL_DATE_TIME expected")
    }
  }

  implicit object localDateFormat extends JsonFormat[LocalDate] {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    def write(date: LocalDate) = JsString(date.format(formatter))

    def read(value: JsValue) = value match {
      case JsString(text) => LocalDate.parse(text, formatter)
      case _ => deserializationError("ISO_LOCAL_DATE expected")
    }
  }

  def enumFormat[E <: Enumeration](enum: E) = new JsonFormat[enum.Value] {
    def write(v: enum.Value) = JsString(v.toString())

    def read(value: JsValue) = value match {
      case JsString(text) => enum.withName(text)
      case _ => deserializationError(s"One of $enum expected")
    }
  }

  implicit def pageFormat[T : JsonFormat] = jsonFormat3(Page.apply[T])
}
