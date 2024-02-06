package Protocol

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Protocol {

  val httpProtocol = http
    .baseUrl("https://api.demoblaze.com")
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())

}


