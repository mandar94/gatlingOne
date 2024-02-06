package simulation

import Protocol.Protocol.httpProtocol
import io.gatling.core.Predef._
import Scenario.Scenario._

class simulate extends Simulation{

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

}
