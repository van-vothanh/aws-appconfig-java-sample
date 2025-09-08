package com.amazonaws.samples.appconfig.movies;

// Converted to a Java record (JDK 16+ feature)
// Records provide immutable data classes with automatic getters, equals, hashCode, and toString
public record Movie(long id, String movieName) {
    // This is a compact constructor to validate inputs if needed
    public Movie {
        // Additional validation could be added here
    }
    
    // Custom static factory method to create a Movie with an integer id
    public static Movie withId(int movieId, String name) {
        return new Movie(movieId, name);
    }
}