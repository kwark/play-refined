package be.venneborg.refined.play

import eu.timepit.refined.api.{RefType, Refined, Validate}
import play.api.data.FormError
import play.api.data.format.Formatter

import scala.language.higherKinds
import scala.util.control.NonFatal

object RefinedForms {

  implicit def refinedStringFormat[P](implicit reftype: RefType[Refined],
                                               validate: Validate[String, P]): Formatter[Refined[String, P]] = new Formatter[Refined[String, P]] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Refined[String, P]] =
      data.get(key) match {
        case None => Left(Seq(FormError(key, "error.required", Nil)))
        case Some(v) => reftype.refine[P](v) match {
          case Right(valueP) => Right(valueP)
          case Left(error) =>
            val (message, args) = RefinedTranslations.translate(error)
            Left(Seq(FormError(key, message, args)))
        }
      }

    override def unbind(key: String, value: Refined[String, P]) = Map[String, String](key -> value.value)
  }

  implicit def refinedIntFormat[P](implicit reftype: RefType[Refined],
                                            validate: Validate[Int, P]): Formatter[Refined[Int, P]] =
    refinedNumberFormatter[Int, P](_.toInt)

  implicit def refinedLongFormat[P](implicit reftype: RefType[Refined],
                                   validate: Validate[Long, P]): Formatter[Refined[Long, P]] =
    refinedNumberFormatter[Long, P](_.toLong)

  implicit def refinedDoubleFormat[P](implicit reftype: RefType[Refined],
                                      validate: Validate[Double, P]): Formatter[Refined[Double, P]] =
    refinedNumberFormatter[Double, P](_.toDouble, real = true)


  private def refinedNumberFormatter[N, P](convert: String => N, real: Boolean = false)
                                          (implicit reftype: RefType[Refined],
                                           numeric: Numeric[N],
                                           validate: Validate[N, P]): Formatter[Refined[N, P]] = {

    val (formatString, errorString) = if (real) ("format.real", "error.real") else ("format.numeric", "error.number")

    new Formatter[Refined[N, P]] {
      override val format = Some(formatString -> Nil)
      def bind(key: String, data: Map[String, String]) =
        data.get(key) match {
          case None => Left(Seq(FormError(key, "error.required", Nil)))
          case Some(v) =>
            try {
              reftype.refine[P](convert(v)) match {
                case Right(valueP) => Right(valueP)
                case Left(error) =>
                  val (message, args) = RefinedTranslations.translate(error)
                  Left(Seq(FormError(key, message, args)))
              }
            } catch {
              case NonFatal(_) => Left(Seq(FormError(key, errorString, Nil)))
            }
        }


      def unbind(key: String, value: Refined[N, P]) = Map(key -> value.value.toString)
    }
  }

}




