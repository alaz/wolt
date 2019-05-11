package me.alaz.interview.wolt.resources

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.core.MediaType
import javax.ws.rs.{Consumes, PUT, Path, Produces}
import me.alaz.interview.wolt.api.WeekSchedule;

@Path("/human/hours")
@Consumes(Array(MediaType.APPLICATION_JSON))
@Produces(Array(MediaType.TEXT_PLAIN))
class HumanHours {
  @PUT
  def convertToHuman(@Valid @NotNull schedule: WeekSchedule): String = {
    // TODO
    "hello"
  }
}
