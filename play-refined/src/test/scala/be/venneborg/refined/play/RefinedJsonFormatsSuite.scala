package be.venneborg.refined.play

import be.venneborg.model._
import be.venneborg.genmodel._
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json.{JsValue, Json, OFormat}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.language.higherKinds

class RefinedJsonFormatsSuite extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("check refined json serialization/deserialization") {
    import RefinedJsonFormats._
    implicit val tcFormat: OFormat[TestClass] = Json.format[TestClass]

    forAll(genTestClass) { (tc: TestClass) =>
      Json.toJson(tc).as[TestClass] shouldBe tc
    }
 }

  test("check refined serialization/deserialization for Map with refined String as key") {
    import RefinedJsonFormats._
    import eu.timepit.refined.auto._

    val map: RMAP = Map[RSM, RI](("foo": RSM) -> (5: RI), ("bar": RSM) -> (10: RI))

    val json = Json.toJson(map)
    json.as[RMAP] shouldBe map

  }

}
