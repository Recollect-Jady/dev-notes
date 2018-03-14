package lib.slick

import scala.concurrent.ExecutionContext

import lib.{Page, Paging}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.Ordered


trait Pagination {
  protected type DbProfile <: JdbcProfile
  protected val _dbConfig: DatabaseConfig[DbProfile]

  import _dbConfig.profile.api._

  def paginate[E, U, T, F, G, T2, U2](paging: Paging,
      filtered: String => Query[E, U, Seq],
      unfiltered: Query[E, U, Seq],
      sortBy: E => T,
      map: E => F
  )(implicit
      ev: T => Ordered,
      shape: Shape[_ <: FlatShapeLevel, F, T2, G],
      ec: ExecutionContext) = {
    val query = paging.search match {
      case Some(search) => filtered(search)
      case None => unfiltered
    }
    val find = query
      .sortBy(sortBy)
      .drop(paging.offset)
      .take(paging.limit)
      .map(map)
      .result

    val totalItems = unfiltered.length.result
    val matchedItems = query.length.result

    for {
      data <- find
      matched <- matchedItems
      total <- paging.search.map(_ => totalItems).getOrElse(DBIO.successful(matched))
    } yield Page(total, matched, data)
  }
}
