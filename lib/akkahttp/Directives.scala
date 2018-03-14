package lib.akkahttp

import lib.Paging


trait Directives extends akka.http.scaladsl.server.Directives {
  protected def pagingParam =
    parameters(("offset".as[Long] ? 0L, "limit".as[Long] ? 25L, "search".?))
      .tmap(Paging.tupled)
}
