package lib

import org.pico.hashids.Hashids
import org.pico.hashids.syntax._


object idhashing {
  case class IdHashing(
    prefix: String = "",
    salt: String = "",
    minHashLength: Int = 0,
    alphabet: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
  ) {
    private[idhashing] implicit val hashids = Hashids.reference(salt, minHashLength, alphabet)
  }

  implicit class IdHashingStringOps(str: String) {
    def unhash(implicit hashing: IdHashing): Option[Long] = {
      import hashing.hashids
      Option(str)
        .filter(_.startsWith(hashing.prefix))
        .map(_.substring(hashing.prefix.length))
        .map(_.unhashid)
        .flatMap(_.headOption)
    }
  }

  implicit class IdHashingLongOps(id: Long) {
    def hash(implicit hashing: IdHashing): String = {
      import hashing.hashids
      hashing.prefix + id.hashid
    }
  }
}
