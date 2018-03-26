package be.venneborg.refined.play

import eu.timepit.refined.api.{RefType, Validate}
import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.collection.Seq
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
            JsError(ValidationError(errors.map(_.errorCode), errors.map(_.args): _*))
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

    // copied and adapted from Play 2.6 Reads.mapReads method
    override def reads(json: JsValue): JsResult[Map[F[String, P], V]] = Reads[Map[F[String, P], V]] {
      case JsObject(m) =>
        type Errors = Seq[(JsPath, Seq[ValidationError])]

        def locate(e: Errors, key: String) = e.map {
          case (p, valerr) => (JsPath \ key) ++ p -> valerr
        }

        // !! Keep accumulating the error after the first one
        m.foldLeft(Right(Map.empty): Either[Errors, Map[F[String, P], V]]) {
          case (acc, (key, value)) =>
            val result = for {
              rv <- readsValue.reads(value)
              rk <- readsKey.reads(JsString(key))
            } yield rk -> rv

            (acc, result) match {
              case (Right(vs), JsSuccess(v, _)) => Right(vs + v)
              case (Right(_), JsError(e)) => Left(locate(e, key))
              case (Left(e), _: JsSuccess[_]) => Left(e)
              case (Left(e1), JsError(e2)) => Left(e1 ++ locate(e2, key))
            }
        }.fold(JsError.apply, res => JsSuccess(res))

      case _ => JsError("error.expected.jsobject")

    }.reads(json)

  }

}
