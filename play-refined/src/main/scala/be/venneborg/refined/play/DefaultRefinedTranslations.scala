package be.venneborg.refined.play

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

  def translate(error: String): (String, Seq[Any])

}

object RefinedTranslations {

  var refinedTranslations: RefinedTranslations = DefaultRefinedTranslations

  def translate(error: String): (String, Seq[Any]) = refinedTranslations.translate(error)

}

object DefaultRefinedTranslations extends RefinedTranslations {

  val predicateDidNotFail = "Predicate ([^:]+) did not fail.".r
  val predicateFailed     = "Predicate failed: (.*).".r

  val sizePredicateFailed = "Predicate taking size\\(.*\\) = \\d+ failed: (.*)".r
  val rightFailed = "Right predicate of \\(.*\\) failed: (Predicate .*)".r
  val leftFailed = "Left predicate of \\(.*\\) failed: (Predicate .*)".r

  val urlPredicateFailed = "Url predicate failed: (.*): .*".r
  val uuidPredicateFailed = "Uuid predicate failed: Invalid UUID string: .*".r
  val startsWith = "\".*\"\\.startsWith\\(\".*\"\\)".r
  val endsWith = "\".*\"\\.endsWith\\(\".*\"\\)".r
  val matches = "\".*\"\\.matches\\(\"(.*)\"\\)".r

  val empty = "isEmpty\\(\\)".r
  val greater = "\\(.* > (-?\\d+)\\)".r
  val less = "\\(.* < (-?\\d+)\\)".r
  val equal = "\\(.* == (-?\\d+)\\)".r

  val invalid = "error.invalid"
  val required = "error.required"

  def translate(error: String): (String, Seq[Any]) = {
    println(error) //TODO replace with trace logging
    error match {
      case sizePredicateFailed(sizePredicate) => sizePredicateTranslate(sizePredicate)
      case predicateDidNotFail(predicate) =>
        predicate match {
          case empty()      => (required, Nil)
          case less(arg)    => ("error.min", Seq(arg))
          case greater(arg) => ("error.max", Seq(arg))
          case _            => (invalid, Seq(predicate))
        }
      case predicateFailed(predicate) =>
        predicate match {
          case greater(arg)     => ("error.min.strict", Seq(arg))
          case less(arg)        => ("error.max.strict", Seq(arg))
          case startsWith()     => (invalid, Nil)
          case endsWith()       => (invalid, Nil)
          case matches(pattern) => ("error.pattern", Seq(pattern))
          case _                => (invalid, Seq(predicate))
        }
      case uuidPredicateFailed() => ("error.uuid", Nil)
      case urlPredicateFailed(reason) => ("error.invalid", Seq(reason))

      case _ => (error, Nil)
    }
  }

  private def sizePredicateTranslate(sizePredicate: String): (String, Seq[Any]) = {
    sizePredicate match {
      case predicateDidNotFail(predicate) =>
        predicate match {
          case less(arg) => ("error.minLength", Seq(arg))
          case greater(arg) => ("error.maxLength", Seq(arg))
          case _ => (invalid, Seq(predicate))
        }
      case predicateFailed(predicate) =>
        predicate match {
          case equal(arg) => ("error.length", Seq(arg))
          case _ => (invalid, Seq(predicate))
        }
      case rightFailed(rightPredicate) => sizePredicateTranslate(rightPredicate)
      case leftFailed(leftPredicate) => sizePredicateTranslate(leftPredicate)
      case _ => (sizePredicate, Nil)
    }
  }
}
