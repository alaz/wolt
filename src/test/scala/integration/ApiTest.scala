package me.alaz.interview.wolt.integration

import java.io.File
import com.datasift.dropwizard.scala.test.{ApplicationTest, BeforeAndAfterAllMulti}
import com.google.common.io.Resources
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import me.alaz.interview.wolt.Application
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ApiTest extends FunSpec with BeforeAndAfterAllMulti with Matchers {
  val configFile = new File(Resources.getResource("test-config.yml").toURI).getAbsolutePath
  val app = ApplicationTest(this, configFile)(Application)
  lazy val client = app.newClient("test client")

  def req(target: String) = for {
    client <- client
    server <- app.server
  } yield {
    client.target(server.getURI.resolve(target))
  }

  describe("API") {
    it("responds with plain text") {
      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.json("{}"))
        response.getStatus should equal(200)
        response.getMediaType.toString should equal(MediaType.TEXT_PLAIN)
      }

      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.json("""{ "monday": [] }"""))
        response.getStatus should equal(200)
      }

      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.json("""{ "monday": [{"type": "open", "value": 36000}, {"type": "close", "value": 64800}] }"""))
        response.getStatus should equal(200)
      }
    }

    it("rejects wrong `Accept`") {
      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_HTML)
          .put(Entity.json("{}"))
        response.getStatus should equal(406)
      }
    }

    it("rejects invalid request type") {
      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.text("{}"))
        response.getStatus should equal(415)
      }
    }

    it("rejects invalid input") {
      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.json("""{ "mon": [] }"""))
        response.getStatus should equal(400)
      }
    }

    it("rejects malformed input") {
      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.json("""{ "monday": [{"type": "open"}] }"""))
        response.getStatus should equal(422)
      }

      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.json("""{ "monday": [{"type": "err"}] }"""))
        response.getStatus should equal(422)
      }

      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.json("""{ "monday": [{"type": "open", "value": 36000}] }"""))
        response.getStatus should equal(422)
      }
    }
  }
}
