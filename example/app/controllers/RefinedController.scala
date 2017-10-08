package controllers

import be.venneborg.refined.play.{RefinedFormsSuite, TestClass, _}
import play.api.libs.json.{JsError, Json}
import play.api.mvc._

class RefinedController(override val controllerComponents: ControllerComponents)
  extends AbstractController(controllerComponents: ControllerComponents) {

  def string(v: RS) = Action { Ok(v.value) }
  def int(v: RI)    = Action { Ok(v.value.toString) }
  def long(v: RL)   = Action { Ok(v.value.toString) }
  def double(v: RD) = Action { Ok(v.value.toString) }

  def queryRS(v: RS) = Action { Ok(v.value) }
  def queryRI(v: RI) = Action { Ok(v.value.toString) }
  def queryRL(v: RL) = Action { Ok(v.value.toString) }
  def queryRD(v: RD) = Action { Ok(v.value.toString) }

  def form() = Action { implicit request =>
    RefinedFormsSuite.tcForm.bindFromRequest().fold(
      form => BadRequest(form.errors.map(_.message).mkString(",")),
      tc   => Ok(tc.toString)
    )
  }

  def json() = Action(parse.json) { implicit request =>
    import RefinedJsonFormats._
    implicit val tcFormat = Json.format[TestClass]

    request.body.validate[TestClass].fold(
      errors => BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))),
      tc     => Ok(Json.obj("status" ->"OK", "result" -> tc.toString))
    )
  }

}
