
package uk.co.paulcourt.blog;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.dbclient.DbClient;
import io.helidon.health.HealthSupport;
import io.helidon.health.checks.HealthChecks;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.metrics.MetricsSupport;
import io.helidon.security.Security;
import io.helidon.security.integration.webserver.WebSecurity;
import io.helidon.webserver.FormParamsSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.StaticContentSupport;
import io.helidon.webserver.WebServer;

/**
 * The application main class.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() {
    }

    /**
     * Application main entry point.
     * @param args command line arguments.
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException {
        startServer();
    }

    /**
     * Start the server.
     * @return the created {@link WebServer} instance
     * @throws IOException if there are problems reading logging properties
     */
    static WebServer startServer() throws IOException {
        // load logging configuration
        setupLogging();

        // By default this will pick up application.yaml from the classpath
        Config config = Config.builder()
                .sources(
                        ConfigSources.file("conf/overrides.properties").optional(true),
                        ConfigSources.classpath("application.yaml"))
                .disableEnvironmentVariablesSource()
                .build();

        // Setup freemarker
        Configuration fmConfig = new Configuration(Configuration.VERSION_2_3_30);
        fmConfig.setClassForTemplateLoading(Main.class, "/views");
        fmConfig.setDefaultEncoding("UTF-8");
        fmConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        fmConfig.setLogTemplateExceptions(false);
        fmConfig.setWrapUncheckedExceptions(true);
        fmConfig.setFallbackOnNullLoopVariable(false);
        
        // Build server with JSONP support
        WebServer server = WebServer.builder(createRouting(config, fmConfig))
                .config(config.get("server"))
                .addMediaSupport(JsonpSupport.create())
                .build();

        // Try to start the server. If successful, print some info and arrange to
        // print a message at shutdown. If unsuccessful, print the exception.
        server.start()
                .thenAccept(ws -> {
                    System.out.println(
                            "WEB server is up! http://localhost:" + ws.port() + "/");
                    ws.whenShutdown().thenRun(()
                            -> System.out.println("WEB server is DOWN. Good bye!"));
                })
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                    return null;
                });

        // Server threads are not daemon. No need to block. Just react.

        return server;
    }

    /**
     * Creates new {@link Routing}.
     *
     * @return routing configured with JSON support, a health check, and a service
     * @param config configuration of this server
     */
    private static Routing createRouting(Config config, Configuration fmConfig) {
        Config dbConfig = config.get("db");
        DbClient dbClient = DbClient.create(dbConfig);
        
        Security security = Security.create(config.get("security"));
        
        MetricsSupport metrics = MetricsSupport.create();
        HealthSupport health = HealthSupport.builder()
                .addLiveness(HealthChecks.healthChecks())   // Adds a convenient set of checks
                .build();

        BlogService blogService = new BlogService(
                config,
                fmConfig,
                dbClient);
        
        SitemapService sitemapService = new SitemapService(
                fmConfig,
                dbClient);
        
        return Routing.builder()
                .register(WebSecurity
                        .create(security)
                        .securityDefaults(WebSecurity.authenticate())
                )
                //.register(MyFormParamSupport.create())
                .register(MyFormParamSupport.create())
                .register(health)                   // Health at "/health"
                .register(metrics)                  // Metrics at "/metrics"
                .register("/", blogService)
                .register("/", sitemapService)
                .register("/", StaticContentSupport.builder("htmlAssets"))
                .build();
    }

    /**
     * Configure logging from logging.properties file.
     */
    private static void setupLogging() throws IOException {
        try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        }
    }
}
