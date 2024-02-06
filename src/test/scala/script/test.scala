package script

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

object test {

    val httpProtocol = http
      .baseUrl("https://petstore.octoperf.com")
      .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())

    val headers_1 = Map(
      "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
      "Accept-Encoding" -> "gzip, deflate, br",
      "Accept-Language" -> "en-US,en;q=0.9",
      "Sec-Fetch-Dest" -> "document",
      "Sec-Fetch-Mode" -> "navigate",
      "Sec-Fetch-Site" -> "none",
      "Sec-Fetch-User" -> "?1",
      "Upgrade-Insecure-Requests" -> "1",
      "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
      "sec-ch-ua" -> """Not A(Brand";v="99", "Google Chrome";v="121", "Chromium";v="121""",
      "sec-ch-ua-mobile" -> "?0",
      "sec-ch-ua-platform" -> "Windows")

    val headers_2 = Map(
      "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
      "Accept-Encoding" -> "gzip, deflate, br",
      "Accept-Language" -> "en-US,en;q=0.9",
      "Sec-Fetch-Dest" -> "document",
      "Sec-Fetch-Mode" -> "navigate",
      "Sec-Fetch-Site" -> "same-origin",
      "Sec-Fetch-User" -> "?1",
      "Upgrade-Insecure-Requests" -> "1",
      "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
      "sec-ch-ua" -> """Not A(Brand";v="99", "Google Chrome";v="121", "Chromium";v="121""",
      "sec-ch-ua-mobile" -> "?0",
      "sec-ch-ua-platform" -> "Windows")

    val headers_5 = Map(
      "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
      "Accept-Encoding" -> "gzip, deflate, br",
      "Accept-Language" -> "en-US,en;q=0.9",
      "Cache-Control" -> "max-age=0",
      "Origin" -> "https://petstore.octoperf.com",
      "Sec-Fetch-Dest" -> "document",
      "Sec-Fetch-Mode" -> "navigate",
      "Sec-Fetch-Site" -> "same-origin",
      "Sec-Fetch-User" -> "?1",
      "Upgrade-Insecure-Requests" -> "1",
      "User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
      "sec-ch-ua" -> """Not A(Brand";v="99", "Google Chrome";v="121", "Chromium";v="121""",
      "sec-ch-ua-mobile" -> "?0",
      "sec-ch-ua-platform" -> "Windows")

    //val credentialsFeeder=csv("C:\\Users\\mankulka1\\Desktop\\gatling\\petStore\\src\\test\\scala\\org\\sample\\creds.csv").random

    val credentialsFeeder = csv("data/creds.csv").circular

    val scn = scenario("petStore").repeat(1) {
      feed(credentialsFeeder)
        .exec(http("T01_HOME")
          .get("/")
          .headers(headers_1))
        //.check(regex("""jsessionid=(.*?)"""").findRandom.saveAs("c_sessionID"))
        .pause(3)

        .exec(http("T02_CATALOG")
          .get("/actions/Catalog.action")
          .headers(headers_2)
          .check(regex("""amp;categoryId=(.*?)"""").findRandom.saveAs("c_category"),
            regex("""action;jsessionid=(.*+)""").findRandom.saveAs("c_sessionID"),
            regex(""""_sourcePage" value="(.*?)"""").findRandom.saveAs("c_sourcePage"),
            regex("""__fp" value="(.*?)"""").findRandom.saveAs("c_fp"))
        )
        .exec(session => {
          println(s"c_category value: ${session("c_category").as[String]}")
          session
        })
        .pause(3)
        .exec(http("T03_LOGIN")
          .get("/actions/Account.action;jsessionid=${c_sessionID}?signonForm=")
          .headers(headers_2))
        .pause(3)
        .exec(http("T04_CREDS")
          .post("/actions/Account.action")
          .headers(headers_5)
          .formParam("username", "${p_username}")
          .formParam("password", "${p_password}")
          .formParam("signon", "Login")
          .formParam("_sourcePage", "${c_sourcePage}")
          .formParam("__fp", "${c_fp}"))
        .pause(3)

        .exec(http("T05_SELECT_CATEGORY")
          .get("/actions/Catalog.action?viewCategory=&categoryId=${c_category}")
          .headers(headers_2)
          .check(regex("""&amp;productId=(.*?)"""").findRandom.saveAs("c_productID")))
        .exec(session => {
          println(s"c_productID value: ${session("c_productID").as[String]}")
          session
        })
        .pause(3)

        .exec(http("T06_SELECT_PRODUCT")
          .get("/actions/Catalog.action?viewProduct=&productId=${c_productID}")
          .headers(headers_2)
          .check(regex("""workingItemId=(.*?)"""").findRandom.saveAs("c_item")))
        .exec(session => {
          println(s"c_item value: ${session("c_item").as[String]}")
          session
        })
        .pause(3)

        .exec(http("T07_ADD_TO_CART")
          .get("/actions/Cart.action?addItemToCart=&workingItemId=${c_item}")
          .headers(headers_2))
        .pause(3)

        .exec(http("T08_CHECKOUT")
          .get("/actions/Order.action?newOrderForm=")
          .headers(headers_2))
        //.check(regex(""""newOrder" type="submit" value="(.*?)"""").findRandom.saveAs("c_order")))
        .pause(3)
        .exec(http("T09_ENTER_DETAILS")
          .post("/actions/Order.action")
          .headers(headers_5)
          .formParam("order.cardType", "${p_cardType}")
          .formParam("order.creditCard", "${p_creditCard}")
          .formParam("order.expiryDate", "${p_expiryDate}")
          .formParam("order.billToFirstName", "${p_billToFirstName}")
          .formParam("order.billToLastName", "${p_billToLastName}")
          .formParam("order.billAddress1", "${p_billAddress1}")
          .formParam("order.billAddress2", "${p_billAddress2}")
          .formParam("order.billCity", "${p_billCity}")
          .formParam("order.billState", "${p_billState}")
          .formParam("order.billZip", "${p_billZip}")
          .formParam("order.billCountry", "${p_billCountry}")
          .formParam("newOrder", "Continue")
          .formParam("_sourcePage", "${c_sourcePage}")
          .formParam("__fp", "${c_fp}"))
        .pause(3)

        .exec(http("T10_CONFIRM")
          .get("/actions/Order.action?newOrder=&confirmed=true")
          .headers(headers_2))
    }




}
