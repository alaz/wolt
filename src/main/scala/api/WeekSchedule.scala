package me.alaz.interview.wolt.api

import javax.validation.Valid
import com.datasift.dropwizard.scala.validation.constraints._

// NOTE: the idea is to use validation provided by the framework here, but --
// See https://github.com/datasift/dropwizard-scala#limitations
case class OpenHours(
  @NotNull @Pattern(regexp = "open|close") `type`: String,
  @NotNull @Max(86399) @Min(0) value: Int
)

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
)
