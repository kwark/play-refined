package be.venneborg.refined.play

import eu.timepit.refined.api.{RefType, Refined, Validate}
import play.api.libs.json._

import scala.collection.immutable.Map
import scala.language.higherKinds

object RefinedJsonFormats {

  implicit def writeRefined[T, P, F[_, _]](implicit writesT: Writes[T],
                                           reftype: RefType[F]): Writes[F[T, P]] =
    Writes(value => writesT.writes(reftype.unwrap(value)))

  implicit def readRefined[T, P, F[_, _]](implicit readsT: Reads[T],
                                          reftype: RefType[F],
                                          validate: Validate[T, P]): Reads[F[T, P]] =
    Reads(jsValue =>
      readsT.reads(jsValue).flatMap { valueT =>
        reftype.refine[P](valueT) match {
          case Right(valueP) => JsSuccess(valueP)
          case Left(error) =>
            val errors = RefinedTranslations.translate(error)
            JsError(JsonValidationError(errors.map(_.errorCode), errors.map(_.args): _*))
        }
      })

  implicit def formatRefined[T, P, F[_, _]](implicit writesT: Writes[T],
                                             readsT: Reads[T],
                                             validate: Validate[T, P],
                                             reftype: RefType[F]): Format[F[T, P]] =
    Format[F[T, P]](readRefined[T, P, F], writeRefined[T, P, F])

  implicit def refinedMapFormat[V, P, F[String, _]](implicit
                                                    readsKey: Reads[F[String, P]],
                                                    readsValue: Reads[V],
                                                    writesValue: Writes[V],
                                                    reftype: RefType[F]): Format[Map[F[String, P], V]] = new Format[Map[F[String, P], V]] {

    override def writes(o: Map[F[String, P], V]): JsValue = OWrites[Map[F[String, P], V]] { ts =>
      JsObject(ts.map { case (key, value) => reftype.unwrap(key) -> writesValue.writes(value)})
    }.writes(o)

    override def reads(json: JsValue): JsResult[Map[F[String, P], V]] = Reads.mapReads(
      (s: String) => readsKey.reads(JsString(s))
    )(readsValue).reads(json)
  }

}
