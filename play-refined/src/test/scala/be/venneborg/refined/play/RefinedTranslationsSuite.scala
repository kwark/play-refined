package be.venneborg.refined.play

import eu.timepit.refined.W
import eu.timepit.refined.api.{RefType, Refined, Validate}
import eu.timepit.refined.collection.{MaxSize, MinSize, NonEmpty, Size}
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.numeric.Interval.{Closed, ClosedOpen, Open}
import eu.timepit.refined.numeric.{Greater, GreaterEqual, Negative, NonNegative, NonPositive, Positive}
import eu.timepit.refined.string.{EndsWith, MatchesRegex, StartsWith, Url, Uuid}
import org.scalatest.{FunSuite, Matchers}

class RefinedTranslationsSuite extends FunSuite with Matchers {

  test("string translations") {
    //Predicate isEmpty() did not fail.
    checkTranslation[String, NonEmpty]("", "error.required", Nil)

    //Uuid predicate failed: Invalid UUID string: foo
    checkTranslation[String, Uuid]("foo bar", "error.uuid", Nil)

    //Predicate failed: "foo".startsWith("abc").
    checkTranslation[String, StartsWith[W.`"abc"`.T]]("foo", "error.invalid", Nil)

    //Predicate failed: "foo".endsWith("abc").
    checkTranslation[String, EndsWith[W.`"abc"`.T]]("foo", "error.invalid", Nil)

    //Url predicate failed: no protocol: foo
    checkTranslation[String, Url]("foo", "error.invalid", Seq("no protocol"))

    //Predicate failed: "9".matches("\d\d").
    checkTranslation[String, MatchesRegex[W.`"\\\\d\\\\d"`.T]]("9", "error.pattern", Seq("\\d\\d"))
  }

  test("string size translations") {
    // Predicate taking size(foo) = 3 failed: Predicate (3 < 5) did not fail.
    checkTranslation[String, MinSize[W.`5`.T]]("foo", "error.minLength", Seq("5"))

    //Predicate taking size(foo) = 3 failed: Predicate (3 > 2) did not fail.
    checkTranslation[String, MaxSize[W.`2`.T]]("foo", "error.maxLength", Seq("2"))

    //Predicate taking size(foo) = 3 failed: Predicate failed: (3 == 10).
    checkTranslation[String, Size[Equal[W.`10`.T]]]("foo", "error.length", Seq("10"))

    //Predicate taking size(foobarbaz) = 9 failed: Right predicate of (!(9 < 5) && !(9 > 6)) failed: Predicate (9 > 6) did not fail.
    checkTranslation[String, Size[Closed[W.`5`.T, W.`6`.T]]]("foobarbaz", "error.maxLength", Seq("6"))

    //Predicate taking size(foo) = 3 failed: Left predicate of (!(3 < 5) && !(3 > 6)) failed: Predicate (3 < 5) did not fail.
    checkTranslation[String, Size[Closed[W.`5`.T, W.`6`.T]]]("foo", "error.minLength", Seq("5"))

    //    checkTranslation[String, Size[Open[W.`5`.T, W.`7`.T]]]("foobarbaz", "error.maxLength", Seq("6"))
  }

  test("numeric translations") {
    //Predicate failed: (0 > 0).
    checkTranslation[Int, Positive](0, "error.min.strict", Seq("0"))

    //Predicate (-1 < 0) did not fail.
    checkTranslation[Int, NonNegative](-1, "error.min", Seq("0"))

    //Predicate failed: (0 < 0).
    checkTranslation[Int, Negative](0, "error.max.strict", Seq("0"))

    //Predicate (1 > 0) did not fail.
    checkTranslation[Int, NonPositive](1, "error.max", Seq("0"))

    //Predicate failed: (2 > 5).
    checkTranslation[Int, Greater[W.`5`.T]](2, "error.min.strict", Seq("5"))

    //Predicate failed: (-11 > -10).
    checkTranslation[Int, Greater[W.`-10`.T]](-11, "error.min.strict", Seq("-10"))

    //Predicate (2 < 5) did not fail.
    checkTranslation[Int, GreaterEqual[W.`5`.T]](2, "error.min", Seq("5"))

    //Predicate (-11 < -10) did not fail.
    checkTranslation[Int, GreaterEqual[W.`-10`.T]](-11, "error.min", Seq("-10"))
  }

  def checkTranslation[T, P](value: T, expectedMessage: String, expectedArgs: Seq[Any])
                      (implicit reftype: RefType[Refined],
                       validate: Validate[T, P]) = {
    val (message, args) = RefinedTranslations.translate(reftype.refine[P](value).left.get)
    message shouldBe expectedMessage
    args shouldBe expectedArgs
  }

}
