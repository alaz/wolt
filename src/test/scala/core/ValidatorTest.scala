package me.alaz.interview.wolt.core

import me.alaz.interview.wolt.api.OpenHours
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ValidatorTest extends FunSpec with Matchers {
  import OpenHoursValidator._

  describe("OpenHoursValidator") {
    it("accepts empty list") {
      areTypesValid(Nil) should be (true)
      areValuesValid(Nil) should be (true)
      isOrderCorrect(Nil) should be (true)
    }

    it("fails on incorrect type") {
      areTypesValid(List(OpenHours("incorr", 1))) should be (false)
    }

    it("accepts correct types") {
      areTypesValid(List(
        OpenHours("open", 1), OpenHours("close", 2), OpenHours("close", 3)
      )) should be (true)
    }

    it("accepts correct order") {
      isOrderCorrect(List(
        OpenHours("open", 1)
      )) should be (true)

      isOrderCorrect(List(
        OpenHours("open", 1), OpenHours("close", 20), OpenHours("close", 50)
      )) should be (true)
    }

    it("fails on wrong order") {
      isOrderCorrect(List(
        OpenHours("open", 1), OpenHours("close", 5), OpenHours("close", 3)
      )) should be (false)

      isOrderCorrect(List(
        OpenHours("close", 5), OpenHours("close", 1), OpenHours("close", 3)
      )) should be (false)

      isOrderCorrect(List(
        OpenHours("close", 1), OpenHours("close", 2), OpenHours("close", 5), OpenHours("close", 3)
      )) should be (false)
    }

    it("accepts when the same `value` in a row") {
      isOrderCorrect(List(
        OpenHours("open", 1), OpenHours("close", 1), OpenHours("close", 30)
      )) should be (true)
    }

    // TODO: good candidate for property-based checking
    it("fails on incorrect `value`") {
      areValuesValid(List(
        OpenHours("open", -1)
      )) should be (false)

      areValuesValid(List(
        OpenHours("open", 86400)
      )) should be (false)
    }

    it("accepts correct `value`") {
      areValuesValid(List(
        OpenHours("open", 0)
      )) should be (true)

      areValuesValid(List(
        OpenHours("open", 86399)
      )) should be (true)
    }
  }
}
