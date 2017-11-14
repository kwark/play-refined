package be.venneborg.refined.play

import be.venneborg.model._
import be.venneborg.genmodel._
import org.scalatest.{FunSuite, Matchers}
import play.api.libs.json.{Json, OFormat}
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

}
