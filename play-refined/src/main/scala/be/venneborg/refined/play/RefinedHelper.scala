package be.venneborg.refined.play

import eu.timepit.refined.api.{RefType, Refined, Validate}

object RefinedHelper {

  private[play] def handleBound[T, P](bound: Either[String, T])
                                     (implicit reftype: RefType[Refined],
                                      validate: Validate[T, P]): Either[String, Refined[T, P]] = {
    bound.right.flatMap { v =>
      reftype.refine[P](v) match {
        case Right(valueP) => Right(valueP)
        case l @ Left(_)   => l
      }
    }
  }

}
