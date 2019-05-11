package me.alaz.interview.wolt.integration

import java.io.File
import com.datasift.dropwizard.scala.test.{ApplicationTest, BeforeAndAfterAllMulti}
import com.google.common.io.Resources
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import me.alaz.interview.wolt.Application
import me.alaz.interview.wolt.api.WeekSchedule
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ApiTest extends FunSpec with BeforeAndAfterAllMulti with Matchers {
  val configFile = new File(Resources.getResource("test-app.yml").toURI).getAbsolutePath
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
    }

    it("rejects wrong `Accept`") {
      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_HTML)
          .put(Entity.json("{}"))
        response.getStatus should equal(406)
      }
    }

    it("rejects wrong request type") {
      for {r <- req("/venue/hours/human")} {
        val response = r
          .request(MediaType.TEXT_PLAIN)
          .put(Entity.text("{}"))
        response.getStatus should equal(415)
      }
    }
  }
}
