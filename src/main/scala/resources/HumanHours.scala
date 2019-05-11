package me.alaz.interview.wolt.resources

import javax.ws.rs.core.MediaType
import javax.ws.rs.{GET, Path, Produces};

@Path("/human/hours")
@Produces(Array(MediaType.TEXT_PLAIN))
class HumanHours {
  @GET
  def convertToHuman(): String = {
    // TODO
    "hello"
  }
}
