package org.sample

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import Headers.Headers._
import Protocol.Protocol._
import io.gatling.core.structure.ChainBuilder
import scala.util.Random
import java.util.UUID

class demoBlazeNew extends Simulation {


		val uri1 = "https://passwordsleakcheck-pa.googleapis.com/v1/leaks:lookupSingle"
    val uri2 = "https://www.demoblaze.com"

		val credentials = csv("data/credentials.csv").circular

	val scn = scenario("demoBlazeNew").repeat(1) {
		feed(credentials)
		.exec(http("T01_HOME")
			.get("/entries")
			.headers(headers_1)
			.check(jsonPath("""$.Items[*].cat""").findRandom.saveAs("c_category")))
			.pause(10)

			.exec(http("T02_LOGIN")
				.post("/login")
				.headers(headers_3)
				.body(StringBody("{\"username\":\"${p_username}\",\"password\":\"${p_password}\"}"))
				.check(regex("""Auth_token: (.*)"""").findRandom.saveAs("c_token")))

			//	.body(RawFileBody("org/sample/demoblazenew/0003_request.json")))
			.pause(1)

			.exec(http("request_4")
				.get("/entries")
				.headers(headers_1))

			.exec(http("request_5")
				.post("/check")
				.headers(headers_3)
				//.body(StringBody("{\"token\":\"YWJjZDExMkBnbWFpbC5jb20xNzA3Mzg1\"}")))
				.body(StringBody("{\"token\":\"${c_token}\"}")))
			//.body(RawFileBody("org/sample/demoblazenew/0005_request.json"))))
			.pause(1)


			.exec(http("T03_SELECT_CATEGORY")
				.post("/bycat")
				.headers(headers_3)
				.body(StringBody("{\"cat\":\"${c_category}\"}"))
				.check(jsonPath("""$.Items[*].id""").findRandom.saveAs("c_id")))
			//.body(RawFileBody("org/sample/demoblazenew/0008_request.json")))
			.pause(19)

			.exec(http("T04_SELECT_PRODUCT")
				.get(uri2 + "/prod.html?idp_=${c_id}")
				.headers(headers_10))
			.pause(1)
			.exec(http("request_11")
				.post("/check")
				.headers(headers_3)
			//	.body(StringBody("{\"token\":\"YWJjZDExMkBnbWFpbC5jb20xNzA3Mzg1\"}")))
			.body(StringBody("{\"token\":\"${c_token}\"}")))
			//.body(RawFileBody("org/sample/demoblazenew/0011_request.json"))

			.exec((http("request_12")
				.post("/view")
				.headers(headers_3)
				.body(StringBody("{\"id\":\"${c_id}\"}"))))
			//	.body(RawFileBody("org/sample/demoblazenew/0012_request.json"))))
			.pause(10)

			//GENERATE UUID
			.exec(session => {
				val c_sessionID = UUID.randomUUID().toString
				session.set("c_sessionID", c_sessionID)
			})

			.exec(http("T05_ADD_TO_CART")
				.post("/addtocart")
				.headers(headers_3)
				.body(StringBody("""{"id": "${c_sessionID}", "cookie": "${c_token}", "prod_id": ${c_id}, "flag": true}""")))
			//.body(StringBody("""{"id": "${c_sessionID}", "cookie": "${c_token}", "prod_id": ${c_id}, "flag": true}"""))



			.exec(http("T06_VIEW_CART")
				.get(uri2 + "/cart.html")
				.headers(headers_10))
			.pause(1)


			.exec(http("request_17")
				.post("/check")
				.headers(headers_3)
				//.body(StringBody("{\"token\":\"YWJjZDExMkBnbWFpbC5jb20xNzA3Mzg1\"}")))
				.body(StringBody("{\"token\":\"${c_token}\"}")))
			.pause(13)
			//.body(RawFileBody("org/sample/demoblazenew/0017_request.json"))
			.exec(http("request_18")
				.post("/viewcart")
				.headers(headers_3)
				.check(jsonPath("""$.Items[*].id """).findRandom.saveAs("c_sessionID"))
				.body(StringBody("{\"cookie\":\"${c_token}\",\"flag\":true}")))
			.pause(13)
			.exec(http("request_19")
				.post("/view")
				.headers(headers_3)
				.body(StringBody("{\"id\":${c_id}}")))
			//.body(RawFileBody("org/sample/demoblazenew/0019_request.json")))
			.pause(13)

			.exec(http("T07_PLACE_ORDER")
				.post("/deletecart")
				.headers(headers_3)
				.body(StringBody("{\"cookie\":\"${p_username}\"}")))
			//.body(RawFileBody("org/sample/demoblazenew/0021_request.json")))
			.pause(7)
			.exec(http("request_22")
				.get("/entries")
				.headers(headers_1))

			.exec((http("request_23")
				.post("/check")
				.headers(headers_3)
				//.body(StringBody("{\"token\":\"YWJjZDExMkBnbWFpbC5jb20xNzA3Mzg1\"}"))))
			.body(StringBody("{\"token\":\"${c_token}\"}"))))
			//.body(RawFileBody("org/sample/demoblazenew/0023_request.json"))))
			.pause(6)

			.exec(http("T08_logout")
				.get(uri2 + "/index.html")
				.headers(headers_10))
			.pause(1)
			.exec(http("request_26")
				.get("/entries")
				.headers(headers_1))
	}
	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}