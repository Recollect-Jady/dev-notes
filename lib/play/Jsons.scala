package lib.play

import play.api.libs.json._
import play.api.libs.json.Reads._


object Jsons {
  def enumFormat[E <: Enumeration](enum: E) = Format(
    new Reads[enum.Value] {
      def reads(json: JsValue) = JsSuccess(enum.withName(json.as[String]))
    },
    new Writes[enum.Value] {
      def writes(value: enum.Value) = JsString(value.toString())
    }
  )

  def secondsToMinutes(child: Symbol) = (__ \ child).json.update(of[Long].map(_ / 60).map(JsNumber(_)))
  def minutesToSeconds(child: Symbol) = (__ \ child).json.update(of[Long].map(_ * 60).map(JsNumber(_)))

  def fenToYuan(child: Symbol) = (__ \ child).json.update(of[Long].map(_ / 100).map(JsNumber(_)))
  def yuanToFen(child: Symbol) = (__ \ child).json.update(of[Long].map(_ * 100).map(JsNumber(_)))
}
