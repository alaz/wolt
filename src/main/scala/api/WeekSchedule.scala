package me.alaz.interview.wolt.api

import javax.validation.Valid
import com.datasift.dropwizard.scala.validation.constraints._

// FIXME: https://github.com/datasift/dropwizard-scala#limitations
case class OpenHours(
  @NotNull @Pattern(regexp = "open|close") `type`: String,
  @NotNull @Max(86399) @Min(0) value: Int
)

case class WeekSchedule(
  @NotNull @Valid monday:     Option[Seq[OpenHours]],
  @NotNull @Valid tuesday:    Option[Seq[OpenHours]],
  @NotNull @Valid wednesday:  Option[Seq[OpenHours]],
  @NotNull @Valid thursday:   Option[Seq[OpenHours]],
  @NotNull @Valid friday:     Option[Seq[OpenHours]],
  @NotNull @Valid saturday:   Option[Seq[OpenHours]],
  @NotNull @Valid sunday:     Option[Seq[OpenHours]]
)
