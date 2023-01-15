package shared.http

import zhttp.http._
import zio.ZIO

object HealthRoutes {

  val routes: Http[Any, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "live" =>
        ZIO.succeed(Response(Status.Ok))
    }
}
