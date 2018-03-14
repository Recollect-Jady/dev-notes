package lib.akkahttp

import akka.http.scaladsl.server.PathMatchers.Segment
import lib.idhashing._


trait PathMatchers {
  protected def DecodeIdHash(implicit idHashing: IdHashing = null) = Segment.flatMap(_.unhash)

  protected def enumMatcher[E <: Enumeration](enum: E) = Segment.map(enum.withName(_))
}
