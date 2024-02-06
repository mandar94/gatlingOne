package variables

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ChainBuilder
import scala.util.Random
import java.util.Properties
import scala.io.Source

object Variables {

  val uri1 = "https://passwordsleakcheck-pa.googleapis.com/v1/leaks:lookupSingle"
  val uri2 = "https://www.demoblaze.com"


 // generate sessionID

 /*def c_sessionID(): String = {
   val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') :+ '-'
   (1 to 16).map(_ => chars(Random.nextInt(chars.length))).mkString
 }*/
}
