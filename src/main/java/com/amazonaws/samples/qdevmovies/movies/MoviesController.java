package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    /**
     * REST endpoint for searching movies with query parameters.
     * Supports searching by name, id, and genre with pirate-themed responses.
     * 
     * @param name Movie name to search for (optional, partial match)
     * @param id Movie ID to search for (optional, exact match)
     * @param genre Movie genre to search for (optional, partial match)
     * @return JSON response with search results and pirate-themed messages
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Ahoy! Searching for treasure with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate that at least one search parameter is provided
            if ((name == null || name.trim().isEmpty()) && 
                id == null && 
                (genre == null || genre.trim().isEmpty())) {
                
                logger.warn("Arrr! No search criteria provided by the landlubber");
                response.put("success", false);
                response.put("message", "Arrr! Ye need to provide at least one search criterion, matey! Try searchin' by name, id, or genre.");
                response.put("movies", List.of());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                logger.warn("Invalid movie ID provided: {}", id);
                response.put("success", false);
                response.put("message", "Shiver me timbers! That ID be as useless as a compass that spins! Provide a proper movie ID, ye scallywag.");
                response.put("movies", List.of());
                return ResponseEntity.badRequest().body(response);
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            if (searchResults.isEmpty()) {
                logger.info("No movies found for search criteria");
                response.put("success", true);
                response.put("message", "Blimey! No treasure found with those search terms, matey. Try different criteria or check yer spellin'!");
                response.put("movies", List.of());
                response.put("searchCriteria", createSearchCriteriaMap(name, id, genre));
                return ResponseEntity.ok(response);
            }
            
            logger.info("Found {} movies matching search criteria", searchResults.size());
            response.put("success", true);
            response.put("message", String.format("Ahoy! Found %d fine pieces of cinematic treasure for ye, me hearty!", searchResults.size()));
            response.put("movies", searchResults);
            response.put("searchCriteria", createSearchCriteriaMap(name, id, genre));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error occurred during movie search: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Batten down the hatches! Something went wrong with yer search. Try again later, ye brave soul!");
            response.put("movies", List.of());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Enhanced movies page that supports both regular listing and search results display
     */
    @GetMapping("/movies/search-page")
    public String searchMoviesPage(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Displaying search page with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        // If no search parameters provided, show all movies
        if ((name == null || name.trim().isEmpty()) && 
            id == null && 
            (genre == null || genre.trim().isEmpty())) {
            
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("searchPerformed", false);
            model.addAttribute("pirateMessage", "Ahoy matey! Welcome to our treasure trove of movies! Use the search form below to find yer perfect cinematic adventure.");
            return "movies";
        }
        
        try {
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                model.addAttribute("movies", List.of());
                model.addAttribute("searchPerformed", true);
                model.addAttribute("errorMessage", "Shiver me timbers! That ID be as useless as a compass that spins! Provide a proper movie ID, ye scallywag.");
                model.addAttribute("searchCriteria", createSearchCriteriaMap(name, id, genre));
                return "movies";
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            model.addAttribute("movies", searchResults);
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchCriteria", createSearchCriteriaMap(name, id, genre));
            
            if (searchResults.isEmpty()) {
                model.addAttribute("pirateMessage", "Blimey! No treasure found with those search terms, matey. Try different criteria or check yer spellin'!");
            } else {
                model.addAttribute("pirateMessage", String.format("Ahoy! Found %d fine pieces of cinematic treasure for ye, me hearty!", searchResults.size()));
            }
            
        } catch (Exception e) {
            logger.error("Error occurred during movie search page: {}", e.getMessage(), e);
            model.addAttribute("movies", List.of());
            model.addAttribute("searchPerformed", true);
            model.addAttribute("errorMessage", "Batten down the hatches! Something went wrong with yer search. Try again later, ye brave soul!");
        }
        
        return "movies";
    }
    
    /**
     * Helper method to create search criteria map for response
     */
    private Map<String, Object> createSearchCriteriaMap(String name, Long id, String genre) {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("name", name);
        criteria.put("id", id);
        criteria.put("genre", genre);
        return criteria;
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}