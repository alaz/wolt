package me.alaz.interview.wolt.resources

import java.time.format.{DateTimeFormatter, FormatStyle, TextStyle}
import java.time.temporal.WeekFields
import java.time.{DayOfWeek, LocalTime}
import java.util.Locale
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs._
import javax.ws.rs.core.MediaType
import me.alaz.interview.wolt.api.{OpenHours, WeekSchedule}
import me.alaz.interview.wolt.core.{OpenHoursValidator, WeekScheduleProcessor}

// TODO: produce either JSON or text plain per `Accepts`
@Path("/hours/human")
@Consumes(Array(MediaType.APPLICATION_JSON))
class OpenHoursToHuman(defaultLocale: Locale) {
  import OpenHoursValidator._

  @PUT
  @Produces(Array(MediaType.TEXT_PLAIN))
  def convertToText(@Valid @NotNull schedule: WeekSchedule): String = {
    // TODO: take from the request OR fallback to the default --
    // @Context request: HttpServletRequest
    // request.getLocale
    implicit val locale: Locale = defaultLocale

    if (!validate(schedule.matrix)) {
      throw new WebApplicationException("Invalid input", 422)
    }

    val processor = WeekScheduleProcessor(schedule)
    if (processor.isFailure) {
      throw new WebApplicationException("Invalid input", 422)
    }

    val m = processor()
    val tf = humanTimeFormatter

    // FIXME: Really ugly code. A variety of representations should be supported
    // FIXME: via HTML/text templating and Jersey's JSON conversion.
    val ds =
      for {
        day <- localeWeekDays if m.contains(day)
        maybeRanges <- m.get(day)
      } yield {
        val info = maybeRanges match {
          case None =>
            "Closed"

          case Some(ranges) =>
            ranges.map { range =>
              s"${tf(range.start)} - ${tf(range.end)}"
            }.mkString(", ")
        }
        s"${humanWeekDay(day)}: $info"
      }
    ds.mkString("\n")
  }

  private def validate(matrix: Seq[Option[Seq[OpenHours]]]) = matrix.flatten.forall { l =>
    areValuesValid(l) && areTypesValid(l) && isOrderCorrect(l)
  }

  private def localeWeekDays(implicit locale: Locale) = {
    // FIXME: is there some more idiomatic way to iterate over week days in Locale-specific order?
    val weekFields = WeekFields.of(locale)
    0 until 7 map { i => (weekFields.getFirstDayOfWeek.getValue + i - 1) % 7 + 1 } map DayOfWeek.of
  }

  private def humanWeekDay(day: DayOfWeek)(implicit locale: Locale) =
    day.getDisplayName(TextStyle.FULL, locale)

  private def humanTimeFormatter(implicit locale: Locale) = {
    val fmt = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
    (t: Int) => {
      val lt = LocalTime.ofSecondOfDay(t)
      // NOTE: this is a hack to remove excessive minutes from time (kind of shorter than `t % 60`)
      fmt.format(lt).replace(":00", "")
    }
  }
}
