package script

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import _root_.script.test._

class simulation extends  Simulation {

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

}
