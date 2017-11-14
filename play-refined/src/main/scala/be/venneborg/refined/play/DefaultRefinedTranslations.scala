package be.venneborg.refined.play

import be.venneborg.refined.play.RefinedTranslations.Error
import org.slf4j.{Logger, LoggerFactory}

import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

/**
  * play error codes:
  *
  *  # --- Errors
  *  error.invalid=Invalid value
  *  error.invalid.java.util.Date=Invalid date value
  *  error.required=This field is required
  *  error.number=Numeric value expected
  *  error.real=Real number value expected
  *  error.real.precision=Real number value with no more than {0} digit(s) including {1} decimal(s) expected
  *  error.min=Must be greater or equal to {0}
  *  error.min.strict=Must be strictly greater than {0}
  *  error.max=Must be less or equal to {0}
  *  error.max.strict=Must be strictly less than {0}
  *  error.minLength=Minimum length is {0}
  *  error.maxLength=Maximum length is {0}
  *  error.email=Valid email required
  *  error.pattern=Must satisfy {0}
  *  error.date=Valid date required
  *  error.uuid=Valid UUID required
  *
  */
trait RefinedTranslations {

  def translate(error: String): Seq[Error]

}

object RefinedTranslations {

  case class Error(errorCode: String, args: Seq[String])

  var refinedTranslations: RefinedTranslations = DefaultRefinedTranslations

  def translate(error: String): Seq[Error] = refinedTranslations.translate(error)

}

object DefaultRefinedTranslations extends RefinedTranslations {

  val logger: Logger = LoggerFactory.getLogger(DefaultRefinedTranslations.getClass)

  val predicateDidNotFail: Regex = "Predicate ([^:]+) did not fail.".r
  val predicateFailed: Regex = "Predicate failed: (.*).".r

  val sizePredicateFailed: Regex = "Predicate taking size\\(.*\\) = \\d+ failed: (.*)".r
  val rightFailed: Regex = "Right predicate of \\(.*\\) failed: (Predicate .*)".r
  val leftFailed: Regex = "Left predicate of \\(.*\\) failed: (Predicate .*)".r
  val bothFailed: Regex = "Both predicates of \\(.*\\) failed. Left: (Predicate .*) Right: (Predicate .*)".r

  val urlPredicateFailed: Regex = "Url predicate failed: (.*): .*".r
  val uuidPredicateFailed: Regex = "Uuid predicate failed: Invalid UUID string: .*".r
  val startsWith: Regex = "\".*\"\\.startsWith\\(\"(.*)\"\\)".r
  val endsWith: Regex = "\".*\"\\.endsWith\\(\"(.*)\"\\)".r
  val matches: Regex = "\".*\"\\.matches\\(\"(.*)\"\\)".r

  val empty: Regex = "isEmpty\\(.*\\)".r
  val greater: Regex = "\\(.* > (-?\\d+)\\)".r
  val less: Regex = "\\(.* < (-?\\d+)\\)".r
  val equal: Regex = "\\(.* == (-?\\d+)\\)".r

  def translate(error: String): Seq[Error] = {
    if (logger.isDebugEnabled) logger.debug("translating refined validation error: "+error)
    error match {
      case bothFailed(left, right) => Seq(translateR(left), translateR(right))
      case _ => Seq(translateR(error))
    }
  }

  def translateR(error: String): Error = {
    error match {
      case leftFailed(predicate) => translateR(predicate)
      case rightFailed(predicate) => translateR(predicate)

      case sizePredicateFailed(sizePredicate) => sizePredicateTranslate(sizePredicate)

      case predicateDidNotFail(predicate) =>
        predicate match {
          case empty()      => Error("error.required", Nil)
          case less(arg)    => Error("error.min", Seq(arg))
          case greater(arg) => Error("error.max", Seq(arg))
          case _            => invalid(predicate)
        }

      case predicateFailed(predicate) =>
        predicate match {
          case greater(arg)     => Error("error.min.strict", Seq(arg))
          case less(arg)        => Error("error.max.strict", Seq(arg))
          case startsWith(arg)  => Error("error.invalid", Seq(s"not starting with: $arg"))
          case endsWith(arg)    => Error("error.invalid", Seq(s"not ending with: $arg"))
          case matches(pattern) => Error("error.pattern", Seq(pattern))
          case empty()          => Error("error.invalid", Seq("not empty"))
          case _                => invalid(predicate)
        }

      case uuidPredicateFailed() => Error("error.uuid", Nil)

      case urlPredicateFailed(reason) => Error("error.invalid", Seq(reason))

      case _ => invalid(error)
    }
  }

  private def invalid(failedPredicate: String) = {
    if (logger.isWarnEnabled) logger.warn("could not translate refined validation error: "+failedPredicate)
    Error("error.invalid", Seq(failedPredicate))
  }

  private def add(s: String, x: Int): String = {
    Try(s.toInt + x) match {
      case Success(result) => result.toString
      case Failure(_)      => s
    }
  }

  private def sizePredicateTranslate(sizePredicate: String): Error = {
    sizePredicate match {
      case predicateDidNotFail(predicate) =>
        predicate match {
          case less(arg)    => Error("error.minLength", Seq(arg))
          case greater(arg) => Error("error.maxLength", Seq(arg))
          case _ => invalid(predicate)
        }
      case predicateFailed(predicate) =>
        predicate match {
          case equal(arg)   => Error("error.length", Seq(arg))
          case less(arg)    => Error("error.maxLength", Seq(add(arg, -1)))
          case greater(arg) => Error("error.minLength", Seq(add(arg, 1)))
          case _ => invalid(predicate)
        }
      case rightFailed(rightPredicate) => sizePredicateTranslate(rightPredicate)
      case leftFailed(leftPredicate)   => sizePredicateTranslate(leftPredicate)
      case _                           => Error(sizePredicate, Nil)
    }
  }
}
