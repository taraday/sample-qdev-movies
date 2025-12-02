package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Hero", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Sci-Fi Adventure", "Sci-Fi Director", 2021, "Sci-Fi", "Sci-Fi description", 130, 4.8)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(movie -> movie.getId() == id).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> results = new ArrayList<>();
                for (Movie movie : testMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = matches && movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim());
                    }
                    
                    if (id != null && id > 0) {
                        matches = matches && movie.getId() == id;
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        matches = matches && movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                return results;
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    // ===== NEW SEARCH FUNCTIONALITY TESTS =====

    @Test
    public void testSearchMoviesByName() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("Test", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Ahoy! Found 1 fine pieces"));
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("action", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesById() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, 2L, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByGenre() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, "Sci-Fi");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Sci-Fi Adventure", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("Action", 2L, "Action");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesNoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("NonExistent", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(0, movies.size());
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Blimey! No treasure found"));
    }

    @Test
    public void testSearchMoviesNoParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Arrr! Ye need to provide at least one search criterion"));
    }

    @Test
    public void testSearchMoviesEmptyParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("", null, "");
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
    }

    @Test
    public void testSearchMoviesInvalidId() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, -1L, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Shiver me timbers! That ID be as useless"));
    }

    @Test
    public void testSearchMoviesZeroId() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, 0L, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
    }

    @Test
    public void testSearchMoviesPageWithResults() {
        String result = moviesController.searchMoviesPage("Test", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("pirateMessage"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        
        Boolean searchPerformed = (Boolean) model.getAttribute("searchPerformed");
        assertTrue(searchPerformed);
        
        String pirateMessage = (String) model.getAttribute("pirateMessage");
        assertTrue(pirateMessage.contains("Ahoy! Found 1 fine pieces"));
    }

    @Test
    public void testSearchMoviesPageNoResults() {
        String result = moviesController.searchMoviesPage("NonExistent", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("pirateMessage"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        
        String pirateMessage = (String) model.getAttribute("pirateMessage");
        assertTrue(pirateMessage.contains("Blimey! No treasure found"));
    }

    @Test
    public void testSearchMoviesPageNoParameters() {
        String result = moviesController.searchMoviesPage(null, null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("pirateMessage"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should return all movies
        
        Boolean searchPerformed = (Boolean) model.getAttribute("searchPerformed");
        assertFalse(searchPerformed);
        
        String pirateMessage = (String) model.getAttribute("pirateMessage");
        assertTrue(pirateMessage.contains("Ahoy matey! Welcome to our treasure trove"));
    }

    @Test
    public void testSearchMoviesPageInvalidId() {
        String result = moviesController.searchMoviesPage(null, -5L, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchPerformed"));
        assertTrue(model.containsAttribute("errorMessage"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        
        String errorMessage = (String) model.getAttribute("errorMessage");
        assertTrue(errorMessage.contains("Shiver me timbers! That ID be as useless"));
    }

    @Test
    public void testSearchCriteriaMapCreation() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("Test", 1L, "Drama");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("searchCriteria"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("Test", searchCriteria.get("name"));
        assertEquals(1L, searchCriteria.get("id"));
        assertEquals("Drama", searchCriteria.get("genre"));
    }

    @Test
    public void testPartialNameMatch() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies("Sci", null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Sci-Fi Adventure", movies.get(0).getMovieName());
    }

    @Test
    public void testPartialGenreMatch() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMovies(null, null, "Act");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Hero", movies.get(0).getMovieName());
    }
}
