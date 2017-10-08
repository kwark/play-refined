package global

import controllers.RefinedController
import play.api.{ApplicationLoader, BuiltInComponentsFromContext}
import router.Routes

class MyApplicationLoader extends ApplicationLoader {

  override def load(context: ApplicationLoader.Context) = new MyComponents(context).application
}

class MyComponents(context: ApplicationLoader.Context) extends BuiltInComponentsFromContext(context) {

  lazy val refinedController: RefinedController = new RefinedController(controllerComponents)

  lazy val router = new Routes(httpErrorHandler, refinedController)

  val httpFilters = Seq.empty
}

