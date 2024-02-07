package be.venneborg.refined.play

import be.venneborg.model._
import be.venneborg.genmodel._

import play.api.libs.json.{JsValue, Json, OFormat}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import scala.language.higherKinds
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class RefinedJsonFormatsSuite extends AnyFunSuite with Matchers with ScalaCheckPropertyChecks {

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
