import org.slf4j.Logger
import org.spongepowered.api.Game
import org.spongepowered.api.event.Subscribe
import org.spongepowered.api.event.state.InitializationEvent
import org.spongepowered.api.event.state.PreInitializationEvent
import org.spongepowered.api.event.state.ServerStartingEvent
import org.spongepowered.api.event.state.ServerStoppingEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.service.config.DefaultConfig

import javax.inject.Inject


@Plugin(id = "@[plugin.id]", name = "@[plugin.name]", version = "@[plugin.version]")
public class GroovyExamplePlugin {

    @Inject private Game game;
    @Inject private Logger logger;
    @Inject @DefaultConfig(sharedRoot = true) private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    @Subscribe
    public void onStarting(ServerStartingEvent event) {
    }

    @Subscribe
    public void onPreInit(PreInitializationEvent event) {
    }

    @Subscribe
    public void onInit(InitializationEvent event) {
    }

    @Subscribe
    public void disable(ServerStoppingEvent event) {
    }

}