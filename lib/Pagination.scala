package lib

case class Page[T](
  totalItems: Int,
  matchedItems: Int,
  data: Seq[T],
) {
  def mapItem[T2](f: T => T2) = this.copy(data = data.map(f))
}

case class Paging(offset: Long, limit: Long, search: Option[String])
