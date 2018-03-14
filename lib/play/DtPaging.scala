package lib.play

import play.api.mvc.QueryStringBindable


case class DtPaging(draw: Int, start: Long, length: Long, search: Option[String])

object DtPaging {
  implicit def queryStringBindable(implicit
      intBinder: QueryStringBindable[Int],
      longBinder: QueryStringBindable[Long],
      strOptBinder: QueryStringBindable[Option[String]]) = new QueryStringBindable[DtPaging] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, DtPaging]] = {
      for {
        draw <- intBinder.bind("draw", params)
        start <- longBinder.bind("start", params)
        length <- longBinder.bind("length", params)
        search <- strOptBinder.bind("search", params)
      } yield {
        (draw, start, length, search) match {
          case (Right(draw), Right(start), Right(length), Right(search)) =>
            Right(DtPaging(draw, start, length, search.map(_.trim).filterNot(_.isEmpty)))
          case _ =>
            Left("Unable to bind an DtPaging")
        }
      }
    }

    override def unbind(key: String, dtPaging: DtPaging): String = {
      intBinder.unbind("draw", dtPaging.draw) +
        "&" + longBinder.unbind("start", dtPaging.start) +
        "&" + longBinder.unbind("length", dtPaging.length) +
        "&" + strOptBinder.unbind("search", dtPaging.search)
    }
  }
}
