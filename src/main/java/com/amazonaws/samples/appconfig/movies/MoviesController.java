package com.amazonaws.samples.appconfig.movies;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import com.amazonaws.samples.appconfig.utils.AppConfigUtility;
import com.amazonaws.samples.appconfig.cache.ConfigurationCache;
import com.amazonaws.samples.appconfig.model.ConfigurationKey;
import com.amazonaws.samples.appconfig.utils.HTMLBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.appconfig.AppConfigClient;
import software.amazon.awssdk.services.appconfig.model.GetConfigurationResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.validation.Valid;

@RestController
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    /**
     * Static Movie Array containing all the list of Movies.
     */
    static final Movie[] PAIDMOVIES = {
        new Movie(1L, "Static Movie 1"),
        new Movie(2L, "Static Movie 2"),
        new Movie(3L, "Static Movie 3"),
        new Movie(4L, "Static Movie 4"),
        new Movie(5L, "Static Movie 5"),
        new Movie(6L, "Static Movie 6"),
        new Movie(7L, "Static Movie 7"),
        new Movie(8L, "Static Movie 8"),
        new Movie(9L, "Static Movie 9"),
        new Movie(10L, "Static Movie 10")
    };
    public Duration cacheItemTtl = Duration.ofSeconds(30);
    private Boolean boolEnableFeature;
    private int intItemLimit;
    AppConfigClient client;
    String clientId;
    ConfigurationCache cache;

    @Autowired
    Environment env;

    /**
     * REST API method to get all the Movies based on AWS App Config parameter.
     *
     * @return List of Movies
     */
    @GetMapping("/movies/getMovies")
    public String movie() {
        logger.info("Fetching movies from AWS App Config");
        try {
            cacheItemTtl = Duration.ofSeconds(Long.parseLong(env.getProperty("appconfig.cacheTtlInSeconds")));

            final AppConfigUtility appConfigUtility = new AppConfigUtility(getOrDefault(this::getClient, this::getDefaultClient),
                    getOrDefault(this::getConfigurationCache, ConfigurationCache::new),
                    getOrDefault(this::getCacheItemTtl, () -> cacheItemTtl),
                    getOrDefault(this::getClientId, this::getDefaultClientId));

            final String application = env.getProperty("appconfig.application");
            final String environment = env.getProperty("appconfig.environment");
            final String config = env.getProperty("appconfig.config");
            final GetConfigurationResponse response = appConfigUtility.getConfiguration(new ConfigurationKey(application, environment, config));
            final String appConfigResponse = response.content().asUtf8String();

            final JSONObject jsonResponseObject = new JSONObject(appConfigResponse);
            System.out.println("json is " + jsonResponseObject);

            JSONArray moviesArray = jsonResponseObject.getJSONArray("movies");
            System.out.println("movies array is " + moviesArray);
            
            // Using Java 21 features: Stream API with toList()
            List<Movie> movieList = IntStream.range(0, moviesArray.length())
                .mapToObj(i -> {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    long id = movieObj.getLong("id");
                    String movieName = movieObj.getString("movieName");
                    return new Movie(id, movieName);
                })
                .toList(); // Java 16+ feature: Stream.toList() instead of collect(Collectors.toList())
            
            // Using Java 21 features: Shorter array conversion
            Movie[] movies = movieList.toArray(Movie[]::new);
            
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            return htmlBuilder.getMoviesHtml(movies);
        } catch (Exception e) {
            logger.error("Error fetching movies from AWS App Config", e);
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            return htmlBuilder.getMoviesHtml(PAIDMOVIES);
        }
    }

    @PostMapping("/movies/{movie}/edit")
    public String processUpdateMovie(@Valid Movie movie, BindingResult result, @PathVariable int movieId) {
        final AppConfigUtility appConfigUtility = new AppConfigUtility(getOrDefault(this::getClient, this::getDefaultClient),
                getOrDefault(this::getConfigurationCache, ConfigurationCache::new),
                getOrDefault(this::getCacheItemTtl, () -> cacheItemTtl),
                getOrDefault(this::getClientId, this::getDefaultClientId));

        final String application = env.getProperty("appconfig.application");
        final String environment = env.getProperty("appconfig.environment");
        final String config = env.getProperty("appconfig.config");

        final GetConfigurationResponse response = appConfigUtility.updateConfiguration(
            new ConfigurationKey(application, environment, config), movie.toString());
        final String appConfigResponse = response.content().asUtf8String();

        final JSONObject jsonResponseObject = new JSONObject(appConfigResponse);
        System.out.println("json is " + jsonResponseObject);

        JSONArray moviesArray = jsonResponseObject.getJSONArray("movies");
        System.out.println("movies array is " + moviesArray);
        
        // Using Java 21 features: Stream API with toList()
        List<Movie> movieList = IntStream.range(0, moviesArray.length())
            .mapToObj(i -> {
                JSONObject movieObj = moviesArray.getJSONObject(i);
                return new Movie(movieObj.getLong("id"), movieObj.getString("movieName"));
            })
            .toList();
        
        // Using Java 21 features: Shorter array conversion
        Movie[] movies = movieList.toArray(Movie[]::new);
        
        HTMLBuilder htmlBuilder = new HTMLBuilder();
        return htmlBuilder.getMoviesHtml(movies);
    }

    private <T> T getOrDefault(final Supplier<T> optionalGetter, final Supplier<T> defaultGetter) {
        return Optional.ofNullable(optionalGetter.get()).orElseGet(defaultGetter);
    }

    String getDefaultClientId() {
        return UUID.randomUUID().toString();
    }

    protected AppConfigClient getDefaultClient() {
        return AppConfigClient.create();
    }

    public ConfigurationCache getConfigurationCache() {
        return cache;
    }

    public AppConfigClient getClient() {
        return client;
    }

    public Duration getCacheItemTtl() {
        return cacheItemTtl;
    }

    public String getClientId() {
        return clientId;
    }
}