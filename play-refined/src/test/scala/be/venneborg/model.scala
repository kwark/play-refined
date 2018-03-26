package be.venneborg

import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._

object model {

  type NonEmptySmall = NonEmpty And MaxSize[W.`10`.T]

  type RS = String Refined NonEmpty
  type RSM = String Refined NonEmptySmall
  type RI = Int Refined Positive
  type RL = Long Refined Negative
  type RD = Double Refined Greater[W.`10.0`.T]

  type RMAP = Map[RSM, RI]

  case class TestClass(rs: RS, ors: Option[RS],
                       rsm: RSM, orsm: Option[RSM],
                       ri: RI, ori: Option[RI],
                       rl: RL, orl: Option[RL],
                       rd: RD, ord: Option[RD]) {

    lazy val asMap: Map[String, Seq[String]] = {
      var map: Map[String, Seq[String]] = Map.empty
      map += "rs" -> Seq(rs.value)
      map += "ors" -> ors.map(v => Seq(v.value)).getOrElse(Seq.empty[String])
      map += "rsm" -> Seq(rsm.value)
      map += "orsm" -> orsm.map(v => Seq(v.value)).getOrElse(Seq.empty[String])
      map += "ri" -> Seq(ri.value.toString)
      map += "ori" -> ori.map(v => Seq(v.value.toString)).getOrElse(Seq.empty[String])
      map += "rl" -> Seq(rl.value.toString)
      map += "orl" -> orl.map(v => Seq(v.value.toString)).getOrElse(Seq.empty[String])
      map += "rd" -> Seq(rd.value.toString)
      map += "ord" -> ord.map(v => Seq(v.value.toString)).getOrElse(Seq.empty[String])
      map
    }
  }

}
