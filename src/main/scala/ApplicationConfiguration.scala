package me.alaz.interview.wolt

import java.util.Locale
import com.datasift.dropwizard.scala.validation.constraints.NotEmpty
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration

class ApplicationConfiguration extends Configuration {
  // Locale is mandatory in the application configuration to avoid falling back to
  // the system's default locale (this would result in possibly non-repeatable
  // deployments)
  @NotEmpty @JsonProperty
  private var locale: String = _

  def defaultLocale = Locale.forLanguageTag(locale)
}
