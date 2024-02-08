package be.venneborg.test

import be.venneborg.refined.play.RefinedJsonFormats._
import be.venneborg.model._
import eu.timepit.refined.auto._
import global.MyApplicationLoader
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.{BaseOneServerPerSuite, FakeApplicationFactory, WsScalaTestClient}
import play.api.libs.json.{Json, __}
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient
import play.api.{Application, ApplicationLoader, Environment}
import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuite

class PlayServerSuite extends AnyFunSuite with BaseOneServerPerSuite with FakeApplicationFactory with ScalaFutures with Matchers with BeforeAndAfterAll with WsScalaTestClient {

  val tc = TestClass("foo", Some("bar"), "baz", Some("foobar"), 5, Some(Int.MaxValue), Long.MinValue, Some(-1L), 12.0, Some(Double.MaxValue))

  test("refined string in path") {
    wsUrl("/strings/foo").get().futureValue.status shouldBe 200
    wsUrl("/strings/").get().futureValue.status shouldBe 404
  }

  test("refined int in path") {
    wsUrl("/ints/5").get().futureValue.status shouldBe 200
    wsUrl("/ints/-10").get().futureValue.status shouldBe 400
    wsUrl("/ints/foo").get().futureValue.status shouldBe 400
  }

  test("refined long in path") {
    wsUrl("/longs/-5").get().futureValue.status shouldBe 200
    wsUrl("/longs/5").get().futureValue.status shouldBe 400
    wsUrl("/longs/foo").get().futureValue.status shouldBe 400
  }

  test("refined double in path") {
    wsUrl("/doubles/12.0").get().futureValue.status shouldBe 200
    wsUrl("/doubles/5.0").get().futureValue.status shouldBe 400
    wsUrl("/doubles/foo").get().futureValue.status shouldBe 400
  }

  test("refined string in query") {
    wsUrl("/queryRS?s=foo").get().futureValue.status shouldBe 200
    wsUrl("/queryRS?s=").get().futureValue.status shouldBe 400
  }

  test("refined int in query") {
    wsUrl("/queryRI?i=5").get().futureValue.status shouldBe 200
    wsUrl("/queryRI?i=foo").get().futureValue.status shouldBe 400
    wsUrl("/queryRI?i=-5").get().futureValue.status shouldBe 400
    wsUrl("/queryRI?i=").get().futureValue.status shouldBe 400
  }

  test("refined long in query") {
    wsUrl("/queryRL?l=-5").get().futureValue.status shouldBe 200
    wsUrl("/queryRL?l=foo").get().futureValue.status shouldBe 400
    wsUrl("/queryRL?l=5").get().futureValue.status shouldBe 400
    wsUrl("/queryRL?l=").get().futureValue.status shouldBe 400
  }

  test("refined double in query") {
    wsUrl("/queryRD?d=12.0").get().futureValue.status shouldBe 200
    wsUrl("/queryRD?d=foo").get().futureValue.status shouldBe 400
    wsUrl("/queryRD?d=1.1111").get().futureValue.status shouldBe 400
    wsUrl("/queryRD?d=").get().futureValue.status shouldBe 400
  }

  test("post refined form") {
    wsUrl("/form").post(tc.asMap).futureValue.status shouldBe 200
    wsUrl("/form").post(tc.asMap - "rs").futureValue.status shouldBe 400
  }

  test("post refined json") {
    implicit val tcFormat = Json.format[TestClass]

    wsUrl("/json").post(Json.toJson(tc)).futureValue.status shouldBe 200
    wsUrl("/json").post(Json.toJson(tc).transform((__ \ 'rs).json.prune).get).futureValue.status shouldBe 400
  }

  implicit lazy val ws: WSClient = AhcWSClient()(null) //no materializer needed

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(Span(2, Seconds))

  private val context = ApplicationLoader.createContext(Environment.simple())

  override def fakeApplication(): Application = new MyApplicationLoader().load(context)
}
