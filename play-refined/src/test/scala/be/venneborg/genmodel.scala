package be.venneborg

import be.venneborg.model._
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.numeric.{Greater, Negative, Positive}
import org.scalacheck.Gen
import eu.timepit.refined.W
import eu.timepit.refined.api.{RefType, Refined}
import eu.timepit.refined.boolean._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._


object genmodel {

  def genRSM(implicit reftype: RefType[Refined]) = Gen.chooseNum(1, 10).flatMap(Gen.listOfN(_, Gen.alphaChar)).map(_.mkString).map(reftype.refine[NonEmptySmall](_).right.get)
  def genRS(implicit reftype: RefType[Refined])  = Gen.nonEmptyListOf[Char](Gen.alphaChar).map(_.mkString).map(reftype.refine[NonEmpty](_).right.get)
  def genRI(implicit reftype: RefType[Refined])  = Gen.posNum[Int].map(reftype.refine[Positive](_).right.get)
  def genRL(implicit reftype: RefType[Refined])  = Gen.negNum[Long].map(reftype.refine[Negative](_).right.get)
  def genRD(implicit reftype: RefType[Refined])  = Gen.chooseNum[Double](12.0, Double.MaxValue).map(reftype.refine[Greater[W.`10.0`.T]](_).right.get)

  val genTestClass: Gen[TestClass] = for {
    rs  <- genRS
    ors <- Gen.option(genRS)
    rsm  <- genRSM
    orsm <- Gen.option(genRSM)
    ri  <- genRI
    ori <- Gen.option(genRI)
    rl  <- genRL
    orl <- Gen.option(genRL)
    rd  <- genRD
    ord <- Gen.option(genRD)
  } yield TestClass(rs, ors, rsm, orsm, ri, ori, rl, orl, rd, ord)


}
