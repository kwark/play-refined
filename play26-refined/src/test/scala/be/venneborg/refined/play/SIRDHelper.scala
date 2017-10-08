package be.venneborg.refined.play

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import play.api.libs.json.JsValue
import play.api.libs.typedmap.TypedMap
import play.api.libs.ws.JsonBodyWritables
import play.api.mvc.request.RequestTarget
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

  object Action extends DefaultActionBuilder {
    override def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    override def parser: BodyParser[AnyContent] = SIRDHelper.parsers.default
    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = block(request)
  }

object SIRDHelper {

  implicit val actorSystem = ActorSystem()
  private implicit val mat: Materializer = ActorMaterializer()

  val parsers: PlayBodyParsers = PlayBodyParsers()
  val jsonParser: BodyParser[JsValue] = parsers.json

  implicit val jsValueBodyWritable = JsonBodyWritables.writeableOf_JsValue

  def shutdown(): Future[Any] = actorSystem.terminate()

  def reqHeaderAt(_method: String, _uri: String) = new RequestHeader {

    def method: String = _method
    def headers: Headers = Headers()
    def version: String = ""
    override def connection = null

    override def target = RequestTarget(
      _uri,
      _uri.dropWhile(_ != '/').takeWhile(_ != '?'),
      {
        if (_uri.contains("?")) {
          val regex = """(\w+)=([^\s]+)?""".r
          Map(_uri.split('?').drop(1)(0).split('&').toSeq.map {
            case regex(k, v) => k -> (if (v != null) v else "")
          }: _*
          ).mapValues(List(_))
        } else Map.empty
      }
    )

    override def attrs = TypedMap.empty
  }


}
