package com.amazonaws.samples.appconfig.utils;
import com.amazonaws.samples.appconfig.movies.Movie;
public class HTMLBuilder {

//    public String getMoviesHtml(Movie[] movies) {
//        StringBuilder htmlBuilder = new StringBuilder();
//        htmlBuilder.append("<div id='movies-container'>");
//        htmlBuilder.append("<h1> FREE Movie List for this Month</h1>");
//        for (Movie movie : movies) {
//            htmlBuilder.append("<div class='movie-item'>");
//            htmlBuilder.append("<p>ID: ").append(movie.getId()).append("</p>");
//            htmlBuilder.append("<h3>").append(movie.getMovieName()).append("</h3>");
//            htmlBuilder.append("<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>");
//            // Add other movie details as needed
//            htmlBuilder.append("</div>");
//        }
//        htmlBuilder.append("<hr>");
//        htmlBuilder.append("<hr width=\"100%\" size=\"2\" color=\"blue\" noshade>");
//
//        return htmlBuilder.toString();
//    }

    public String getMoviesHtml(Movie[] movies) {
        // Using text block (JDK 15+ feature) for better HTML template readability
        String htmlTemplate = """
            <div id='movies-container'>
                <h1> FREE Movie List for this Month</h1>
                %s
                <hr>
                <hr width="100%%" size="2" color="blue" noshade>
            </div>
            """;
        
        return String.format(htmlTemplate, getMovieItemsHtml(movies));
    }

    private static String getMovieItemsHtml(Movie[] movies) {
        StringBuilder movieItemsHtml = new StringBuilder();
        for (Movie movie : movies) {
            // Using text block for each movie item
            String movieTemplate = """
                <div class='movie-item'>
                    <p>ID: %d</p>
                    <h3>%s</h3>
                    <hr width="100%%" size="2" color="blue" noshade>
                </div>
                """;
            
            movieItemsHtml.append(String.format(movieTemplate, movie.id(), movie.movieName()));
        }
        return movieItemsHtml.toString();
    }
}