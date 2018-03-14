package lib.play

import play.api.libs.json._
import play.api.libs.json.Reads._


object Json {
  def enumFormat[E <: Enumeration](enum: E) = Format(
    new Reads[enum.Value] {
      def reads(json: JsValue) = JsSuccess(enum.withName(json.as[String]))
    },
    new Writes[enum.Value] {
      def writes(value: enum.Value) = JsString(value.toString())
    }
  )

  def secondsToMinutes(child: Symbol) = (__ \ child).json.update(of[Int].map(_ / 60.0).map(JsNumber(_)))
  def minutesToSeconds(child: Symbol) = (__ \ child).json.update(of[Double].map(_ * 60).map(JsNumber(_)))

  def secondsToHours(child: Symbol) = (__ \ child).json.update(of[Int].map(_ / 3600.0).map(JsNumber(_)))
  def hoursToSeconds(child: Symbol) = (__ \ child).json.update(of[Double].map(_ * 3600).map(JsNumber(_)))

  def fenToYuan(child: Symbol) = (__ \ child).json.update(of[Int].map(_ / 100.0).map(JsNumber(_)))
  def yuanToFen(child: Symbol) = (__ \ child).json.update(of[Double].map(_ * 100).map(JsNumber(_)))

}
