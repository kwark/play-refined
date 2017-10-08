package be.venneborg.refined

import eu.timepit.refined.api.{RefType, Refined, Validate}
import eu.timepit.refined.W
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.{Greater, Negative, Positive}
import eu.timepit.refined.scalacheck.any.arbitraryFromValidate
import org.scalacheck.Gen


package object play {

  type RS = String Refined NonEmpty
  type RI = Int Refined Positive
  type RL = Long Refined Negative
  type RD = Double Refined Greater[W.`10.0`.T]

  case class TestClass(rs: RS, ors: Option[RS],
                       ri: RI, ori: Option[RI],
                       rl: RL, orl: Option[RL],
                       rd: RD, ord: Option[RD]) {

    lazy val asMap: Map[String, Seq[String]] = {
      var map: Map[String, Seq[String]] = Map.empty
      map += "rs" -> Seq(rs.value)
      map += "ors" -> ors.map(v => Seq(v.value)).getOrElse(Seq.empty[String])
      map += "ri" -> Seq(ri.value.toString)
      map += "ori" -> ori.map(v => Seq(v.value.toString)).getOrElse(Seq.empty[String])
      map += "rl" -> Seq(rl.value.toString)
      map += "orl" -> orl.map(v => Seq(v.value.toString)).getOrElse(Seq.empty[String])
      map += "rd" -> Seq(rd.value.toString)
      map += "ord" -> ord.map(v => Seq(v.value.toString)).getOrElse(Seq.empty[String])
      map
    }
  }

  def genRS(implicit reftype: RefType[Refined]) = Gen.nonEmptyListOf[Char](Gen.alphaChar).map(_.toString).map(reftype.refine[NonEmpty](_).right.get)
  def genRI(implicit reftype: RefType[Refined]) = Gen.posNum[Int].map(reftype.refine[Positive](_).right.get)
  def genRL(implicit reftype: RefType[Refined]) = Gen.negNum[Long].map(reftype.refine[Negative](_).right.get)
  def genRD(implicit reftype: RefType[Refined]) = Gen.chooseNum[Double](12.0, Double.MaxValue).map(reftype.refine[Greater[W.`10.0`.T]](_).right.get)

  val genTestClass: Gen[TestClass] = for {
    rs  <- genRS
    ors <- Gen.option(genRS)
    ri  <- genRI
    ori <- Gen.option(genRI)
    rl  <- genRL
    orl <- Gen.option(genRL)
    rd  <- genRD
    ord <- Gen.option(genRD)
  } yield TestClass(rs, ors, ri, ori, rl, orl, rd, ord)


}
