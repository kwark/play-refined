package be.venneborg.refined.play

import be.venneborg.model._
import SIRDHelper.reqHeaderAt

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.{ActorMaterializer, Materializer}
import play.api.routing.sird._
import play.api.routing._

import play.api.http.HttpVerbs
import play.api.mvc.Results
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SIRDRoutingSuite extends AnyFunSuite with Matchers {

  import RefinedPathBinders._

  test("refined string routing") {
    val extractor: PathBindableExtractor[RS] = new PathBindableExtractor[RS]

    val router: Router = Router.from {
      case GET(p"/strings/${extractor(_)}") => Action { Results.Ok }
    }
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/strings/foo")) shouldBe true
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/strings/")) shouldBe false
  }

  test("refined string query") {
    val extractor: PathBindableExtractor[RS] = new PathBindableExtractor[RS]

    val router: Router = Router.from {
      case GET(p"/query" ? q"q=${extractor(_)}") => Action { Results.Ok }
    }
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=5")) shouldBe true
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=")) shouldBe false
  }


  test("refined int routing") {
    val extractor: PathBindableExtractor[RI] = new PathBindableExtractor[RI]

    val router: Router = Router.from {
      case GET(p"/ints/${extractor(_)}") => Action { Results.Ok }
    }
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/ints/5")) shouldBe true
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/ints/-5")) shouldBe false
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/ints/foo")) shouldBe false
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/ints/12.0")) shouldBe false
  }

  test("refined int query") {
    val extractor: PathBindableExtractor[RI] = new PathBindableExtractor[RI]

    val router: Router = Router.from {
      case GET(p"/query" ? q"q=${extractor(_)}") => Action { Results.Ok }
    }
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=5")) shouldBe true
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=foo")) shouldBe false
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=-5")) shouldBe false
  }

  test("refined long routing") {
    val extractor: PathBindableExtractor[RL] = new PathBindableExtractor[RL]

    val router: Router = Router.from {
      case GET(p"/longs/${extractor(_)}") => Action { Results.Ok }
    }
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/longs/5")) shouldBe false
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/longs/-5")) shouldBe true
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/longs/foo")) shouldBe false
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/longs/12.0")) shouldBe false
  }

  test("refined long query") {
    val extractor: PathBindableExtractor[RL] = new PathBindableExtractor[RL]

    val router: Router = Router.from {
      case GET(p"/query" ? q"q=${extractor(_)}") => Action { Results.Ok }
    }
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=-5")) shouldBe true
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=foo")) shouldBe false
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=5")) shouldBe false
  }

  test("refined double routing") {
    val extractor: PathBindableExtractor[RD] = new PathBindableExtractor[RD]

    val router: Router = Router.from {
      case GET(p"/doubles/${extractor(_)}") => Action { Results.Ok }
    }
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/doubles/15.0")) shouldBe true
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/doubles/0.0")) shouldBe false
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, s"/doubles/foo")) shouldBe false
  }

  test("refined double query") {
    val extractor: PathBindableExtractor[RD] = new PathBindableExtractor[RD]

    val router: Router = Router.from {
      case GET(p"/query" ? q"q=${extractor(_)}") => Action { Results.Ok }
    }
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=12.0")) shouldBe true
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=foo")) shouldBe false
    router.routes.isDefinedAt(reqHeaderAt(HttpVerbs.GET, "/query?q=0.0")) shouldBe false
  }

}
