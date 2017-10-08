package be.venneborg.refined.play

import eu.timepit.refined.api.{RefType, Refined, Validate}
import play.api.mvc.QueryStringBindable

object RefinedQueryBinders {

  implicit def refinedStringQueryBinder[P](implicit reftype: RefType[Refined],
                                           validate: Validate[String, P],
                                           stringBinder: QueryStringBindable[String]) =
    new RefinedQueryStringBindable[String, P](stringBinder, reftype, validate) {
        override def unbind(key: String, value: Refined[String, P]) = stringBinder.unbind(key, value.value)
    }

  implicit def refinedNumberQueryBinder[N, P](implicit reftype: RefType[Refined],
                                              validate: Validate[N, P],
                                              numeric: Numeric[N],
                                              numberBinder: QueryStringBindable[N]) =
    new RefinedQueryStringBindable[N, P](numberBinder, reftype, validate) {
      override def unbind(key: String, value: Refined[N, P]) = numberBinder.unbind(key, value.value)
    }


  abstract class RefinedQueryStringBindable[T, P](binder: QueryStringBindable[T],
                                                  implicit val reftype: RefType[Refined],
                                                  implicit val validate: Validate[T, P]) extends QueryStringBindable[Refined[T, P]] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Refined[T, P]]] = {
      binder.bind(key, params).map(RefinedHelper.handleBound(_))
    }

  }

}
