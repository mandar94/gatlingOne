package Scenario


import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Headers.Headers._
import variables.Variables._

import scala.concurrent.duration._

object Scenario {

  val scn = scenario("demoBlazeNew")

    .exec(http("T01_HOME")
      .get("/entries")
      .headers(headers_1)
      .check(jsonPath("""$.Items[*].cat""").saveAs("c_category")))
    .pause(10)

    .exec(http("T02_LOGIN")
      .post("/login")
      .headers(headers_3)
      .body(StringBody("{\"username\":\"abcd112@gmail.com\",\"password\":\"VGVzdGVyQDEyMw==\"}"))
      .check(regex("""Auth_token: (.*?)""").findRandom.saveAs("c_token")))
    //	.body(RawFileBody("org/sample/demoblazenew/0003_request.json")))
    .pause(1)
    .exec(http("request_4")
      .get("/entries")
      .headers(headers_1)
      .resources(http("request_5")
        .post("/check")
        .headers(headers_3)
        .body(StringBody("{\"token\":\"${c_token}\"}")))
    )
    //.body(RawFileBody("org/sample/demoblazenew/0005_request.json"))))
    .pause(1)


    .exec(http("T03_SELECT_CATEGORY")
      .post("/bycat")
      .headers(headers_3)
      .body(StringBody("{\"cat\":\"${c_category}\"}")))
    //.body(RawFileBody("org/sample/demoblazenew/0008_request.json")))
    .pause(19)

    .exec(http("T04_SELECT_PRODUCT")
      .get(uri2 + "/prod.html?idp_=10")
      .headers(headers_10))
    .pause(1)
    .exec(http("request_11")
      .post("/check")
      .headers(headers_3)
      .body(StringBody("{\"token\":\"${c_token}\"}"))
      //.body(RawFileBody("org/sample/demoblazenew/0011_request.json"))
      .resources(http("request_12")
        .post("/view")
        .headers(headers_3)
        .body(StringBody("{\"id\":\"10\"}"))))
    //	.body(RawFileBody("org/sample/demoblazenew/0012_request.json"))))
    .pause(10)

    .exec(http("T05_ADD_TO_CART")
      .post("/addtocart")
      .headers(headers_3)
      .body(StringBody("{\"id\":\"3eccd6e7-1c8a-5dd5-2014-c55dc920188e\",\"cookie\":\"${c_token}\",\"prod_id\":10,\"flag\":true}")))
    //.body(RawFileBody("org/sample/demoblazenew/0014_request.json")))
    .pause(9)

    .exec(http("T06_VIEW_CART")
      .get(uri2 + "/cart.html")
      .headers(headers_10))
    .pause(1)

    .exec(http("request_17")
      .post("/check")
      .headers(headers_3)
      .body(StringBody("{\"token\":\"${c_token}\"}")))
    .pause(13)
    //.body(RawFileBody("org/sample/demoblazenew/0017_request.json"))
    .exec(http("request_18")
      .post("/viewcart")
      .headers(headers_3)
      .body(StringBody("{\"cookie\":\"${c_token}\",\"flag\":true}")))
    .pause(13)
    .exec(http("request_19")
      .post("/view")
      .headers(headers_3)
      .body(StringBody("{\"id\":10}")))
    //.body(RawFileBody("org/sample/demoblazenew/0019_request.json")))
    .pause(13)

    .exec(http("T07_PLACE_ORDER")
      .post("/deletecart")
      .headers(headers_3)
      .body(StringBody("{\"cookie\":\"abcd112@gmail.com\"}")))
    //.body(RawFileBody("org/sample/demoblazenew/0021_request.json")))
    .pause(7)
    .exec(http("request_22")
      .get("/entries")
      .headers(headers_1)
      .resources(http("request_23")
        .post("/check")
        .headers(headers_3)
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

