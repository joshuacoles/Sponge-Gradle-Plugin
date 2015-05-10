import javax.inject.Inject

import org.slf4j.Logger
import org.spongepowered.api._
import org.spongepowered.api.event._
import org.spongepowered.api.event.state._
import org.spongepowered.api.plugin._
import org.spongepowered.api.service.config.DefaultConfig

@Plugin()
class ExamplePlugin {

  @Inject private val game: Game
  @Inject private val logger: Logger

  @Inject
  @DefaultConfig(sharedRoot = true)
  private val configLoader: ConfigurationLoader[CommentedConfigurationNode]

  @Subscribe
  def onStarting(event: ServerStartingEvent) {
  }

  @Subscribe
  def onPreInit(event: PreInitializationEvent) {
  }

  @Subscribe
  def onInit(event: InitializationEvent) {
  }

  @Subscribe
  def disable(event: ServerStoppingEvent) {
  }

}