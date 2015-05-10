@Plugin(id = "@[plugin.id]", name = "@[plugin.name]", version = "@[plugin.version]")
public class ExamplePlugin {

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