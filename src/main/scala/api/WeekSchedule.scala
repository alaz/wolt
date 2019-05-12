package me.alaz.interview.wolt.api

import com.datasift.dropwizard.scala.validation.constraints._
import javax.validation.Valid

// NOTE: having days as properties is on purpose, so we can utilize input validation
// @see InputTest
case class WeekSchedule(
  @NotNull @Valid monday:     Option[Seq[OpenHours]],
  @NotNull @Valid tuesday:    Option[Seq[OpenHours]],
  @NotNull @Valid wednesday:  Option[Seq[OpenHours]],
  @NotNull @Valid thursday:   Option[Seq[OpenHours]],
  @NotNull @Valid friday:     Option[Seq[OpenHours]],
  @NotNull @Valid saturday:   Option[Seq[OpenHours]],
  @NotNull @Valid sunday:     Option[Seq[OpenHours]]
) {
  // NOTE: the range of index is [0, 7)
  def matrix = IndexedSeq(monday, tuesday, wednesday, thursday, friday, saturday, sunday)
}
