package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Search movies based on provided criteria.
     * Supports filtering by name (partial match), id (exact match), and genre (partial match).
     * All text searches are case-insensitive.
     * 
     * @param name Movie name to search for (partial match, case-insensitive)
     * @param id Movie ID to search for (exact match)
     * @param genre Movie genre to search for (partial match, case-insensitive)
     * @return List of movies matching the search criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Searching movies with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>();
        
        // If all parameters are null or empty, return empty list
        if (isEmptyString(name) && id == null && isEmptyString(genre)) {
            logger.warn("All search parameters are empty, returning empty results");
            return results;
        }
        
        for (Movie movie : movies) {
            boolean matches = true;
            
            // Check name criteria (partial match, case-insensitive)
            if (!isEmptyString(name)) {
                matches = matches && movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim());
            }
            
            // Check id criteria (exact match)
            if (id != null && id > 0) {
                matches = matches && movie.getId() == id;
            }
            
            // Check genre criteria (partial match, case-insensitive)
            if (!isEmptyString(genre)) {
                matches = matches && movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim());
            }
            
            if (matches) {
                results.add(movie);
            }
        }
        
        logger.info("Found {} movies matching search criteria", results.size());
        return results;
    }
    
    /**
     * Helper method to check if a string is null, empty, or contains only whitespace
     */
    private boolean isEmptyString(String str) {
        return str == null || str.trim().isEmpty();
    }
}
