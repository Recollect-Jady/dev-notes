package lib.play

import java.util.Date

import scala.util.Random


class VerificationCodeGenerator {
  private val random = new Random(new Date().getTime)

  def next() = (for (i <- 0 to 5) yield random.nextInt(10)).mkString("")
}
