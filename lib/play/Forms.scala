package lib.play

import play.api.data.Forms._

object Forms {
  def enumMapping[E <: Enumeration](enum: E) = nonEmptyText.transform[enum.Value](
    text => enum.withName(text),
    value => value.toString()
  )

  def moneyMapping(precision: Int, scale: Int) = bigDecimal(precision, scale).transform[Int](
    yuan => (yuan * 100).toInt,
    fen => BigDecimal(fen) / 100
  )
}
