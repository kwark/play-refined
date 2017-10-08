package be.venneborg.refined.play

import eu.timepit.refined.api.{RefType, Refined, Validate}
import play.api.mvc.PathBindable

object RefinedPathBinders {

  implicit def refinedStringPathBinder[P](implicit reftype: RefType[Refined],
                                          validate: Validate[String, P],
                                          stringBinder: PathBindable[String]) =
    new RefinedPathBindable[String, P](stringBinder, reftype, validate) {
      override def unbind(key: String, value: Refined[String, P]) = stringBinder.unbind(key, value.value)
    }

  implicit def refinedNumberPathBinder[N, P](implicit reftype: RefType[Refined],
                                             validate: Validate[N, P],
                                             numeric: Numeric[N],
                                             numberBinder: PathBindable[N]) =
    new RefinedPathBindable[N, P](numberBinder, reftype, validate){
      override def unbind(key: String, value: Refined[N, P]) = numberBinder.unbind(key, value.value)
    }


  abstract class RefinedPathBindable[T, P](binder: PathBindable[T],
                                           reftype: RefType[Refined],
                                           implicit val validate: Validate[T, P]) extends PathBindable[Refined[T, P]] {

    override def bind(key: String, value: String): Either[String, Refined[T, P]] = {
      RefinedHelper.handleBound[T, P](binder.bind(key, value))
    }

  }

}
