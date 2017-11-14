package be.venneborg.refined.play

import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.{Negative, Positive}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import play.api.data.{Form, Forms}
import play.api.data.Forms.{mapping, optional}
import be.venneborg.model._
import be.venneborg.genmodel._

import scala.language.higherKinds

class RefinedFormsSuite extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  import RefinedForms._

  test("check refined bind/unbind") {

    forAll(genTestClass, maxDiscardedFactor(100.0)) { tc =>

      val rsResult = refinedStringFormat[NonEmpty].bind("rs", refinedStringFormat[NonEmpty].unbind("rs", tc.rs))
      rsResult.isRight shouldBe true
      rsResult.right.get shouldBe tc.rs

      val riResult = refinedIntFormat[Positive].bind("ri", refinedIntFormat[Positive].unbind("ri", tc.ri))
      riResult.isRight shouldBe true
      riResult.right.get shouldBe tc.ri

      val rlResult = refinedLongFormat[Negative].bind("rl", refinedLongFormat[Negative].unbind("rl", tc.rl))
      rlResult.isRight shouldBe true
      rlResult.right.get shouldBe tc.rl

    }
  }

  test("refined form fill/bind") {
    import RefinedFormsSuite._

    forAll(genTestClass, maxDiscardedFactor(100.0)) { tc =>
      tcForm.bind(tcForm.fill(tc).data).value shouldBe Some(tc)
    }
  }

}

object RefinedFormsSuite {

  import RefinedForms._

  val tcForm: Form[TestClass] = Form(
    mapping(
      "rs"  -> Forms.of[RS],
      "ors" -> optional(Forms.of[RS]),
      "rsm"  -> Forms.of[RSM],
      "orsm" -> optional(Forms.of[RSM]),
      "ri"  -> Forms.of[RI],
      "ori" -> optional(Forms.of[RI]),
      "rl"  -> Forms.of[RL],
      "orl" -> optional(Forms.of[RL]),
      "rd"  -> Forms.of[RD],
      "ord" -> optional(Forms.of[RD])
    )(TestClass.apply)(TestClass.unapply)
  )

}


