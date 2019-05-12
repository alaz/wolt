package me.alaz.interview.wolt.api

import com.datasift.dropwizard.scala.validation.constraints._

// NOTE: the idea is to use validation provided by the framework here, but --
// See https://github.com/datasift/dropwizard-scala#limitations
// @see me.alaz.interview.wolt.core.OpenHoursValidator
case class OpenHours(
  @NotNull @Pattern(regexp = "open|close") `type`: String,
  @NotNull @Max(86399) @Min(0) value: Int
) {
  def isOpening = `type` == "open"
  def isClosing = `type` == "close"
}
