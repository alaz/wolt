package me.alaz.interview.wolt.api

import com.datasift.dropwizard.scala.ScalaBundle
import com.fasterxml.jackson.core.JsonProcessingException
import io.dropwizard.setup.Bootstrap
import io.dropwizard.testing.FixtureHelpers.fixture
import me.alaz.interview.wolt.Application
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers, OptionValues}

@RunWith(classOf[JUnitRunner])
class InputTest extends FunSpec with Matchers with OptionValues {
  val bootstrap = new Bootstrap(Application)
  bootstrap.addBundle(new ScalaBundle)

  val Mapper = bootstrap.getObjectMapper

  describe("open hours validator") {
    it("accepts valid input 1") {
      val schedule = Mapper.readValue(fixture("fixtures/correct-1.json"), classOf[WeekSchedule])
      schedule.monday should be (None)
      schedule.tuesday should be (None)
      schedule.wednesday should be (None)
      schedule.thursday should be (None)
      schedule.friday should equal (Some(List(OpenHours("open", 64800))))
      schedule.saturday.value should have (size(5))
      schedule.sunday should be (None)
    }

    it("accepts valid input 2") {
      val schedule = Mapper.readValue(fixture("fixtures/correct-2.json"), classOf[WeekSchedule])
      schedule.monday should equal (Some(Nil))
      schedule.tuesday.value should have (size(2))
      schedule.wednesday should equal (Some(Nil))
      schedule.thursday.value should have (size(2))
      schedule.friday.value should have (size(1))
      schedule.saturday.value should have (size(2))
      schedule.sunday.value should have (size(3))
    }

    it("fails for incorrect day") {
      an[JsonProcessingException] should be thrownBy {
        Mapper.readValue(fixture("fixtures/incorrect-day.json"), classOf[WeekSchedule])
      }
    }

    it("duplicate day overrides") {
      // NOTE: this is a corner case, since JSON spec does not specify what behavior is correct.
      // So we are safe to assume here that our library behavior is correct. Which means
      // that this test case is kept here for reference mostly.
      val schedule = Mapper.readValue(fixture("fixtures/corner-dup.json"), classOf[WeekSchedule])
      schedule.monday should equal (Some(Seq(OpenHours("open", 1))))
    }

    // NOTE: it would be nice to utilize validation provided by the framework for us.
    // See https://github.com/datasift/dropwizard-scala#limitations
    ignore("fails for incorrect type") {
      an[JsonProcessingException] should be thrownBy {
        Mapper.readValue(fixture("fixtures/incorrect-type.json"), classOf[WeekSchedule])
      }
    }

    ignore("fails for incorrect value") {
      an[JsonProcessingException] should be thrownBy {
        Mapper.readValue(fixture("fixtures/incorrect-value.json"), classOf[WeekSchedule])
      }
    }
  }
}
