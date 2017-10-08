package be.venneborg.refined.play

import eu.timepit.refined.W
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.{Greater, Negative, Positive}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

import scala.language.higherKinds

class RefinedPathBindersSuite extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  import RefinedPathBinders._

  test("check refined bind/unbind") {

    forAll(genTestClass, maxDiscardedFactor(100.0)) { tc =>

      refinedStringPathBinder[NonEmpty].unbind("rs", tc.rs) shouldBe tc.rs.value

      val rsResult = refinedStringPathBinder[NonEmpty].bind("rs", tc.rs.value)
      rsResult.isRight shouldBe true
      rsResult.right.get shouldBe tc.rs

      refinedNumberPathBinder[Int, Positive].unbind("ri", tc.ri) shouldBe tc.ri.value.toString

      val riResult = refinedNumberPathBinder[Int, Positive].bind("ri", tc.ri.value.toString)
      riResult.isRight shouldBe true
      riResult.right.get shouldBe tc.ri

    }
  }

}
