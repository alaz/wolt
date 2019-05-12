package me.alaz.interview.wolt.core

import java.time.DayOfWeek
import scala.util.{Failure, Success}
import me.alaz.interview.wolt.api.{OpenHours, WeekSchedule}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ProcessorTest extends FunSpec with Matchers {
  describe("WeekScheduleProcessor") {
    it("accepts empty list") {
      val p = processor()
      p should be('success)
      p should not be('failure)
    }

    it("builds a range") {
      val p = processor(
        monday = Some(Seq.empty),
        friday = Some(Seq(OpenHours("open", 32400), OpenHours("close", 39600)))
      )
      p.normalized should have size 2
      p.normalized(DayOfWeek.MONDAY) should equal (None)
      p.normalized(DayOfWeek.FRIDAY) should equal (Some(Seq(Success((32400 until 39600).inclusive))))

      p.isSuccess should equal (true)
      p.isFailure should equal (false)

      p() should equal(Map(
        DayOfWeek.MONDAY -> None,
        DayOfWeek.FRIDAY -> Some(Seq((32400 until 39600).inclusive))
      ))
    }

    it("builds ranges") {
      val p = processor(friday = Some(Seq(
        OpenHours("open", 32400), OpenHours("close", 39600),
        OpenHours("open", 57600), OpenHours("close", 82800),
      )))
      p.normalized should have size 1
      p.normalized(DayOfWeek.FRIDAY) should equal (Some(Seq(
        Success((32400 until 39600).inclusive),
        Success((57600 until 82800).inclusive)
      )))

      p.isSuccess should equal (true)
      p.isFailure should equal (false)
    }

    it("processes next day closure") {
      val p = processor(
        monday = Some(Seq(OpenHours("close", 3600))),
        friday = Some(Seq(OpenHours("open", 36000))),
        saturday = Some(Seq(OpenHours("close", 3600), OpenHours("open", 36000))),
        sunday = Some(Seq(OpenHours("close", 3600), OpenHours("open", 36000)))
      )
      p.normalized should have size 4
      p.normalized(DayOfWeek.MONDAY) should equal (None)
      p.normalized(DayOfWeek.FRIDAY) should equal(Some(Seq( Success((36000 until 3600).inclusive) )))
      p.normalized(DayOfWeek.SATURDAY) should equal(Some(Seq( Success((36000 until 3600).inclusive) )))
      p.normalized(DayOfWeek.SUNDAY) should equal(Some(Seq( Success((36000 until 3600).inclusive) )))

      p.isSuccess should equal (true)
      p.isFailure should equal (false)
    }

    it("detects extra hours specification") {
      val p = processor(
        friday = Some(Seq(OpenHours("close", 32400), OpenHours("close", 39600)))
      )
      p.normalized should have size 1
      p.normalized(DayOfWeek.FRIDAY) should equal(Some(Seq(Failure(MalformedInputException))))

      p.isSuccess should equal (false)
      p.isFailure should equal (true)
    }

    it("detects single specification") {
      val p = processor(
        friday = Some(Seq(OpenHours("open", 32400)))
      )
      p.normalized should have size 1
      p.normalized(DayOfWeek.FRIDAY) should equal(Some(Seq(Failure(MalformedInputException))))

      p.isSuccess should equal (false)
      p.isFailure should equal (true)

      p.errors should equal(Map(DayOfWeek.FRIDAY -> Seq(MalformedInputException)))
    }
  }

  private def processor(monday: Option[Seq[OpenHours]] = None, tuesday: Option[Seq[OpenHours]] = None, wednesday: Option[Seq[OpenHours]] = None, thursday: Option[Seq[OpenHours]] = None, friday: Option[Seq[OpenHours]] = None, saturday: Option[Seq[OpenHours]] = None, sunday: Option[Seq[OpenHours]] = None) =
    WeekScheduleProcessor(WeekSchedule(monday, tuesday, wednesday, thursday, friday, saturday, sunday))
}