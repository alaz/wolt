package me.alaz.interview.wolt.integration

import java.io.File
import com.datasift.dropwizard.scala.test.{ApplicationTest, BeforeAndAfterAllMulti}
import com.google.common.io.Resources
import me.alaz.interview.wolt.Application
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ApiTest extends FunSpec with BeforeAndAfterAllMulti with Matchers {
    val app = ApplicationTest(this, new File(Resources.getResource("test-app.yml").toURI).getAbsolutePath) {
      Application
    }

  describe("API") {
    it("responds with plain text") {
      // TODO
    }
  }
}
