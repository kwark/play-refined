package be.venneborg.refined.play

import be.venneborg.refined.play.RefinedTranslations.Error
import eu.timepit.refined.W
import eu.timepit.refined.api.{RefType, Refined, Validate}
import eu.timepit.refined.boolean.{AllOf, And, False, Not, True}
import eu.timepit.refined.collection.{Empty, MaxSize, MinSize, NonEmpty, Size}
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.numeric.Interval.{Closed, ClosedOpen, Open, OpenClosed}
import eu.timepit.refined.numeric.{Greater, GreaterEqual, Negative, NonNegative, NonPositive, Positive}
import eu.timepit.refined.string.{EndsWith, MatchesRegex, StartsWith, Url, Uuid}
import shapeless.{::, HNil}

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class RefinedTranslationsSuite extends AnyFunSuite with Matchers {

  test("string translations") {
    //Predicate isEmpty() did not fail.
    checkTranslation[String, NonEmpty]("", "error.required", Nil)

    //Predicate failed: isEmpty(foo).
    checkTranslation[String, Empty]("foo", "error.invalid", Seq("not empty"))

    //Uuid predicate failed: Invalid UUID string: foo
    checkTranslation[String, Uuid]("foo bar", "error.uuid", Nil)

    //Predicate failed: "foo".startsWith("abc").
    checkTranslation[String, StartsWith[W.`"abc"`.T]]("foo", "error.invalid", Seq("not starting with: abc"))

    //Predicate failed: "foo".endsWith("abc").
    checkTranslation[String, EndsWith[W.`"abc"`.T]]("foo", "error.invalid", Seq("not ending with: abc"))

    //Url predicate failed: URI is not absolute: foo
    checkTranslation[String, Url]("foo", "error.invalid", Seq("Url predicate failed: URI is not absolute"))

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

    //Predicate taking size(foobarbaz) = 9 failed: Right predicate of ((9 > 5) && (9 < 7)) failed: Predicate failed: (9 < 7).
    checkTranslation[String, Size[Open[W.`5`.T, W.`7`.T]]]("foobarbaz", "error.maxLength", Seq("6"))

    //Predicate taking size(fooba) = 5 failed: Left predicate of ((5 > 5) && (5 < 7)) failed: Predicate failed: (5 > 5).
    checkTranslation[String, Size[Open[W.`5`.T, W.`7`.T]]]("fooba", "error.minLength", Seq("6"))
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

  test("interval translations") {
    checkTranslation[Int, Closed[W.`1`.T, W.`5`.T]](0, "error.min", Seq("1"))
    checkTranslation[Int, Closed[W.`1`.T, W.`5`.T]](6, "error.max", Seq("5"))
    checkTranslation[Int, OpenClosed[W.`1`.T, W.`5`.T]](1, "error.min.strict", Seq("1"))
    checkTranslation[Int, ClosedOpen[W.`1`.T, W.`5`.T]](5, "error.max.strict", Seq("5"))
  }

  test("boolean translations") {
      //Predicate failed: false.
      checkTranslation[String, False]("whatever", "error.invalid", Seq("false"))

      //Predicate true did not fail.
      checkTranslation[String, Not[True]]("whatever", "error.invalid", Seq("true")) // ???
  }

  test("combination") {
    //Left predicate of ("foo".startsWith("@") && !(3 > 4)) failed: Predicate failed: "foo".startsWith("@").
    checkTranslation[String, StartsWith[W.`"@"`.T] And MaxSize[W.`4`.T]]("foo", "error.invalid", Seq("not starting with: @"))

    //Right predicate of ("@foobar".startsWith("@") && !(7 > 4)) failed: Predicate taking size(@foobar) = 7 failed: Predicate (7 > 4) did not fail.
    checkTranslation[String, StartsWith[W.`"@"`.T] And MaxSize[W.`4`.T]]("@foobar", "error.maxLength", Seq("4"))

    //Both predicates of ("foobar".startsWith("@") && !(6 > 4)) failed. Left: Predicate failed: "foobar".startsWith("@"). Right: Predicate taking size(foobar) = 6 failed: Predicate (6 > 4) did not fail.
    checkTranslation[String, StartsWith[W.`"@"`.T] And MaxSize[W.`4`.T]]("foobar",
      Seq(Error("error.invalid", Seq("not starting with: @")), Error("error.maxLength", Seq("4"))))

    //Predicate failed: ("foo".startsWith("@") && !(3 > 4) && true).
    checkTranslation[String, AllOf[StartsWith[W.`"@"`.T] :: MaxSize[W.`4`.T] :: HNil]]("foo", "error.invalid", Seq("""("foo".startsWith("@") && (!(3 < 0) && !(3 > 4)) && true)"""))

    //Predicate failed: ("foobar".startsWith("@") && !(6 > 4) && true).
    checkTranslation[String, AllOf[StartsWith[W.`"@"`.T] :: MaxSize[W.`4`.T] :: HNil]]("foobar", "error.invalid", Seq("""("foobar".startsWith("@") && (!(6 < 0) && !(6 > 4)) && true)"""))
  }

  def checkTranslation[T, P](value: T, expectedMessage: String, expectedArgs: Seq[String])
                      (implicit reftype: RefType[Refined],
                       validate: Validate[T, P]) = {
    val errors = RefinedTranslations.translate(reftype.refine[P](value).left.get)
    errors.nonEmpty shouldBe true
    val error = errors.head
    error.errorCode shouldBe expectedMessage
    error.args shouldBe expectedArgs
  }

  def checkTranslation[T, P](value: T, expectedErrors: Seq[Error])
                            (implicit reftype: RefType[Refined],
                             validate: Validate[T, P]) = {
    val errors = RefinedTranslations.translate(reftype.refine[P](value).left.get)
    errors should contain theSameElementsAs expectedErrors
  }

}
