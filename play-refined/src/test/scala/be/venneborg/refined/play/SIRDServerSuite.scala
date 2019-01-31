package be.venneborg.refined.play

import be.venneborg.model._
import SIRDHelper._
import eu.timepit.refined.auto._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}
import play.api.libs.json._
import play.api.libs.ws.ahc.AhcWSClient
import play.api.mvc._
import play.api.routing.sird._
import play.core.server.Server

class SIRDServerSuite extends FunSuite with ScalaFutures with Matchers with BeforeAndAfterAll {

  import RefinedPathBinders._

  val rsExtractor: PathBindableExtractor[RS] = new PathBindableExtractor[RS]
  val riExtractor: PathBindableExtractor[RI] = new PathBindableExtractor[RI]
  val rlExtractor: PathBindableExtractor[RL] = new PathBindableExtractor[RL]
  val rdExtractor: PathBindableExtractor[RD] = new PathBindableExtractor[RD]

  val tc = TestClass("foo", Some("bar"), "baz", Some("foobar"), 5, Some(Int.MaxValue), Long.MinValue, Some(-1L), 12.0, Some(Double.MaxValue))

  val routes: PartialFunction[play.api.mvc.RequestHeader,play.api.mvc.Handler] = {
    // path bindables
    case GET(p"/strings/${rsExtractor(v)}")      => Action { Results.Ok(v.value) }
    case GET(p"/ints/${riExtractor(v)}")         => Action { Results.Ok(v.value.toString) }
    case GET(p"/longs/${rlExtractor(v)}")        => Action { Results.Ok(v.value.toString) }
    case GET(p"/doubles/${rdExtractor(v)}")      => Action { Results.Ok(v.value.toString) }

    // query strings
    case GET(p"/query" ? q"s=${rsExtractor(v)}") => Action { Results.Ok(v.value) }
    case GET(p"/query" ? q"i=${riExtractor(v)}") => Action { Results.Ok(v.value.toString) }
    case GET(p"/query" ? q"l=${rlExtractor(v)}") => Action { Results.Ok(v.value.toString) }
    case GET(p"/query" ? q"d=${rdExtractor(v)}") => Action { Results.Ok(v.value.toString) }

    // form handling
    case POST(p"/form") => Action { implicit request =>
      RefinedFormsSuite.tcForm.bindFromRequest().fold(
        form => Results.BadRequest(form.errors.map(_.message).mkString(",")),
        tc   => Results.Ok(tc.toString)
      )
    }

    // json handling
    case POST(p"/json") => Action(jsonParser) { implicit request =>
      import RefinedJsonFormats._
      implicit val tcFormat = Json.format[TestClass]

      request.body.validate[TestClass].fold(
        errors => Results.BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))),
        tc     => Results.Ok(Json.obj("status" ->"OK", "result" -> tc.toString))
      )
    }
  }

  test("refined string in path") {
    val value = wsClient.url("http://localhost:9000/strings/foo").get().futureValue
    println(value)
    value.status shouldBe 200
    wsClient.url("http://localhost:9000/strings/").get().futureValue.status shouldBe 404
  }

  test("refined string in query") {
    wsClient.url("http://localhost:9000/query?s=foo").get().futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/query?s=").get().futureValue.status shouldBe 404
  }

  test("refined int in path") {
    wsClient.url("http://localhost:9000/ints/5").get().futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/ints/-5").get().futureValue.status shouldBe 404
    wsClient.url("http://localhost:9000/ints/foo").get().futureValue.status shouldBe 404
  }

  test("refined int in query") {
    wsClient.url("http://localhost:9000/query?i=5").get().futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/query?i=foo").get().futureValue.status shouldBe 404
    wsClient.url("http://localhost:9000/query?i=-5").get().futureValue.status shouldBe 404
  }

  test("refined long in path") {
    wsClient.url("http://localhost:9000/longs/-5").get().futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/longs/5").get().futureValue.status shouldBe 404
    wsClient.url("http://localhost:9000/longs/foo").get().futureValue.status shouldBe 404
  }

  test("refined long in query") {
    wsClient.url("http://localhost:9000/query?l=-5").get().futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/query?l=foo").get().futureValue.status shouldBe 404
    wsClient.url("http://localhost:9000/query?l=5").get().futureValue.status shouldBe 404
  }

  test("refined double in path") {
    wsClient.url("http://localhost:9000/doubles/12.0").get().futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/doubles/5.0").get().futureValue.status shouldBe 404
    wsClient.url("http://localhost:9000/doubles/foo").get().futureValue.status shouldBe 404
  }

  test("refined double in query") {
    wsClient.url("http://localhost:9000/query?d=12.0").get().futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/query?d=foo").get().futureValue.status shouldBe 404
    wsClient.url("http://localhost:9000/query?d=5.0").get().futureValue.status shouldBe 404
  }

  test("post refined form") {
    wsClient.url("http://localhost:9000/form").post(tc.asMap).futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/form").post(tc.asMap - "rs").futureValue.status shouldBe 400
  }

  test("post refined json") {
    import RefinedJsonFormats._
    implicit val tcFormat = Json.format[TestClass]

    wsClient.url("http://localhost:9000/json").post(Json.toJson(tc)).futureValue.status shouldBe 200
    wsClient.url("http://localhost:9000/json").post(Json.toJson(tc).transform((__ \ 'rs).json.prune).get).futureValue.status shouldBe 400
  }

  var server: Option[Server] = None
  var wsClient: AhcWSClient = _

  override protected def beforeAll() = {
    wsClient = AhcWSClient()(null) //no need for a materializer
    server = Some(startWithRoutes(routes))
  }

  override protected def afterAll() = {
    wsClient.close()
    server.foreach(_.stop())
    shutdown().futureValue
  }

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(Span(5, Seconds))

}
