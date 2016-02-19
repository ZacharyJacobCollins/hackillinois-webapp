import java.nio.charset.StandardCharsets
import java.net.URLEncoder

import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._

import org.json4s._
import org.json4s.native.JsonMethods._

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.routing._
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.httpx.Json4sSupport

object Config {
  val clientId = ""
  val clientSecret = ""
  val host = "localhost"
  val port = 18888
  val callbackUrl = s"http://$host:$port/authenticate"
}

object Login extends App {
  import spray.can.Http

  implicit val system = ActorSystem("login")

  val service = system.actorOf(Props[LoginService], "login-service")

  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = Config.port)
}

class LoginService extends HttpServiceActor with ActorLogging {

  override def actorRefFactory = context

  val login = new LoginHttpService {
    def actorRefFactory = context
  }

  def receive = runRoute(
    login.routes 
    ~ get {
      respondWithMediaType(`text/html`)  {
        complete("""<a href="/login">Login with Climate</a>""")
      }
    }
  )
}

trait LoginHttpService extends HttpService with Json4sSupport {
  import scalaj.http._

  implicit def json4sFormats = DefaultFormats

  val routes = login ~ authenticate

  def login = get {
    path("login") {
      val params = Map("mobile" -> "true",
                       "page" -> "oidcauthn",
                       "response_type" -> "code",
                       "redirect_uri" -> Config.callbackUrl,
                       "scope" -> "openid user",
                       "client_id" -> Config.clientId)
      
      var query = params.map {
        case (key, value) => s"$key=${URLEncoder.encode(value, "UTF-8")}"
      }.mkString("&")

      redirect(s"https://climate.com/static/app-login/index.html?$query", Found)
    }
  }

  def authenticate = get {
    path("authenticate") {
      parameters('code) { code =>
        getToken(code) match {
          case Success((accessToken, userId)) => {
            // persist access token for user
            complete("success")
          }
          case Failure(ex) => complete(500, ex.getMessage)
        }
      }
    }
  }

  private def getToken(code:String) = Try {
    val params = Seq("grant_type" -> "authorization_code",
                     "scope" -> "openid user",
                     "redirect_uri" -> Config.callbackUrl,
                     "code" -> code)
    
    val credentials = s"${Config.clientId}:${Config.clientSecret}"
    val token = Base64.encodeString(credentials)

    val response = Http("https://climate.com/api/oauth/token")
      .header("Authorization", s"Basic $token")
      .postForm(params)
      .asString

    response.code match {
      case 200 => {
        val json = parse(response.body)
        val JString(accessToken) = json \ "access_token"
        val JInt(userId) = json \ "user" \ "id"
        (accessToken, userId)
      }
      case code => throw new IllegalStateException(s"Unexpected response $code")
    }
  }
}
