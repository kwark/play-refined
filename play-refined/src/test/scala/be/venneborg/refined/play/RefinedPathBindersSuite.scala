package be.venneborg.refined.play

import be.venneborg.model._
import be.venneborg.genmodel._
import eu.timepit.refined.W
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.{Greater, Negative, Positive}
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.funsuite.AnyFunSuite

import scala.language.higherKinds

class RefinedPathBindersSuite extends AnyFunSuite with Matchers with ScalaCheckPropertyChecks {

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
