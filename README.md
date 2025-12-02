# 🏴‍☠️ Pirate's Movie Treasure Trove - Spring Boot Demo Application

Ahoy matey! Welcome to the finest movie catalog web application on the seven seas, built with Spring Boot and featuring a swashbuckling pirate theme!

## ⚓ Features

- **🎬 Movie Catalog**: Browse 12 classic cinematic treasures with detailed information
- **🔍 Advanced Search**: Hunt for movies by name, ID, or genre with our powerful search functionality
- **📋 Movie Details**: View comprehensive information including captain director, year, adventure type, journey length, and description
- **⭐ Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **📱 Responsive Design**: Mobile-first design that works on all devices, from ship to shore
- **🎨 Pirate-Themed UI**: Dark nautical theme with treasure-inspired gradients and smooth animations
- **🗺️ Interactive Search Form**: Easy-to-use search interface with pirate language and helpful guidance

## 🛠️ Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Maven** for dependency management
- **Thymeleaf** for templating
- **Log4j 2** for logging
- **JUnit 5.8.2** for testing
- **JSON** for data handling

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **🏴‍☠️ Movie Treasure Trove**: http://localhost:8080/movies
- **🔍 Search Movies**: http://localhost:8080/movies/search-page
- **📋 Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)

## 🔍 Search Functionality

### Web Interface Search

Navigate to the main movies page where ye'll find a prominent search form with the following options:

- **🎬 Movie Name**: Search for any part of the movie title (e.g., "Prison" will find "The Prison Escape")
- **🆔 Movie ID**: Enter the exact ID number if ye know it (1-12 in our treasure chest)
- **🎭 Genre**: Search by movie type (Drama, Action, Crime, Sci-Fi, Adventure, Fantasy, etc.)

**Pro Tip**: Ye can combine multiple search criteria to narrow down yer hunt!

### REST API Search

For programmatic access, use our REST API endpoint:

```
GET /movies/search?name={name}&id={id}&genre={genre}
```

**Parameters** (all optional, but at least one required):
- `name`: Movie name (partial match, case-insensitive)
- `id`: Movie ID (exact match)
- `genre`: Movie genre (partial match, case-insensitive)

**Example Requests:**
```bash
# Search by name
curl "http://localhost:8080/movies/search?name=Prison"

# Search by genre
curl "http://localhost:8080/movies/search?genre=Drama"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=Hero&genre=Action"
```

**Response Format:**
```json
{
  "success": true,
  "message": "Ahoy! Found 2 fine pieces of cinematic treasure for ye, me hearty!",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0
    }
  ],
  "searchCriteria": {
    "name": "Prison",
    "id": null,
    "genre": null
  }
}
```

**Error Responses:**
- **400 Bad Request**: When no search parameters are provided or invalid ID
- **500 Internal Server Error**: When system errors occur

All error messages include pirate-themed language for a fun user experience!

## 🏗️ Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Business logic with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── templates/
│       │   ├── movies.html                   # Enhanced movie listing with search form
│       │   └── movie-details.html            # Movie details page
│       ├── static/css/                       # Stylesheets
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       └── log4j2.xml                        # Logging configuration
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MoviesControllerTest.java     # Controller tests with search scenarios
            ├── MovieServiceTest.java         # Service layer tests
            └── MovieTest.java                # Model tests
```

## 🌐 API Endpoints

### Get All Movies (HTML)
```
GET /movies
```
Returns an HTML page displaying all movies with search form and pirate-themed interface.

### Search Movies (HTML)
```
GET /movies/search-page?name={name}&id={id}&genre={genre}
```
Returns an HTML page with search results and maintains search criteria in the form.

### Search Movies (REST API)
```
GET /movies/search?name={name}&id={id}&genre={genre}
```
Returns JSON response with search results and pirate-themed messages.

**Parameters:**
- `name` (optional): Movie name for partial matching
- `id` (optional): Movie ID for exact matching  
- `genre` (optional): Movie genre for partial matching

**Example:**
```
http://localhost:8080/movies/search?name=Prison&genre=Drama
```

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## 🧪 Testing

Run the comprehensive test suite:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MovieServiceTest

# Run with coverage
mvn test jacoco:report
```

The test suite includes:
- **Unit Tests**: Service layer search functionality
- **Integration Tests**: Controller endpoints with various scenarios
- **Edge Case Tests**: Invalid parameters, empty results, error conditions
- **Pirate Language Tests**: Verification of themed messages

## 🔧 Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that at least one search parameter is provided
2. Verify movie data is loaded (check logs for JSON loading messages)
3. Test with simple searches first (e.g., single character in name field)

### Pirate language not displaying

1. Ensure Thymeleaf templates are properly loaded
2. Check browser console for JavaScript errors
3. Verify model attributes are being set in controller

## 🤝 Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the treasure chest
- Enhance the pirate-themed UI/UX
- Improve search functionality (fuzzy matching, sorting, etc.)
- Add new features like favorites or watchlists
- Expand the responsive design
- Add more comprehensive error handling

## 📝 Development Notes

### Search Implementation Details

- **Case-insensitive**: All text searches ignore case
- **Partial matching**: Name and genre support partial string matching
- **Exact ID matching**: Movie ID requires exact match
- **Multiple criteria**: All provided criteria must match (AND logic)
- **Input validation**: Proper handling of null, empty, and whitespace-only inputs
- **Error handling**: Comprehensive error messages with pirate theme

### Performance Considerations

- **In-memory search**: Current implementation searches loaded movie list
- **No caching**: Each search processes the full dataset
- **Scalability**: For larger datasets, consider database integration with indexed searches

## 📄 License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye explore this cinematic treasure trove! 🏴‍☠️*
