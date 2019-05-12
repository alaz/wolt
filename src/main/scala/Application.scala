package me.alaz.interview.wolt

import com.datasift.dropwizard.scala.ScalaApplication
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import me.alaz.interview.wolt.resources.OpenHoursToHuman

object Application extends ScalaApplication[ApplicationConfiguration] {
  override def init(bootstrap: Bootstrap[ApplicationConfiguration]) {
  }

  override def run(conf: ApplicationConfiguration, env: Environment): Unit = {
    env.jersey().register(new OpenHoursToHuman(conf.defaultLocale))
  }
}
