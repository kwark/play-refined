package be.venneborg.refined.play

import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.routing.Router.Routes
import play.core.server.NettyServer

import scala.concurrent.Future

object Action extends ActionBuilder[Request] {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = block(request)
}

object SIRDHelper {

  val jsonParser: BodyParser[JsValue] = BodyParsers.parse.json

  def shutdown(): Future[Unit] = Future.successful(())

  def startWithRoutes(routes: Routes): NettyServer = NettyServer.fromRouter()(routes)

  def reqHeaderAt(_method: String, _uri: String) = new RequestHeader {

    def method: String = _method

    def headers: Headers = Headers()

    def version: String = ""

    override def id = ???

    override def tags = Map.empty

    override def uri = _uri

    override def path = _uri.dropWhile(_ != '/').takeWhile(_ != '?')

    override def queryString =
      if (_uri.contains("?")) {
        val regex = """(\w+)=([^\s]+)?""".r
        Map(_uri.split('?').drop(1)(0).split('&').toSeq.map {
          case regex(k, v) => k -> (if (v != null) v else "")
        }: _*
        ).mapValues(List(_))
      } else Map.empty


    override def remoteAddress = ???

    override def secure = false

    override def clientCertificateChain = None
  }


}
