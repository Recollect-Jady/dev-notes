package lib.play

import play.api.data.Forms._

object Forms {
  def enumMapping[E <: Enumeration](enum: E) = nonEmptyText.transform[enum.Value](
    text => enum.withName(text),
    value => value.toString()
  )
}
