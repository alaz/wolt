package me.alaz.interview.wolt.resources

import java.util.Locale
import javax.ws.rs.WebApplicationException
import me.alaz.interview.wolt.api.{OpenHours, WeekSchedule}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class OpenHoursToHumanTest extends FunSpec with Matchers {
  describe("Resource") {
    it("fails on invalid input") {
      val resource = new OpenHoursToHuman(Locale.ENGLISH)
      an[WebApplicationException] should be thrownBy {
        resource.convertToText(schedule(saturday = Some(Seq(
          OpenHours("open", 34200), OpenHours("open", 39600)
        ))))
      }

      an[WebApplicationException] should be thrownBy {
        resource.convertToText(schedule(saturday = Some(Seq(
          OpenHours("open", 100), OpenHours("close", 10)
        ))))
      }

      an[WebApplicationException] should be thrownBy {
        resource.convertToText(schedule(saturday = Some(Seq(
          OpenHours("err", 100)
        ))))
      }
    }

    it("sorts days per EN locale") {
      val resource = new OpenHoursToHuman(Locale.ENGLISH)
      val output = resource.convertToText(schedule(
        monday = Some(Seq( OpenHours("open", 32400), OpenHours("close", 39600) )),
        sunday = Some(Seq( OpenHours("open", 32400), OpenHours("close", 39600) ))
      ))
      output should equal("""Sunday: 9 AM - 11 AM
                            |Monday: 9 AM - 11 AM""".stripMargin)
    }

    it("sorts days per FI locale") {
      val resource = new OpenHoursToHuman(Locale.forLanguageTag("fi"))
      val output = resource.convertToText(schedule(
        monday = Some(Seq( OpenHours("open", 32400), OpenHours("close", 39600) )),
        sunday = Some(Seq( OpenHours("open", 32400), OpenHours("close", 39600) ))
      ))
      output should equal("""maanantai: 9 - 11
                            |sunnuntai: 9 - 11""".stripMargin)
    }

    it("generates half-hours") {
      val resource = new OpenHoursToHuman(Locale.ENGLISH)
      val output = resource.convertToText(schedule(saturday = Some(Seq(
        OpenHours("open", 34200), OpenHours("close", 39600)
      ))))
      output should equal("""Saturday: 9:30 AM - 11 AM""")
    }
  }

  private def schedule(monday: Option[Seq[OpenHours]] = None, tuesday: Option[Seq[OpenHours]] = None, wednesday: Option[Seq[OpenHours]] = None, thursday: Option[Seq[OpenHours]] = None, friday: Option[Seq[OpenHours]] = None, saturday: Option[Seq[OpenHours]] = None, sunday: Option[Seq[OpenHours]] = None) =
    WeekSchedule(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
}
