# 🏴‍☠️ Movie Search API Documentation

Ahoy, fellow developers! This document provides comprehensive technical documentation for the Movie Search API with its swashbuckling pirate theme.

## 📋 Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [Base URL](#base-url)
- [Endpoints](#endpoints)
- [Data Models](#data-models)
- [Error Handling](#error-handling)
- [Examples](#examples)
- [Rate Limiting](#rate-limiting)
- [Changelog](#changelog)

## 🌊 Overview

The Movie Search API provides powerful search capabilities for the Pirate's Movie Treasure Trove application. It supports both REST API endpoints for programmatic access and HTML form-based search for web interface interactions.

### Key Features

- **Multi-criteria Search**: Search by movie name, ID, and/or genre
- **Case-insensitive Matching**: All text searches ignore case
- **Partial String Matching**: Name and genre support substring matching
- **Pirate-themed Responses**: All messages use nautical terminology
- **Comprehensive Error Handling**: Detailed error messages with appropriate HTTP status codes
- **Input Validation**: Robust validation for all parameters

## 🔐 Authentication

Currently, no authentication is required for the Movie Search API. All endpoints are publicly accessible.

## 🌐 Base URL

```
http://localhost:8080
```

For production deployments, replace with your actual domain.

## 🛠️ Endpoints

### 1. Search Movies (REST API)

**Endpoint:** `GET /movies/search`

**Description:** Search for movies using various criteria and receive JSON response with pirate-themed messages.

**Parameters:**

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `name` | String | No* | Movie name (partial match, case-insensitive) | `Prison`, `Hero`, `Sci` |
| `id` | Long | No* | Movie ID (exact match) | `1`, `5`, `12` |
| `genre` | String | No* | Movie genre (partial match, case-insensitive) | `Drama`, `Action`, `Sci-Fi` |

*At least one parameter is required.

**Response Format:**

```json
{
  "success": boolean,
  "message": "string (pirate-themed)",
  "movies": [Movie],
  "searchCriteria": {
    "name": "string or null",
    "id": "number or null", 
    "genre": "string or null"
  }
}
```

**HTTP Status Codes:**

- `200 OK`: Search completed successfully (even if no results found)
- `400 Bad Request`: Invalid parameters or no search criteria provided
- `500 Internal Server Error`: Server error occurred

### 2. Search Movies (HTML Interface)

**Endpoint:** `GET /movies/search-page`

**Description:** Search for movies and display results in HTML format with pirate-themed interface.

**Parameters:** Same as REST API endpoint

**Response:** HTML page with search results and form

### 3. Get All Movies (HTML)

**Endpoint:** `GET /movies`

**Description:** Display all movies with search form interface.

**Parameters:** None

**Response:** HTML page with all movies and search form

## 📊 Data Models

### Movie

```json
{
  "id": 1,
  "movieName": "The Prison Escape",
  "director": "John Director", 
  "year": 1994,
  "genre": "Drama",
  "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
  "duration": 142,
  "imdbRating": 5.0,
  "icon": "🎬"
}
```

### Search Response

```json
{
  "success": true,
  "message": "Ahoy! Found 2 fine pieces of cinematic treasure for ye, me hearty!",
  "movies": [Movie],
  "searchCriteria": {
    "name": "Prison",
    "id": null,
    "genre": "Drama"
  }
}
```

### Error Response

```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search criterion, matey!",
  "movies": [],
  "searchCriteria": null
}
```

## ⚠️ Error Handling

### Common Error Scenarios

#### 1. No Search Parameters (400 Bad Request)

**Request:** `GET /movies/search`

**Response:**
```json
{
  "success": false,
  "message": "Arrr! Ye need to provide at least one search criterion, matey! Try searchin' by name, id, or genre.",
  "movies": []
}
```

#### 2. Invalid Movie ID (400 Bad Request)

**Request:** `GET /movies/search?id=-1`

**Response:**
```json
{
  "success": false,
  "message": "Shiver me timbers! That ID be as useless as a compass that spins! Provide a proper movie ID, ye scallywag.",
  "movies": []
}
```

#### 3. No Results Found (200 OK)

**Request:** `GET /movies/search?name=NonExistentMovie`

**Response:**
```json
{
  "success": true,
  "message": "Blimey! No treasure found with those search terms, matey. Try different criteria or check yer spellin'!",
  "movies": [],
  "searchCriteria": {
    "name": "NonExistentMovie",
    "id": null,
    "genre": null
  }
}
```

#### 4. Server Error (500 Internal Server Error)

**Response:**
```json
{
  "success": false,
  "message": "Batten down the hatches! Something went wrong with yer search. Try again later, ye brave soul!",
  "movies": []
}
```

## 📝 Examples

### Example 1: Search by Movie Name

**Request:**
```bash
curl "http://localhost:8080/movies/search?name=Prison"
```

**Response:**
```json
{
  "success": true,
  "message": "Ahoy! Found 1 fine pieces of cinematic treasure for ye, me hearty!",
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
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

### Example 2: Search by Genre

**Request:**
```bash
curl "http://localhost:8080/movies/search?genre=Action"
```

**Response:**
```json
{
  "success": true,
  "message": "Ahoy! Found 3 fine pieces of cinematic treasure for ye, me hearty!",
  "movies": [
    {
      "id": 3,
      "movieName": "The Masked Hero",
      "director": "Chris Moviemaker",
      "year": 2008,
      "genre": "Action/Crime",
      "description": "When a menacing villain wreaks havoc and chaos on the people of the city, a masked hero must accept one of the greatest psychological and physical tests.",
      "duration": 152,
      "imdbRating": 5.0
    }
  ],
  "searchCriteria": {
    "name": null,
    "id": null,
    "genre": "Action"
  }
}
```

### Example 3: Combined Search

**Request:**
```bash
curl "http://localhost:8080/movies/search?name=Hero&genre=Action"
```

**Response:**
```json
{
  "success": true,
  "message": "Ahoy! Found 1 fine pieces of cinematic treasure for ye, me hearty!",
  "movies": [
    {
      "id": 3,
      "movieName": "The Masked Hero",
      "director": "Chris Moviemaker",
      "year": 2008,
      "genre": "Action/Crime",
      "description": "When a menacing villain wreaks havoc and chaos on the people of the city, a masked hero must accept one of the greatest psychological and physical tests.",
      "duration": 152,
      "imdbRating": 5.0
    }
  ],
  "searchCriteria": {
    "name": "Hero",
    "id": null,
    "genre": "Action"
  }
}
```

### Example 4: Search by ID

**Request:**
```bash
curl "http://localhost:8080/movies/search?id=5"
```

**Response:**
```json
{
  "success": true,
  "message": "Ahoy! Found 1 fine pieces of cinematic treasure for ye, me hearty!",
  "movies": [
    {
      "id": 5,
      "movieName": "Life Journey",
      "director": "Robert Filmmaker",
      "year": 1994,
      "genre": "Drama/Romance",
      "description": "The presidencies of Kennedy and Johnson, the Vietnam War, and other historical events unfold from the perspective of an Alabama man with an IQ of 75.",
      "duration": 142,
      "imdbRating": 4.0
    }
  ],
  "searchCriteria": {
    "name": null,
    "id": 5,
    "genre": null
  }
}
```

## 🚦 Rate Limiting

Currently, no rate limiting is implemented. For production deployments, consider implementing rate limiting to prevent abuse.

**Recommended Limits:**
- 100 requests per minute per IP address
- 1000 requests per hour per IP address

## 🔍 Search Behavior Details

### Text Matching Rules

1. **Case Insensitive**: "PRISON" matches "The Prison Escape"
2. **Partial Matching**: "Sci" matches "Sci-Fi Adventure"
3. **Whitespace Trimming**: "  Drama  " is treated as "Drama"
4. **Special Characters**: Handled as literal characters in search

### ID Matching Rules

1. **Exact Match Only**: ID must match exactly
2. **Positive Numbers Only**: IDs must be greater than 0
3. **Long Type**: Accepts long integer values

### Multiple Criteria Logic

When multiple search criteria are provided, ALL criteria must match (AND logic):
- `name=Prison&genre=Drama` finds movies that contain "Prison" in name AND "Drama" in genre
- `name=Hero&id=3&genre=Action` finds movies that match all three criteria

## 🧪 Testing the API

### Using cURL

```bash
# Basic search
curl "http://localhost:8080/movies/search?name=Prison"

# Search with multiple parameters
curl "http://localhost:8080/movies/search?name=Hero&genre=Action"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Test error handling
curl "http://localhost:8080/movies/search"
curl "http://localhost:8080/movies/search?id=-1"
```

### Using JavaScript (Fetch API)

```javascript
// Search by name
fetch('/movies/search?name=Prison')
  .then(response => response.json())
  .then(data => {
    if (data.success) {
      console.log('Found movies:', data.movies);
      console.log('Message:', data.message);
    } else {
      console.error('Search failed:', data.message);
    }
  });

// Combined search
fetch('/movies/search?name=Hero&genre=Action')
  .then(response => response.json())
  .then(data => console.log(data));
```

## 📈 Performance Considerations

### Current Implementation

- **In-Memory Search**: All movies are loaded into memory and searched linearly
- **No Caching**: Each request processes the full dataset
- **Time Complexity**: O(n) where n is the number of movies

### Optimization Recommendations

For larger datasets, consider:

1. **Database Integration**: Use SQL with indexed columns
2. **Caching**: Implement Redis or in-memory caching for frequent searches
3. **Pagination**: Add pagination for large result sets
4. **Search Indexing**: Use Elasticsearch or similar for advanced search capabilities

## 🔄 Changelog

### Version 1.0.0 (Current)

**Added:**
- Movie search by name, ID, and genre
- Pirate-themed response messages
- Comprehensive error handling
- HTML form interface
- REST API endpoints
- Input validation and sanitization

**Features:**
- Case-insensitive text search
- Partial string matching for name and genre
- Exact ID matching
- Multiple criteria support (AND logic)
- Detailed error messages with HTTP status codes

---

*Arrr! May this documentation guide ye safely through the treacherous waters of API integration! 🏴‍☠️*