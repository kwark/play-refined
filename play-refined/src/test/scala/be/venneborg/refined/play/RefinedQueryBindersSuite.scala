package be.venneborg.refined.play

import eu.timepit.refined.W
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.{Greater, Negative, Positive}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}
import play.api.mvc.QueryStringBindable

import scala.language.higherKinds

class RefinedQueryBindersSuite extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  import RefinedQueryBinders._

  test("check refined bind/unbind") {

    forAll(genTestClass) { tc =>

      refinedStringQueryBinder[NonEmpty].unbind("rs", tc.rs).length should be > 4

      val rsResult = refinedStringQueryBinder[NonEmpty].bind("rs", Map("rs" -> Seq(tc.rs.value)))
      rsResult.isDefined shouldBe true
      rsResult.get.isRight shouldBe true
      rsResult.get.right.get shouldBe tc.rs

      refinedNumberQueryBinder[Int, Positive].unbind("ri", tc.ri).length should be > 3

      val riResult = refinedNumberQueryBinder[Int, Positive].bind("ri", Map("ri" -> Seq(tc.ri.value.toString)))
      riResult.isDefined shouldBe true
      riResult.get.isRight shouldBe true
      riResult.get.right.get shouldBe tc.ri

    }
  }

  val testClassBinder = new QueryStringBindable[TestClass] {

    import QueryStringBindable._

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, TestClass]] = {
      for {
        rs  <- refinedStringQueryBinder[NonEmpty].bind("rs", params)
        ors <- bindableOption(refinedStringQueryBinder[NonEmpty]).bind("ors", params)
        ri  <- refinedNumberQueryBinder[Int, Positive].bind("ri", params)
        ori <- bindableOption(refinedNumberQueryBinder[Int, Positive]).bind("ori", params)
        rl  <- refinedNumberQueryBinder[Long, Negative].bind("rl", params)
        orl <- bindableOption(refinedNumberQueryBinder[Long, Negative]).bind("orl", params)
        rd  <- refinedNumberQueryBinder[Double, Greater[W.`10.0`.T]].bind("rd", params)
        ord <- bindableOption(refinedNumberQueryBinder[Double, Greater[W.`10.0`.T]]).bind("ord", params)
      } yield {
        (rs, ors, ri, ori, rl, orl, rd, ord) match {
          case (Right(rs), Right(ors), Right(ri), Right(ori), Right(rl), Right(orl), Right(rd), Right(ord)) =>
            Right(TestClass(rs, ors, ri, ori, rl, orl, rd, ord))
          case _ =>
            Left("Unable to bind a TestClass")
        }
      }
    }

    override def unbind(key: String, ageRange: TestClass): String = ???

  }

  test("bind testclass") {

    forAll(genTestClass) { tc =>

      val r = testClassBinder.bind("", Map(
        "rs"  -> Seq(tc.rs.value),
        "ors" -> tc.ors.map(v => Seq(v.value)).getOrElse(Nil),
        "ri"  -> Seq(tc.ri.value.toString),
        "ori" -> tc.ori.map(v => Seq(v.value.toString)).getOrElse(Nil),
        "rl"  -> Seq(tc.rl.value.toString),
        "orl" -> tc.orl.map(v => Seq(v.value.toString)).getOrElse(Nil),
        "rd"  -> Seq(tc.rd.value.toString),
        "ord" -> tc.ord.map(v => Seq(v.value.toString)).getOrElse(Nil)
      ))

      r.isDefined shouldBe true
      r.get.isRight shouldBe true
      r.get.right.get shouldBe tc

    }
  }

}
