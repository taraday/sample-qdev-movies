package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Should load movies from movies.json
        assertTrue(movies.size() > 0);
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNull() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdZero() {
        Optional<Movie> movie = movieService.getMovieById(0L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdNegative() {
        Optional<Movie> movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }

    // ===== SEARCH FUNCTIONALITY TESTS =====

    @Test
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find "The Prison Escape"
        assertTrue(results.stream().anyMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("prison")));
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find "The Prison Escape" regardless of case
        assertTrue(results.stream().anyMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("prison")));
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("Hero", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find "The Masked Hero"
        assertTrue(results.stream().anyMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("hero")));
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find movies with Drama genre
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find movies with Drama genre regardless of case
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Sci");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find movies with Sci-Fi genre
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("sci")));
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        // Search for a movie that matches both name and genre
        List<Movie> results = movieService.searchMovies("Prison", null, "Drama");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should find movies that match both criteria
        assertTrue(results.stream().allMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("prison") && 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesMultipleCriteriaNoMatch() {
        // Search for criteria that don't match any movie
        List<Movie> results = movieService.searchMovies("Prison", null, "Action");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesAllCriteria() {
        // Search using all three criteria
        List<Movie> results = movieService.searchMovies("Prison", 1L, "Drama");
        assertNotNull(results);
        
        if (!results.isEmpty()) {
            // If found, should match all criteria
            Movie movie = results.get(0);
            assertEquals(1L, movie.getId());
            assertTrue(movie.getMovieName().toLowerCase().contains("prison"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesNoParameters() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesEmptyStrings() {
        List<Movie> results = movieService.searchMovies("", null, "");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesWhitespaceStrings() {
        List<Movie> results = movieService.searchMovies("   ", null, "   ");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesNonExistentName() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesNonExistentGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "NonExistentGenre");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesInvalidId() {
        List<Movie> results = movieService.searchMovies(null, -1L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesZeroId() {
        List<Movie> results = movieService.searchMovies(null, 0L, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesNameWithSpaces() {
        List<Movie> results = movieService.searchMovies("  Prison  ", null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should trim spaces and find "The Prison Escape"
        assertTrue(results.stream().anyMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("prison")));
    }

    @Test
    public void testSearchMoviesGenreWithSpaces() {
        List<Movie> results = movieService.searchMovies(null, null, "  Drama  ");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Should trim spaces and find Drama movies
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMoviesSpecialCharacters() {
        // Test with movie names that might contain special characters
        List<Movie> results = movieService.searchMovies("Sci-Fi", null, null);
        assertNotNull(results);
        
        // Should handle special characters in search
        if (!results.isEmpty()) {
            assertTrue(results.stream().anyMatch(movie -> 
                movie.getMovieName().toLowerCase().contains("sci-fi") ||
                movie.getGenre().toLowerCase().contains("sci-fi")));
        }
    }

    @Test
    public void testSearchMoviesResultsConsistency() {
        // Test that multiple calls return consistent results
        List<Movie> results1 = movieService.searchMovies("Drama", null, null);
        List<Movie> results2 = movieService.searchMovies("Drama", null, null);
        
        assertNotNull(results1);
        assertNotNull(results2);
        assertEquals(results1.size(), results2.size());
        
        // Results should be in the same order
        for (int i = 0; i < results1.size(); i++) {
            assertEquals(results1.get(i).getId(), results2.get(i).getId());
        }
    }
}