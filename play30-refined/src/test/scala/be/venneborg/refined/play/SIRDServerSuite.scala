package be.venneborg.refined.play

import be.venneborg.model._
import SIRDHelper._
import eu.timepit.refined.auto._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import com.typesafe.sslconfig.ssl.DisabledComplainingHostnameVerifier
import play.api.libs.json._
import play.api.libs.ws.ahc.AhcWSClient
import play.api.data.FormBinding.Implicits._
import play.api.mvc._
import play.api.routing.sird._
import play.core.server.Server
import org.scalatest.matchers.should.Matchers
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterAll
import play.api.mvc

class SIRDServerSuite extends AnyFunSuite with ScalaFutures with Matchers with BeforeAndAfterAll {

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
    wsClient.url(s"http://localhost:$port/strings/foo").get().futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/strings/").get().futureValue.status shouldBe 404
  }

  test("refined string in query") {
    wsClient.url(s"http://localhost:$port/query?s=foo").get().futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/query?s=").get().futureValue.status shouldBe 404
  }

  test("refined int in path") {
    wsClient.url(s"http://localhost:$port/ints/5").get().futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/ints/-5").get().futureValue.status shouldBe 404
    wsClient.url(s"http://localhost:$port/ints/foo").get().futureValue.status shouldBe 404
  }

  test("refined int in query") {
    wsClient.url(s"http://localhost:$port/query?i=5").get().futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/query?i=foo").get().futureValue.status shouldBe 404
    wsClient.url(s"http://localhost:$port/query?i=-5").get().futureValue.status shouldBe 404
  }

  test("refined long in path") {
    wsClient.url(s"http://localhost:$port/longs/-5").get().futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/longs/5").get().futureValue.status shouldBe 404
    wsClient.url(s"http://localhost:$port/longs/foo").get().futureValue.status shouldBe 404
  }

  test("refined long in query") {
    wsClient.url(s"http://localhost:$port/query?l=-5").get().futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/query?l=foo").get().futureValue.status shouldBe 404
    wsClient.url(s"http://localhost:$port/query?l=5").get().futureValue.status shouldBe 404
  }

  test("refined double in path") {
    wsClient.url(s"http://localhost:$port/doubles/12.0").get().futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/doubles/5.0").get().futureValue.status shouldBe 404
    wsClient.url(s"http://localhost:$port/doubles/foo").get().futureValue.status shouldBe 404
  }

  test("refined double in query") {
    wsClient.url(s"http://localhost:$port/query?d=12.0").get().futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/query?d=foo").get().futureValue.status shouldBe 404
    wsClient.url(s"http://localhost:$port/query?d=5.0").get().futureValue.status shouldBe 404
  }

  test("post refined form") {
    wsClient.url(s"http://localhost:$port/form").post(tc.asMap).futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/form").post(tc.asMap - "rs").futureValue.status shouldBe 400
  }

  test("post refined json") {
    import RefinedJsonFormats._
    implicit val tcFormat = Json.format[TestClass]

    wsClient.url(s"http://localhost:$port/json").post(Json.toJson(tc)).futureValue.status shouldBe 200
    wsClient.url(s"http://localhost:$port/json").post(Json.toJson(tc).transform((__ \ 'rs).json.prune).get).futureValue.status shouldBe 400
  }

  var server: Option[Server] = None
  def port: Int = server.flatMap(_.httpPort).getOrElse(9000)
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
