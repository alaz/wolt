package me.alaz.interview.wolt.core

import me.alaz.interview.wolt.api.OpenHours

// TODO: investigate how to fix/extend Hybernate validation
// @see me.alaz.interview.wolt.api.OpenHours
// @see me.alaz.interview.wolt.core.ValidatorTest
object OpenHoursValidator {
  private val correctTypes = Set("open", "close")
  private val valueRange = 0 until 86400

  def areValuesValid(list: Seq[OpenHours]) =
    list.map(_.value).forall(valueRange.contains)

  // This cannot be checked by the framework, that is why this code.
  // Note that equal `value`s are acceptable.
  def isOrderCorrect(list: Seq[OpenHours]) = {
    val l = list.map(_.value).view
    l.isEmpty || !l.zip(l.tail).exists { case (prev, next) => prev > next }
  }

  def areTypesValid(list: Seq[OpenHours]): Boolean =
    list.map(_.`type`).forall(correctTypes)
}
