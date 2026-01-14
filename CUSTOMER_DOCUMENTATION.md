# Customer Guide: News API

This guide describes every News API endpoint, required inputs, and the full response shapes (including success, empty, validation, authentication, and server error variants). It is written for external integrators with no prior context.

## Base URL and Versioning
- Default production base URL: `https://newsapi.one`
- Example local base URL (development): `http://localhost:8080`
- All endpoints are under `/v1`.

## Authentication
Most endpoints require an API key.
- Preferred header: `X-API-KEY: <your-key>`
- Alternate query param: `?apiKey=<your-key>`
- Public endpoints (no API key required): `/v1/auth/**`, `/v1/health/**`, `/v1/test/public`, `/actuator/**`.
- Missing or invalid API key returns HTTP 401 with a plain text response body:

```text
Unauthorized: Missing or invalid API key
```

- Note: the API key filter runs on all requests. If you send an invalid API key to a public endpoint, you will still receive the 401 response shown above.
- Admin-only operations use an `adminKey` value (query or body depending on endpoint). Invalid admin keys return a documented 400 or 401 response per endpoint.

## Content Types
- Requests with JSON bodies must set `Content-Type: application/json`.
- Successful responses are JSON unless otherwise noted.
- Authentication failures return plain text.

## Date and Time Formats
- Date-only filters use `yyyy-MM-dd` (UTC).
- Timestamps in responses use ISO-8601 strings such as `2024-05-24T12:01:00Z`.
- Pagination is zero-based: `page=0` is the first page.

## Response Formats

### ApiResponse envelope (most endpoints)
```json
{
  "status": "ok",
  "message": "string or null",
  "data": {}
}
```

### PaginatedResponse (used inside `data`)
```json
{
  "samples": [],
  "totalItems": 0,
  "totalPages": 0,
  "currentPage": 0,
  "pageSize": 10
}
```

### Error responses (GlobalExceptionHandler)
Validation, not-found, and server errors use this JSON structure:

```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Not Found",
  "message": "Resource not found with id: 6650f1c2a4d8f93a2a12b7c9"
}
```

```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### Authentication error (plain text)
```text
Unauthorized: Missing or invalid API key
```

### Non-ApiResponse endpoints
The following endpoints return raw JSON objects (no ApiResponse envelope):
- `GET /v1/health`
- `GET /v1/test/public`
- `GET /v1/test/secured`
- `POST /v1/auth/generate-key`

## Data Models (Response Objects)

### Article
Fields (all fields are always present in documentation; nullable fields are explicitly shown as null in examples):
- `id` (string)
- `title` (string)
- `url` (string)
- `summary` (string, nullable)
- `text` (string, nullable)
- `source` (string)
- `scraped_at` (string, ISO-8601)
- `topic` (string, nullable)
- `sentiment` (object, nullable)
  - `label` (string)
  - `score` (number)
- `sample` (string, nullable)
- `locations` (array of strings, nullable)
- `organizations` (array of strings, nullable)
- `persons` (array of strings, nullable)

### EntityCount
Fields:
- `entity` (string)
- `count` (integer)

### EventDTO (full event shape)
Fields:
- `id` (string)
- `label` (string)
- `summary` (string, nullable)
- `category` (string)
- `language` (string)
- `location` (string, nullable)
- `locationConfidence` (number, nullable)
- `locationGranularity` (string, nullable)
- `locationSource` (string, nullable)
- `lat` (number, nullable)
- `lng` (number, nullable)
- `url` (string, nullable)
- `country` (string, nullable)
- `region` (string, nullable)
- `source` (string)
- `tags` (array of strings)
- `centroidEmbedding` (array of numbers)
- `firstSeenAt` (string, ISO-8601)
- `lastSeenAt` (string, ISO-8601, nullable)
- `articleIds` (array of strings)
- `importanceScore` (integer, nullable)
- `status` (string)
- `sourceStats` (object/map of string to integer)
- `miscFlags` (object/map of string to mixed values)
- `verified` (boolean, nullable)
- `verifiedAt` (string, ISO-8601, nullable)
- `createdBy` (string, nullable)

### Event (lite/raw event shape)
Fields:
- `id` (string)
- `title` (string)
- `timestamp` (string, ISO-8601)
- `lat` (number, defaults to `0.0` when unknown)
- `lng` (number, defaults to `0.0` when unknown)
- `type` (string, nullable)
- `category` (string)
- `url` (string, nullable)
- `description` (string, nullable)
- `language` (string)
- `location` (string, nullable)
- `status` (string)
- `country` (string, nullable)
- `region` (string, nullable)
- `source` (string)
- `verified` (boolean, nullable; omitted by the API when null but shown as null in examples for clarity)
- `createdBy` (string, nullable)

### PipelineLog
Fields:
- `id` (string)
- `ts` (string, ISO-8601)
- `actor` (string)
- `action` (string)
- `articleId` (string, nullable)
- `eventId` (string, nullable)
- `status` (string)
- `details` (object/map, nullable)
- `errorMessage` (string, nullable)

### RequestLog
Fields (note: `id` is not included in responses):
- `path` (string)
- `method` (string)
- `ip` (string)
- `apiKey` (string)
- `timestamp` (string, ISO-8601 without timezone, e.g. `2024-05-24T12:10:15`)
- `success` (boolean)
- `statusCode` (integer)
- `errorMessage` (string, nullable)
- `durationMillis` (integer)
- `costMicros` (integer, nullable; `0` for free endpoints)
- `billed` (boolean, nullable)
- `balanceAfterMicros` (integer, nullable)
- `requestQuery` (string, nullable)
- `requestBody` (string, nullable)
- `responseBody` (string, nullable)

### RequestLogAnalyticsResponse
Extends `PaginatedResponse<RequestLog>` and adds:
- `successCount` (integer)
- `failureCount` (integer)
- `averageDurationMillis` (number)

### ApiKey (returned by `POST /v1/auth/generate-key`)
Fields:
- `id` (string)
- `user` (string)
- `key` (string)
- `active` (boolean)
- `creditsMicros` (integer, nullable)
- `createdAt` (string, ISO-8601)
- `lastChargedAt` (string, ISO-8601)
- `plan` (string, nullable)

### EventDeleteResponse
Fields:
- `id` (string)
- `title` (string, nullable)

### HealthResponse
Fields:
- `status` (string, e.g. `UP` or `DOWN`)
- `details` (object/map of component name to component status and details)

### Test responses
- `/v1/test/public`: `{ "message": "..." }`
- `/v1/test/secured`: `{ "message": "...", "apiKeyUsed": "..." }`

## Quick Start
1) Health check (public): `curl https://newsapi.one/v1/health`
2) Fetch news (secured): `curl -H "X-API-KEY: <key>" "https://newsapi.one/v1/news?size=5"`
3) Fetch events by category (secured): `curl -H "X-API-KEY: <key>" "https://newsapi.one/v1/events/by-category?value=politics"`
4) View request metrics (public, admin key required in query): `curl "https://newsapi.one/v1/auth/logs/filtered?apiKey=<key>&adminKey=<admin-key>"`
5) Validate API key (secured): `curl -H "X-API-KEY: <key>" https://newsapi.one/v1/test/secured`

## Articles

### GET /v1/news
Paginated list of articles sorted by `scrapedAt`.

Authentication: Required API key.

Query params:
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated news articles",
  "data": {
    "samples": [
      {
        "id": "6650f1c2a4d8f93a2a12b7c9",
        "title": "Central bank signals rate pause",
        "url": "https://news.example.com/markets/central-bank-pause",
        "summary": "Policy makers indicated a likely pause after a year of hikes.",
        "text": "Full article text about policy makers and market reaction.",
        "source": "Reuters",
        "scraped_at": "2024-05-24T12:01:00Z",
        "topic": "economy",
        "sentiment": {
          "label": "neutral",
          "score": 0.11
        },
        "sample": "Policy makers indicated a likely pause ...",
        "locations": ["Washington, DC", "New York"],
        "organizations": ["Federal Reserve", "IMF"],
        "persons": ["Jane Doe", "John Smith"]
      }
    ],
    "totalItems": 1200,
    "totalPages": 600,
    "currentPage": 0,
    "pageSize": 2
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated news articles",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid query parameter
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/news/by-topic
Articles filtered by exact topic.

Authentication: Required API key.

Query params:
- `value` (string, required; topic)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated news articles by topic",
  "data": {
    "samples": [
      {
        "id": "6650f1c2a4d8f93a2a12b7c9",
        "title": "Central bank signals rate pause",
        "url": "https://news.example.com/markets/central-bank-pause",
        "summary": "Policy makers indicated a likely pause after a year of hikes.",
        "text": "Full article text about policy makers and market reaction.",
        "source": "Reuters",
        "scraped_at": "2024-05-24T12:01:00Z",
        "topic": "economy",
        "sentiment": {
          "label": "neutral",
          "score": 0.11
        },
        "sample": "Policy makers indicated a likely pause ...",
        "locations": ["Washington, DC", "New York"],
        "organizations": ["Federal Reserve", "IMF"],
        "persons": ["Jane Doe", "John Smith"]
      }
    ],
    "totalItems": 312,
    "totalPages": 32,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated news articles by topic",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid topic
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/news/by-date
Articles filtered by scrape date (UTC).

Authentication: Required API key.

Query params:
- `value` (string, required; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated news articles by date",
  "data": {
    "samples": [
      {
        "id": "6650f1c2a4d8f93a2a12b7c9",
        "title": "Central bank signals rate pause",
        "url": "https://news.example.com/markets/central-bank-pause",
        "summary": "Policy makers indicated a likely pause after a year of hikes.",
        "text": "Full article text about policy makers and market reaction.",
        "source": "Reuters",
        "scraped_at": "2024-05-24T12:01:00Z",
        "topic": "economy",
        "sentiment": {
          "label": "neutral",
          "score": 0.11
        },
        "sample": "Policy makers indicated a likely pause ...",
        "locations": ["Washington, DC", "New York"],
        "organizations": ["Federal Reserve", "IMF"],
        "persons": ["Jane Doe", "John Smith"]
      }
    ],
    "totalItems": 120,
    "totalPages": 12,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated news articles by date",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid date format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/news/by-title
Articles filtered by case-insensitive title match.

Authentication: Required API key.

Query params:
- `value` (string, required; title substring)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated news articles by title",
  "data": {
    "samples": [
      {
        "id": "6650f1c2a4d8f93a2a12b7c9",
        "title": "Central bank signals rate pause",
        "url": "https://news.example.com/markets/central-bank-pause",
        "summary": "Policy makers indicated a likely pause after a year of hikes.",
        "text": "Full article text about policy makers and market reaction.",
        "source": "Reuters",
        "scraped_at": "2024-05-24T12:01:00Z",
        "topic": "economy",
        "sentiment": {
          "label": "neutral",
          "score": 0.11
        },
        "sample": "Policy makers indicated a likely pause ...",
        "locations": ["Washington, DC", "New York"],
        "organizations": ["Federal Reserve", "IMF"],
        "persons": ["Jane Doe", "John Smith"]
      }
    ],
    "totalItems": 44,
    "totalPages": 5,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated news articles by title",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid title value
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/news/top-entities
Top entities for a date range, grouped by a selected entity type.

Authentication: Required API key.

Query params:
- `start_date` (string, required; format `yyyy-MM-dd`)
- `end_date` (string, required; format `yyyy-MM-dd`, inclusive)
- `entity` (string, required; one of `persons`, `locations`, `organizations`)
- `limit` (int, required; range `1`-`100`)

#### Responses
##### Success (200) - Top list
```json
{
  "status": "ok",
  "message": "Top entities by frequency",
  "data": [
    { "entity": "Federal Reserve", "count": 12 },
    { "entity": "IMF", "count": 9 },
    { "entity": "Jane Doe", "count": 7 }
  ]
}
```

##### Success (200) - Empty list
```json
{
  "status": "ok",
  "message": "Top entities by frequency",
  "data": []
}
```

##### Validation error (400) - Invalid inputs
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "entity must be one of: persons, locations, organizations."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/news/{id}
Fetch a single article by ID.

Authentication: Required API key.

Path params:
- `id` (string, required)

#### Responses
##### Success (200) - Full object
```json
{
  "status": "ok",
  "message": "Article details",
  "data": {
    "id": "6650f1c2a4d8f93a2a12b7c9",
    "title": "Central bank signals rate pause",
    "url": "https://news.example.com/markets/central-bank-pause",
    "summary": "Policy makers indicated a likely pause after a year of hikes.",
    "text": "Full article text about policy makers and market reaction.",
    "source": "Reuters",
    "scraped_at": "2024-05-24T12:01:00Z",
    "topic": "economy",
    "sentiment": {
      "label": "neutral",
      "score": 0.11
    },
    "sample": "Policy makers indicated a likely pause ...",
    "locations": ["Washington, DC", "New York"],
    "organizations": ["Federal Reserve", "IMF"],
    "persons": ["Jane Doe", "John Smith"]
  }
}
```

##### Success (200) - Partial object (nullable fields present)
```json
{
  "status": "ok",
  "message": "Article details",
  "data": {
    "id": "6650f1c2a4d8f93a2a12b7ca",
    "title": "Flooding disrupts transit in Venice",
    "url": "https://news.example.com/europe/venice-flooding",
    "summary": null,
    "text": null,
    "source": "AP",
    "scraped_at": "2024-05-25T09:15:00Z",
    "topic": "weather",
    "sentiment": {
      "label": "negative",
      "score": -0.62
    },
    "sample": null,
    "locations": null,
    "organizations": null,
    "persons": null
  }
}
```

##### Validation error (400) - Invalid ID format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Not found (404)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Not Found",
  "message": "Article not found with id: 6650f1c2a4d8f93a2a12b7c9"
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

## Events (Full DTO)

### GET /v1/events
Paginated list of full events sorted by `firstSeenAt`.

Authentication: Required API key.

Query params:
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee123",
        "label": "Wildfire grows near city limits",
        "summary": "Evacuations underway as crews battle blaze.",
        "category": "disaster",
        "language": "en",
        "location": "Valencia, Spain",
        "locationConfidence": 0.82,
        "locationGranularity": "city",
        "locationSource": "geocoder",
        "lat": 39.4699,
        "lng": -0.3763,
        "url": "https://news.example.com/wildfire",
        "country": "ES",
        "region": "Valencian Community",
        "source": "Reuters",
        "tags": ["wildfire", "evacuation", "smoke"],
        "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
        "firstSeenAt": "2024-05-24T10:15:00Z",
        "lastSeenAt": "2024-05-24T14:45:00Z",
        "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
        "importanceScore": 78,
        "status": "active",
        "sourceStats": {"reuters": 5, "ap": 2},
        "miscFlags": {"hasVideo": true, "priority": "high"},
        "verified": true,
        "verifiedAt": "2024-05-24T11:00:00Z",
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 84,
    "totalPages": 9,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid pagination
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/by-category
Events filtered by category.

Authentication: Required API key.

Query params:
- `value` (string, required; category)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by category",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee123",
        "label": "Wildfire grows near city limits",
        "summary": "Evacuations underway as crews battle blaze.",
        "category": "disaster",
        "language": "en",
        "location": "Valencia, Spain",
        "locationConfidence": 0.82,
        "locationGranularity": "city",
        "locationSource": "geocoder",
        "lat": 39.4699,
        "lng": -0.3763,
        "url": "https://news.example.com/wildfire",
        "country": "ES",
        "region": "Valencian Community",
        "source": "Reuters",
        "tags": ["wildfire", "evacuation", "smoke"],
        "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
        "firstSeenAt": "2024-05-24T10:15:00Z",
        "lastSeenAt": "2024-05-24T14:45:00Z",
        "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
        "importanceScore": 78,
        "status": "active",
        "sourceStats": {"reuters": 5, "ap": 2},
        "miscFlags": {"hasVideo": true, "priority": "high"},
        "verified": true,
        "verifiedAt": "2024-05-24T11:00:00Z",
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 25,
    "totalPages": 3,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by category",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid category
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/by-date
Events filtered by `firstSeenAt` date (UTC).

Authentication: Required API key.

Query params:
- `value` (string, required; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by date",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee123",
        "label": "Wildfire grows near city limits",
        "summary": "Evacuations underway as crews battle blaze.",
        "category": "disaster",
        "language": "en",
        "location": "Valencia, Spain",
        "locationConfidence": 0.82,
        "locationGranularity": "city",
        "locationSource": "geocoder",
        "lat": 39.4699,
        "lng": -0.3763,
        "url": "https://news.example.com/wildfire",
        "country": "ES",
        "region": "Valencian Community",
        "source": "Reuters",
        "tags": ["wildfire", "evacuation", "smoke"],
        "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
        "firstSeenAt": "2024-05-24T10:15:00Z",
        "lastSeenAt": "2024-05-24T14:45:00Z",
        "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
        "importanceScore": 78,
        "status": "active",
        "sourceStats": {"reuters": 5, "ap": 2},
        "miscFlags": {"hasVideo": true, "priority": "high"},
        "verified": true,
        "verifiedAt": "2024-05-24T11:00:00Z",
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 18,
    "totalPages": 2,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by date",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid date format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/by-status
Events filtered by lifecycle status.

Authentication: Required API key.

Query params:
- `value` (string, required; status)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by status",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee123",
        "label": "Wildfire grows near city limits",
        "summary": "Evacuations underway as crews battle blaze.",
        "category": "disaster",
        "language": "en",
        "location": "Valencia, Spain",
        "locationConfidence": 0.82,
        "locationGranularity": "city",
        "locationSource": "geocoder",
        "lat": 39.4699,
        "lng": -0.3763,
        "url": "https://news.example.com/wildfire",
        "country": "ES",
        "region": "Valencian Community",
        "source": "Reuters",
        "tags": ["wildfire", "evacuation", "smoke"],
        "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
        "firstSeenAt": "2024-05-24T10:15:00Z",
        "lastSeenAt": "2024-05-24T14:45:00Z",
        "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
        "importanceScore": 78,
        "status": "active",
        "sourceStats": {"reuters": 5, "ap": 2},
        "miscFlags": {"hasVideo": true, "priority": "high"},
        "verified": true,
        "verifiedAt": "2024-05-24T11:00:00Z",
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 62,
    "totalPages": 7,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by status",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid status
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/by-created-by-and-verified
Events filtered by `created_by` and `verified` with optional date range.

Authentication: Required API key.

Query params:
- `created_by` (string, required)
- `verified` (boolean, required)
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy and verified",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee123",
        "label": "Wildfire grows near city limits",
        "summary": "Evacuations underway as crews battle blaze.",
        "category": "disaster",
        "language": "en",
        "location": "Valencia, Spain",
        "locationConfidence": 0.82,
        "locationGranularity": "city",
        "locationSource": "geocoder",
        "lat": 39.4699,
        "lng": -0.3763,
        "url": "https://news.example.com/wildfire",
        "country": "ES",
        "region": "Valencian Community",
        "source": "Reuters",
        "tags": ["wildfire", "evacuation", "smoke"],
        "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
        "firstSeenAt": "2024-05-24T10:15:00Z",
        "lastSeenAt": "2024-05-24T14:45:00Z",
        "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
        "importanceScore": 78,
        "status": "active",
        "sourceStats": {"reuters": 5, "ap": 2},
        "miscFlags": {"hasVideo": true, "priority": "high"},
        "verified": true,
        "verifiedAt": "2024-05-24T11:00:00Z",
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 12,
    "totalPages": 2,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy and verified",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid verified value or date
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/by-created-by-and-verified-null
Events filtered by `created_by` where `verified` is null, with optional date range.

Authentication: Required API key.

Query params:
- `created_by` (string, required)
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy with null verified",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee124",
        "label": "Protest blocks main highway",
        "summary": null,
        "category": "civil-unrest",
        "language": "en",
        "location": null,
        "locationConfidence": null,
        "locationGranularity": null,
        "locationSource": null,
        "lat": null,
        "lng": null,
        "url": null,
        "country": null,
        "region": null,
        "source": "local",
        "tags": [],
        "centroidEmbedding": [],
        "firstSeenAt": "2024-05-25T07:00:00Z",
        "lastSeenAt": null,
        "articleIds": [],
        "importanceScore": null,
        "status": "monitoring",
        "sourceStats": {},
        "miscFlags": {},
        "verified": null,
        "verifiedAt": null,
        "createdBy": "editor:alice"
      }
    ],
    "totalItems": 3,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy with null verified",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid date or created_by
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/by-created-by
Events filtered by `created_by` with optional date range.

Authentication: Required API key.

Query params:
- `value` (string, required; created_by)
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee123",
        "label": "Wildfire grows near city limits",
        "summary": "Evacuations underway as crews battle blaze.",
        "category": "disaster",
        "language": "en",
        "location": "Valencia, Spain",
        "locationConfidence": 0.82,
        "locationGranularity": "city",
        "locationSource": "geocoder",
        "lat": 39.4699,
        "lng": -0.3763,
        "url": "https://news.example.com/wildfire",
        "country": "ES",
        "region": "Valencian Community",
        "source": "Reuters",
        "tags": ["wildfire", "evacuation", "smoke"],
        "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
        "firstSeenAt": "2024-05-24T10:15:00Z",
        "lastSeenAt": "2024-05-24T14:45:00Z",
        "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
        "importanceScore": 78,
        "status": "active",
        "sourceStats": {"reuters": 5, "ap": 2},
        "miscFlags": {"hasVideo": true, "priority": "high"},
        "verified": true,
        "verifiedAt": "2024-05-24T11:00:00Z",
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 10,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid created_by
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/is-verified
Events filtered by verified flag with optional date range.

Authentication: Required API key.

Query params:
- `value` (boolean, required; verified)
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by verified status",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee123",
        "label": "Wildfire grows near city limits",
        "summary": "Evacuations underway as crews battle blaze.",
        "category": "disaster",
        "language": "en",
        "location": "Valencia, Spain",
        "locationConfidence": 0.82,
        "locationGranularity": "city",
        "locationSource": "geocoder",
        "lat": 39.4699,
        "lng": -0.3763,
        "url": "https://news.example.com/wildfire",
        "country": "ES",
        "region": "Valencian Community",
        "source": "Reuters",
        "tags": ["wildfire", "evacuation", "smoke"],
        "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
        "firstSeenAt": "2024-05-24T10:15:00Z",
        "lastSeenAt": "2024-05-24T14:45:00Z",
        "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
        "importanceScore": 78,
        "status": "active",
        "sourceStats": {"reuters": 5, "ap": 2},
        "miscFlags": {"hasVideo": true, "priority": "high"},
        "verified": true,
        "verifiedAt": "2024-05-24T11:00:00Z",
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 45,
    "totalPages": 5,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by verified status",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid verified value or date
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/is-verified-null
Events where `verified` is null, with optional date range.

Authentication: Required API key.

Query params:
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events with null verified",
  "data": {
    "samples": [
      {
        "id": "66510a9f2c4fdd9b1a0ee124",
        "label": "Protest blocks main highway",
        "summary": null,
        "category": "civil-unrest",
        "language": "en",
        "location": null,
        "locationConfidence": null,
        "locationGranularity": null,
        "locationSource": null,
        "lat": null,
        "lng": null,
        "url": null,
        "country": null,
        "region": null,
        "source": "local",
        "tags": [],
        "centroidEmbedding": [],
        "firstSeenAt": "2024-05-25T07:00:00Z",
        "lastSeenAt": null,
        "articleIds": [],
        "importanceScore": null,
        "status": "monitoring",
        "sourceStats": {},
        "miscFlags": {},
        "verified": null,
        "verifiedAt": null,
        "createdBy": "editor:alice"
      }
    ],
    "totalItems": 5,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events with null verified",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid date
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/{id}
Fetch a single full event by ID.

Authentication: Required API key.

Path params:
- `id` (string, required)

#### Responses
##### Success (200) - Full object
```json
{
  "status": "ok",
  "message": "Event details",
  "data": {
    "id": "66510a9f2c4fdd9b1a0ee123",
    "label": "Wildfire grows near city limits",
    "summary": "Evacuations underway as crews battle blaze.",
    "category": "disaster",
    "language": "en",
    "location": "Valencia, Spain",
    "locationConfidence": 0.82,
    "locationGranularity": "city",
    "locationSource": "geocoder",
    "lat": 39.4699,
    "lng": -0.3763,
    "url": "https://news.example.com/wildfire",
    "country": "ES",
    "region": "Valencian Community",
    "source": "Reuters",
    "tags": ["wildfire", "evacuation", "smoke"],
    "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
    "firstSeenAt": "2024-05-24T10:15:00Z",
    "lastSeenAt": "2024-05-24T14:45:00Z",
    "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
    "importanceScore": 78,
    "status": "active",
    "sourceStats": {"reuters": 5, "ap": 2},
    "miscFlags": {"hasVideo": true, "priority": "high"},
    "verified": true,
    "verifiedAt": "2024-05-24T11:00:00Z",
    "createdBy": "system-ingest"
  }
}
```

##### Success (200) - Partial object (nullable fields present)
```json
{
  "status": "ok",
  "message": "Event details",
  "data": {
    "id": "66510a9f2c4fdd9b1a0ee124",
    "label": "Protest blocks main highway",
    "summary": null,
    "category": "civil-unrest",
    "language": "en",
    "location": null,
    "locationConfidence": null,
    "locationGranularity": null,
    "locationSource": null,
    "lat": null,
    "lng": null,
    "url": null,
    "country": null,
    "region": null,
    "source": "local",
    "tags": [],
    "centroidEmbedding": [],
    "firstSeenAt": "2024-05-25T07:00:00Z",
    "lastSeenAt": null,
    "articleIds": [],
    "importanceScore": null,
    "status": "monitoring",
    "sourceStats": {},
    "miscFlags": {},
    "verified": null,
    "verifiedAt": null,
    "createdBy": "editor:alice"
  }
}
```

##### Validation error (400) - Invalid ID format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Not found (404)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Not Found",
  "message": "Event not found with id: 66510a9f2c4fdd9b1a0ee123"
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### POST /v1/events
Create a new event (full DTO). The API returns the saved event.

Authentication: Required API key.

Request body: `EventDTO` (all fields accepted; null fields are allowed and treated as absent).

Example request body:
```json
{
  "label": "Wildfire grows near city limits",
  "summary": "Evacuations underway as crews battle blaze.",
  "category": "disaster",
  "language": "en",
  "location": "Valencia, Spain",
  "locationConfidence": 0.82,
  "locationGranularity": "city",
  "locationSource": "geocoder",
  "lat": 39.4699,
  "lng": -0.3763,
  "url": "https://news.example.com/wildfire",
  "country": "ES",
  "region": "Valencian Community",
  "source": "Reuters",
  "tags": ["wildfire", "evacuation", "smoke"],
  "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
  "firstSeenAt": "2024-05-24T10:15:00Z",
  "lastSeenAt": "2024-05-24T14:45:00Z",
  "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
  "importanceScore": 78,
  "status": "active",
  "sourceStats": {"reuters": 5, "ap": 2},
  "miscFlags": {"hasVideo": true, "priority": "high"},
  "verified": true,
  "verifiedAt": "2024-05-24T11:00:00Z",
  "createdBy": "system-ingest"
}
```

#### Responses
##### Success (200) - Full object
```json
{
  "status": "ok",
  "message": "Event created",
  "data": {
    "id": "66510a9f2c4fdd9b1a0ee123",
    "label": "Wildfire grows near city limits",
    "summary": "Evacuations underway as crews battle blaze.",
    "category": "disaster",
    "language": "en",
    "location": "Valencia, Spain",
    "locationConfidence": 0.82,
    "locationGranularity": "city",
    "locationSource": "geocoder",
    "lat": 39.4699,
    "lng": -0.3763,
    "url": "https://news.example.com/wildfire",
    "country": "ES",
    "region": "Valencian Community",
    "source": "Reuters",
    "tags": ["wildfire", "evacuation", "smoke"],
    "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
    "firstSeenAt": "2024-05-24T10:15:00Z",
    "lastSeenAt": "2024-05-24T14:45:00Z",
    "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
    "importanceScore": 78,
    "status": "active",
    "sourceStats": {"reuters": 5, "ap": 2},
    "miscFlags": {"hasVideo": true, "priority": "high"},
    "verified": true,
    "verifiedAt": "2024-05-24T11:00:00Z",
    "createdBy": "system-ingest"
  }
}
```

##### Success (200) - Partial object (nullable fields present)
```json
{
  "status": "ok",
  "message": "Event created",
  "data": {
    "id": "66510a9f2c4fdd9b1a0ee124",
    "label": "Protest blocks main highway",
    "summary": null,
    "category": "civil-unrest",
    "language": "en",
    "location": null,
    "locationConfidence": null,
    "locationGranularity": null,
    "locationSource": null,
    "lat": null,
    "lng": null,
    "url": null,
    "country": null,
    "region": null,
    "source": "local",
    "tags": [],
    "centroidEmbedding": [],
    "firstSeenAt": "2024-05-25T07:00:00Z",
    "lastSeenAt": null,
    "articleIds": [],
    "importanceScore": null,
    "status": "monitoring",
    "sourceStats": {},
    "miscFlags": {},
    "verified": null,
    "verifiedAt": null,
    "createdBy": "editor:alice"
  }
}
```

##### Validation error (400) - Invalid JSON or fields
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Invalid request body."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### PUT /v1/events/{id}
Update an event by ID. Only non-null fields in the request body are applied.

Authentication: Required API key.

Path params:
- `id` (string, required)

Request body: `EventDTO` (partial allowed)

Example request body (partial update):
```json
{
  "summary": "Evacuations expanded to additional districts.",
  "status": "active",
  "verified": true,
  "verifiedAt": "2024-05-24T12:00:00Z"
}
```

#### Responses
##### Success (200) - Full object
```json
{
  "status": "ok",
  "message": "Event updated",
  "data": {
    "id": "66510a9f2c4fdd9b1a0ee123",
    "label": "Wildfire grows near city limits",
    "summary": "Evacuations expanded to additional districts.",
    "category": "disaster",
    "language": "en",
    "location": "Valencia, Spain",
    "locationConfidence": 0.82,
    "locationGranularity": "city",
    "locationSource": "geocoder",
    "lat": 39.4699,
    "lng": -0.3763,
    "url": "https://news.example.com/wildfire",
    "country": "ES",
    "region": "Valencian Community",
    "source": "Reuters",
    "tags": ["wildfire", "evacuation", "smoke"],
    "centroidEmbedding": [0.12, -0.03, 0.44, 0.01],
    "firstSeenAt": "2024-05-24T10:15:00Z",
    "lastSeenAt": "2024-05-24T14:45:00Z",
    "articleIds": ["6650f1c2a4d8f93a2a12b7c9", "6650f1c2a4d8f93a2a12b7d0"],
    "importanceScore": 78,
    "status": "active",
    "sourceStats": {"reuters": 5, "ap": 2},
    "miscFlags": {"hasVideo": true, "priority": "high"},
    "verified": true,
    "verifiedAt": "2024-05-24T12:00:00Z",
    "createdBy": "system-ingest"
  }
}
```

##### Success (200) - Partial object (nullable fields present)
```json
{
  "status": "ok",
  "message": "Event updated",
  "data": {
    "id": "66510a9f2c4fdd9b1a0ee124",
    "label": "Protest blocks main highway",
    "summary": null,
    "category": "civil-unrest",
    "language": "en",
    "location": null,
    "locationConfidence": null,
    "locationGranularity": null,
    "locationSource": null,
    "lat": null,
    "lng": null,
    "url": null,
    "country": null,
    "region": null,
    "source": "local",
    "tags": [],
    "centroidEmbedding": [],
    "firstSeenAt": "2024-05-25T07:00:00Z",
    "lastSeenAt": null,
    "articleIds": [],
    "importanceScore": null,
    "status": "monitoring",
    "sourceStats": {},
    "miscFlags": {},
    "verified": null,
    "verifiedAt": null,
    "createdBy": "editor:alice"
  }
}
```

##### Validation error (400) - Invalid JSON or fields
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Invalid request body."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Not found (404)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Not Found",
  "message": "Event not found with id: 66510a9f2c4fdd9b1a0ee123"
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### DELETE /v1/events/{id}
Delete an event by ID.

Authentication: Required API key.

Path params:
- `id` (string, required)

#### Responses
##### Success (200) - Full delete response
```json
{
  "status": "ok",
  "message": "Event deleted",
  "data": {
    "id": "66510a9f2c4fdd9b1a0ee123",
    "title": "Wildfire grows near city limits"
  }
}
```

##### Success (200) - Partial delete response (title null)
```json
{
  "status": "ok",
  "message": "Event deleted",
  "data": {
    "id": "66510a9f2c4fdd9b1a0ee124",
    "title": null
  }
}
```

##### Validation error (400) - Invalid ID format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Not found (404)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Not Found",
  "message": "Event not found with id: 66510a9f2c4fdd9b1a0ee123"
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

## Events (Lite)
The lite endpoints return the simplified `Event` shape. Field names differ from `EventDTO` (for example, `timestamp` instead of `firstSeenAt`).

### GET /v1/events/lite
Paginated list of lite events sorted by `firstSeenAt`.

Authentication: Required API key.

Query params:
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated lite events",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee125",
        "title": "Earthquake shakes coastal region",
        "timestamp": "2024-05-23T06:40:00Z",
        "lat": 37.7749,
        "lng": -122.4194,
        "type": "quake",
        "category": "disaster",
        "url": "https://news.example.com/earthquake",
        "description": "A 5.4 magnitude quake was felt across the bay area.",
        "language": "en",
        "location": "San Francisco, CA, USA",
        "status": "active",
        "country": "US",
        "region": "California",
        "source": "AP",
        "verified": true,
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 84,
    "totalPages": 9,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated lite events",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid pagination
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/by-category
Lite events filtered by category.

Authentication: Required API key.

Query params:
- `value` (string, required; category)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by category",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee125",
        "title": "Earthquake shakes coastal region",
        "timestamp": "2024-05-23T06:40:00Z",
        "lat": 37.7749,
        "lng": -122.4194,
        "type": "quake",
        "category": "disaster",
        "url": "https://news.example.com/earthquake",
        "description": "A 5.4 magnitude quake was felt across the bay area.",
        "language": "en",
        "location": "San Francisco, CA, USA",
        "status": "active",
        "country": "US",
        "region": "California",
        "source": "AP",
        "verified": true,
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 25,
    "totalPages": 3,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by category",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid category
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/by-date
Lite events filtered by `firstSeenAt` date (UTC).

Authentication: Required API key.

Query params:
- `value` (string, required; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by date",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee125",
        "title": "Earthquake shakes coastal region",
        "timestamp": "2024-05-23T06:40:00Z",
        "lat": 37.7749,
        "lng": -122.4194,
        "type": "quake",
        "category": "disaster",
        "url": "https://news.example.com/earthquake",
        "description": "A 5.4 magnitude quake was felt across the bay area.",
        "language": "en",
        "location": "San Francisco, CA, USA",
        "status": "active",
        "country": "US",
        "region": "California",
        "source": "AP",
        "verified": true,
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 18,
    "totalPages": 2,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by date",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid date format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/by-status
Lite events filtered by status.

Authentication: Required API key.

Query params:
- `value` (string, required; status)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by status",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee125",
        "title": "Earthquake shakes coastal region",
        "timestamp": "2024-05-23T06:40:00Z",
        "lat": 37.7749,
        "lng": -122.4194,
        "type": "quake",
        "category": "disaster",
        "url": "https://news.example.com/earthquake",
        "description": "A 5.4 magnitude quake was felt across the bay area.",
        "language": "en",
        "location": "San Francisco, CA, USA",
        "status": "active",
        "country": "US",
        "region": "California",
        "source": "AP",
        "verified": true,
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 62,
    "totalPages": 7,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by status",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid status
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/by-created-by-and-verified
Lite events filtered by `created_by` and `verified` with optional date range.

Authentication: Required API key.

Query params:
- `created_by` (string, required)
- `verified` (boolean, required)
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy and verified",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee125",
        "title": "Earthquake shakes coastal region",
        "timestamp": "2024-05-23T06:40:00Z",
        "lat": 37.7749,
        "lng": -122.4194,
        "type": "quake",
        "category": "disaster",
        "url": "https://news.example.com/earthquake",
        "description": "A 5.4 magnitude quake was felt across the bay area.",
        "language": "en",
        "location": "San Francisco, CA, USA",
        "status": "active",
        "country": "US",
        "region": "California",
        "source": "AP",
        "verified": true,
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 12,
    "totalPages": 2,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy and verified",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid verified value or date
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/by-created-by-and-verified-null
Lite events filtered by `created_by` where `verified` is null, with optional date range.

Authentication: Required API key.

Query params:
- `created_by` (string, required)
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy with null verified",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee126",
        "title": "Bridge closure after inspection",
        "timestamp": "2024-05-26T08:30:00Z",
        "lat": 0.0,
        "lng": 0.0,
        "type": null,
        "category": "infrastructure",
        "url": null,
        "description": null,
        "language": "en",
        "location": null,
        "status": "monitoring",
        "country": null,
        "region": null,
        "source": "local",
        "verified": null,
        "createdBy": null
      }
    ],
    "totalItems": 3,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy with null verified",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid date or created_by
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/by-created-by
Lite events filtered by `created_by` with optional date range.

Authentication: Required API key.

Query params:
- `value` (string, required; created_by)
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee125",
        "title": "Earthquake shakes coastal region",
        "timestamp": "2024-05-23T06:40:00Z",
        "lat": 37.7749,
        "lng": -122.4194,
        "type": "quake",
        "category": "disaster",
        "url": "https://news.example.com/earthquake",
        "description": "A 5.4 magnitude quake was felt across the bay area.",
        "language": "en",
        "location": "San Francisco, CA, USA",
        "status": "active",
        "country": "US",
        "region": "California",
        "source": "AP",
        "verified": true,
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 10,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by createdBy",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid created_by
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/is-verified
Lite events filtered by verified flag with optional date range.

Authentication: Required API key.

Query params:
- `value` (boolean, required; verified)
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events by verified status",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee125",
        "title": "Earthquake shakes coastal region",
        "timestamp": "2024-05-23T06:40:00Z",
        "lat": 37.7749,
        "lng": -122.4194,
        "type": "quake",
        "category": "disaster",
        "url": "https://news.example.com/earthquake",
        "description": "A 5.4 magnitude quake was felt across the bay area.",
        "language": "en",
        "location": "San Francisco, CA, USA",
        "status": "active",
        "country": "US",
        "region": "California",
        "source": "AP",
        "verified": true,
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 45,
    "totalPages": 5,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events by verified status",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid verified value or date
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/is-verified-null
Lite events where `verified` is null, with optional date range.

Authentication: Required API key.

Query params:
- `start_date` (string, optional; format `yyyy-MM-dd`)
- `end_date` (string, optional; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

If `start_date` or `end_date` is missing or blank, the date filter is not applied.

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated events with null verified",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee126",
        "title": "Bridge closure after inspection",
        "timestamp": "2024-05-26T08:30:00Z",
        "lat": 0.0,
        "lng": 0.0,
        "type": null,
        "category": "infrastructure",
        "url": null,
        "description": null,
        "language": "en",
        "location": null,
        "status": "monitoring",
        "country": null,
        "region": null,
        "source": "local",
        "verified": null,
        "createdBy": null
      }
    ],
    "totalItems": 5,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated events with null verified",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid date
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/lite/{id}
Fetch a single lite event by ID.

Authentication: Required API key.

Path params:
- `id` (string, required)

#### Responses
##### Success (200) - Full object
```json
{
  "status": "ok",
  "message": "Lite event details",
  "data": {
    "id": "66510b3d2c4fdd9b1a0ee125",
    "title": "Earthquake shakes coastal region",
    "timestamp": "2024-05-23T06:40:00Z",
    "lat": 37.7749,
    "lng": -122.4194,
    "type": "quake",
    "category": "disaster",
    "url": "https://news.example.com/earthquake",
    "description": "A 5.4 magnitude quake was felt across the bay area.",
    "language": "en",
    "location": "San Francisco, CA, USA",
    "status": "active",
    "country": "US",
    "region": "California",
    "source": "AP",
    "verified": true,
    "createdBy": "system-ingest"
  }
}
```

##### Success (200) - Partial object (nullable fields present)
```json
{
  "status": "ok",
  "message": "Lite event details",
  "data": {
    "id": "66510b3d2c4fdd9b1a0ee126",
    "title": "Bridge closure after inspection",
    "timestamp": "2024-05-26T08:30:00Z",
    "lat": 0.0,
    "lng": 0.0,
    "type": null,
    "category": "infrastructure",
    "url": null,
    "description": null,
    "language": "en",
    "location": null,
    "status": "monitoring",
    "country": null,
    "region": null,
    "source": "local",
    "verified": null,
    "createdBy": null
  }
}
```

##### Validation error (400) - Invalid ID format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Not found (404)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Not Found",
  "message": "Lite event not found with id: 66510b3d2c4fdd9b1a0ee125"
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

## Events (Raw)
The raw endpoints currently return the same `Event` shape as lite endpoints (the implementation is an alias to the lite repository). The response message still says "Paginated lite events" or "Lite event details".

### GET /v1/events/raw
Raw events (currently the same shape as lite events).

Authentication: Required API key.

Query params:
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated lite events",
  "data": {
    "samples": [
      {
        "id": "66510b3d2c4fdd9b1a0ee125",
        "title": "Earthquake shakes coastal region",
        "timestamp": "2024-05-23T06:40:00Z",
        "lat": 37.7749,
        "lng": -122.4194,
        "type": "quake",
        "category": "disaster",
        "url": "https://news.example.com/earthquake",
        "description": "A 5.4 magnitude quake was felt across the bay area.",
        "language": "en",
        "location": "San Francisco, CA, USA",
        "status": "active",
        "country": "US",
        "region": "California",
        "source": "AP",
        "verified": true,
        "createdBy": "system-ingest"
      }
    ],
    "totalItems": 84,
    "totalPages": 9,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated lite events",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid pagination
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/events/raw/{id}
Raw event by ID (same shape as lite event).

Authentication: Required API key.

Path params:
- `id` (string, required)

#### Responses
##### Success (200) - Full object
```json
{
  "status": "ok",
  "message": "Lite event details",
  "data": {
    "id": "66510b3d2c4fdd9b1a0ee125",
    "title": "Earthquake shakes coastal region",
    "timestamp": "2024-05-23T06:40:00Z",
    "lat": 37.7749,
    "lng": -122.4194,
    "type": "quake",
    "category": "disaster",
    "url": "https://news.example.com/earthquake",
    "description": "A 5.4 magnitude quake was felt across the bay area.",
    "language": "en",
    "location": "San Francisco, CA, USA",
    "status": "active",
    "country": "US",
    "region": "California",
    "source": "AP",
    "verified": true,
    "createdBy": "system-ingest"
  }
}
```

##### Success (200) - Partial object (nullable fields present)
```json
{
  "status": "ok",
  "message": "Lite event details",
  "data": {
    "id": "66510b3d2c4fdd9b1a0ee126",
    "title": "Bridge closure after inspection",
    "timestamp": "2024-05-26T08:30:00Z",
    "lat": 0.0,
    "lng": 0.0,
    "type": null,
    "category": "infrastructure",
    "url": null,
    "description": null,
    "language": "en",
    "location": null,
    "status": "monitoring",
    "country": null,
    "region": null,
    "source": "local",
    "verified": null,
    "createdBy": null
  }
}
```

##### Validation error (400) - Invalid ID format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Not found (404)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Not Found",
  "message": "Lite event not found with id: 66510b3d2c4fdd9b1a0ee125"
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

## Pipeline Logs

### GET /v1/pipeline-logs
Paginated list of pipeline logs sorted by `ts`.

Authentication: Required API key.

Query params:
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs",
  "data": {
    "samples": [
      {
        "id": "665111aa2c4fdd9b1a0ee200",
        "ts": "2024-05-24T12:05:33Z",
        "actor": "ingest-service",
        "action": "ARTICLE_PARSED",
        "articleId": "6650f1c2a4d8f93a2a12b7c9",
        "eventId": "66510a9f2c4fdd9b1a0ee123",
        "status": "success",
        "details": {
          "parser": "boilerpipe",
          "languageDetected": "en",
          "chars": 2150,
          "topics": ["economy", "policy"]
        },
        "errorMessage": null
      }
    ],
    "totalItems": 240,
    "totalPages": 24,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid pagination
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/pipeline-logs/by-actor
Pipeline logs filtered by actor.

Authentication: Required API key.

Query params:
- `value` (string, required; actor)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by actor",
  "data": {
    "samples": [
      {
        "id": "665111aa2c4fdd9b1a0ee200",
        "ts": "2024-05-24T12:05:33Z",
        "actor": "ingest-service",
        "action": "ARTICLE_PARSED",
        "articleId": "6650f1c2a4d8f93a2a12b7c9",
        "eventId": "66510a9f2c4fdd9b1a0ee123",
        "status": "success",
        "details": {
          "parser": "boilerpipe",
          "languageDetected": "en",
          "chars": 2150,
          "topics": ["economy", "policy"]
        },
        "errorMessage": null
      }
    ],
    "totalItems": 90,
    "totalPages": 9,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by actor",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid actor
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/pipeline-logs/by-action
Pipeline logs filtered by action.

Authentication: Required API key.

Query params:
- `value` (string, required; action)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by action",
  "data": {
    "samples": [
      {
        "id": "665111aa2c4fdd9b1a0ee200",
        "ts": "2024-05-24T12:05:33Z",
        "actor": "ingest-service",
        "action": "ARTICLE_PARSED",
        "articleId": "6650f1c2a4d8f93a2a12b7c9",
        "eventId": "66510a9f2c4fdd9b1a0ee123",
        "status": "success",
        "details": {
          "parser": "boilerpipe",
          "languageDetected": "en",
          "chars": 2150,
          "topics": ["economy", "policy"]
        },
        "errorMessage": null
      }
    ],
    "totalItems": 40,
    "totalPages": 4,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by action",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid action
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/pipeline-logs/by-status
Pipeline logs filtered by status.

Authentication: Required API key.

Query params:
- `value` (string, required; status)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by status",
  "data": {
    "samples": [
      {
        "id": "665111aa2c4fdd9b1a0ee201",
        "ts": "2024-05-24T12:07:01Z",
        "actor": "enrichment-service",
        "action": "SENTIMENT",
        "articleId": "6650f1c2a4d8f93a2a12b7d0",
        "eventId": null,
        "status": "error",
        "details": null,
        "errorMessage": "Timeout contacting sentiment model"
      }
    ],
    "totalItems": 14,
    "totalPages": 2,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by status",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid status
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/pipeline-logs/by-article
Pipeline logs filtered by article ID.

Authentication: Required API key.

Query params:
- `value` (string, required; article ID)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by article",
  "data": {
    "samples": [
      {
        "id": "665111aa2c4fdd9b1a0ee200",
        "ts": "2024-05-24T12:05:33Z",
        "actor": "ingest-service",
        "action": "ARTICLE_PARSED",
        "articleId": "6650f1c2a4d8f93a2a12b7c9",
        "eventId": "66510a9f2c4fdd9b1a0ee123",
        "status": "success",
        "details": {
          "parser": "boilerpipe",
          "languageDetected": "en",
          "chars": 2150,
          "topics": ["economy", "policy"]
        },
        "errorMessage": null
      }
    ],
    "totalItems": 30,
    "totalPages": 3,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by article",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid article ID
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/pipeline-logs/by-event
Pipeline logs filtered by event ID.

Authentication: Required API key.

Query params:
- `value` (string, required; event ID)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by event",
  "data": {
    "samples": [
      {
        "id": "665111aa2c4fdd9b1a0ee200",
        "ts": "2024-05-24T12:05:33Z",
        "actor": "ingest-service",
        "action": "ARTICLE_PARSED",
        "articleId": "6650f1c2a4d8f93a2a12b7c9",
        "eventId": "66510a9f2c4fdd9b1a0ee123",
        "status": "success",
        "details": {
          "parser": "boilerpipe",
          "languageDetected": "en",
          "chars": 2150,
          "topics": ["economy", "policy"]
        },
        "errorMessage": null
      }
    ],
    "totalItems": 20,
    "totalPages": 2,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by event",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Missing or invalid event ID
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'value' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/pipeline-logs/by-date
Pipeline logs filtered by date (UTC).

Authentication: Required API key.

Query params:
- `value` (string, required; format `yyyy-MM-dd`)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`)

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by date",
  "data": {
    "samples": [
      {
        "id": "665111aa2c4fdd9b1a0ee200",
        "ts": "2024-05-24T12:05:33Z",
        "actor": "ingest-service",
        "action": "ARTICLE_PARSED",
        "articleId": "6650f1c2a4d8f93a2a12b7c9",
        "eventId": "66510a9f2c4fdd9b1a0ee123",
        "status": "success",
        "details": {
          "parser": "boilerpipe",
          "languageDetected": "en",
          "chars": 2150,
          "topics": ["economy", "policy"]
        },
        "errorMessage": null
      }
    ],
    "totalItems": 50,
    "totalPages": 5,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "Paginated pipeline logs by date",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

##### Validation error (400) - Invalid date format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/pipeline-logs/{id}
Fetch a single pipeline log by ID.

Authentication: Required API key.

Path params:
- `id` (string, required)

#### Responses
##### Success (200) - Full object
```json
{
  "status": "ok",
  "message": "Pipeline log details",
  "data": {
    "id": "665111aa2c4fdd9b1a0ee200",
    "ts": "2024-05-24T12:05:33Z",
    "actor": "ingest-service",
    "action": "ARTICLE_PARSED",
    "articleId": "6650f1c2a4d8f93a2a12b7c9",
    "eventId": "66510a9f2c4fdd9b1a0ee123",
    "status": "success",
    "details": {
      "parser": "boilerpipe",
      "languageDetected": "en",
      "chars": 2150,
      "topics": ["economy", "policy"]
    },
    "errorMessage": null
  }
}
```

##### Success (200) - Partial object (nullable fields present)
```json
{
  "status": "ok",
  "message": "Pipeline log details",
  "data": {
    "id": "665111aa2c4fdd9b1a0ee201",
    "ts": "2024-05-24T12:07:01Z",
    "actor": "enrichment-service",
    "action": "SENTIMENT",
    "articleId": "6650f1c2a4d8f93a2a12b7d0",
    "eventId": null,
    "status": "error",
    "details": null,
    "errorMessage": "Timeout contacting sentiment model"
  }
}
```

##### Validation error (400) - Invalid ID format
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Not found (404)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Not Found",
  "message": "Pipeline log not found with id: 665111aa2c4fdd9b1a0ee200"
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

## API Key Management and Request Metrics

### POST /v1/auth/generate-key
Generate an API key (admin only). This endpoint is public but requires `adminKey` in the request body.

Authentication: No API key required. Admin key required in request body.

Request body:
- `user` (string, required)
- `adminKey` (string, required)
- `creditsMicros` (integer, optional)
- `createdAt` (string, ISO-8601, optional)
- `lastChargedAt` (string, ISO-8601, optional)
- `plan` (string, optional)

Example request body:
```json
{
  "user": "acme-corp",
  "adminKey": "admin_live_123",
  "creditsMicros": 1000000,
  "createdAt": "2024-05-24T12:00:00Z",
  "lastChargedAt": "2024-05-24T12:10:15Z",
  "plan": "pro"
}
```

#### Responses
##### Success (200) - Full object (no ApiResponse envelope)
```json
{
  "id": "665112aa2c4fdd9b1a0ee300",
  "user": "acme-corp",
  "key": "key_live_abc123",
  "active": true,
  "creditsMicros": 1000000,
  "createdAt": "2024-05-24T12:00:00Z",
  "lastChargedAt": "2024-05-24T12:10:15Z",
  "plan": "pro"
}
```

##### Success (200) - Partial object (nullable fields present)
```json
{
  "id": "665112aa2c4fdd9b1a0ee301",
  "user": "beta-tester",
  "key": "key_live_beta456",
  "active": true,
  "creditsMicros": null,
  "createdAt": "2024-05-24T12:30:00Z",
  "lastChargedAt": "2024-05-24T12:30:00Z",
  "plan": null
}
```

##### Validation error (400) - Invalid request body
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Invalid request body."
}
```

##### Authorization error (401) - Invalid adminKey
```text
Unauthorized: Only admins can generate API keys
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/auth/logs
Get recent request logs for a specific API key. This endpoint is public but requires the `apiKey` query param.

Authentication: No API key required. The `apiKey` query param identifies which key to fetch logs for.

Query params:
- `apiKey` (string, required)
- `page` (int, default `0`)
- `success` (boolean, optional; accepted but currently ignored by implementation)
- `date` (string, optional; accepted but currently ignored by implementation)
- `method` (string, optional; `GET`/`POST`/`PUT`/`PATCH`/`DELETE`)

Notes:
- Page size is fixed at 10 (no `size` parameter).
- The response is actually `RequestLogAnalyticsResponse` (it includes `successCount`, `failureCount`, and `averageDurationMillis`).

#### Responses
##### Success (200) - Full page
```json
{
  "status": "ok",
  "message": "This a list of the metrics of apiKey: key_live_abc123",
  "data": {
    "samples": [
      {
        "path": "/v1/news?size=2&page=0&sort=DESC",
        "method": "GET",
        "ip": "203.0.113.9",
        "apiKey": "key_live_abc123",
        "timestamp": "2024-05-24T12:10:15",
        "success": true,
        "statusCode": 200,
        "errorMessage": null,
        "durationMillis": 132,
        "costMicros": 2500,
        "billed": true,
        "balanceAfterMicros": 975000,
        "requestQuery": "size=2&page=0&sort=DESC",
        "requestBody": null,
        "responseBody": "{\"status\":\"ok\",\"message\":\"Paginated news articles\",\"data\":{\"samples\":[{\"id\":\"6650f1c2a4d8f93a2a12b7c9\",\"title\":\"Central bank signals rate pause\",\"url\":\"https://news.example.com/markets/central-bank-pause\",\"summary\":\"Policy makers indicated a likely pause after a year of hikes.\",\"text\":\"Full article text about policy makers and market reaction.\",\"source\":\"Reuters\",\"scraped_at\":\"2024-05-24T12:01:00Z\",\"topic\":\"economy\",\"sentiment\":{\"label\":\"neutral\",\"score\":0.11},\"sample\":\"Policy makers indicated a likely pause ...\",\"locations\":[\"Washington, DC\",\"New York\"],\"organizations\":[\"Federal Reserve\",\"IMF\"],\"persons\":[\"Jane Doe\",\"John Smith\"]}],\"totalItems\":1200,\"totalPages\":600,\"currentPage\":0,\"pageSize\":2}}"
      }
    ],
    "totalItems": 62,
    "totalPages": 7,
    "currentPage": 0,
    "pageSize": 10,
    "successCount": 58,
    "failureCount": 4,
    "averageDurationMillis": 128.5
  }
}
```

##### Success (200) - Empty page
```json
{
  "status": "ok",
  "message": "This a list of the metrics of apiKey: key_live_abc123",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10,
    "successCount": 0,
    "failureCount": 0,
    "averageDurationMillis": 0.0
  }
}
```

##### Validation error (400) - Missing apiKey
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Required request parameter 'apiKey' is missing or invalid."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/auth/logs/filtered
Get request logs with filters and aggregates. This endpoint is public but requires both `apiKey` and `adminKey` query params.

Authentication: No API key required. Admin key required in query.

Query params:
- `apiKey` (string, required)
- `adminKey` (string, required)
- `page` (int, default `0`)
- `date` (string, optional; format `yyyy-MM-dd`)
- `success` (boolean, optional)
- `method` (string, optional; `GET`/`POST`/`PUT`/`PATCH`/`DELETE`)

Notes:
- Page size is fixed at 10 (no `size` parameter).

#### Responses
##### Success (200) - Full page with analytics
```json
{
  "status": "ok",
  "message": "Filtered logs for apiKey: key_live_abc123",
  "data": {
    "samples": [
      {
        "path": "/v1/events?size=1&page=0",
        "method": "GET",
        "ip": "198.51.100.17",
        "apiKey": "key_live_abc123",
        "timestamp": "2024-05-24T12:12:55",
        "success": false,
        "statusCode": 401,
        "errorMessage": "Unauthorized: Missing or invalid API key",
        "durationMillis": 5,
        "costMicros": 0,
        "billed": false,
        "balanceAfterMicros": null,
        "requestQuery": "size=1&page=0",
        "requestBody": null,
        "responseBody": "Unauthorized: Missing or invalid API key"
      }
    ],
    "totalItems": 4,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10,
    "successCount": 3,
    "failureCount": 1,
    "averageDurationMillis": 97.2
  }
}
```

##### Success (200) - Empty page with analytics
```json
{
  "status": "ok",
  "message": "Filtered logs for apiKey: key_live_abc123",
  "data": {
    "samples": [],
    "totalItems": 0,
    "totalPages": 0,
    "currentPage": 0,
    "pageSize": 10,
    "successCount": 0,
    "failureCount": 0,
    "averageDurationMillis": 0.0
  }
}
```

##### Validation error (400) - Invalid filter values
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Invalid Input",
  "message": "Invalid format or value. Please check your request and try again."
}
```

##### Authorization error (400) - Invalid adminKey
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Bad Request",
  "message": "Unauthorized: Only admins can access this endpoint"
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

## Health and Test Endpoints

### GET /v1/health
Composite health status. Public endpoint.

Authentication: Not required.

#### Responses
##### Success (200) - UP
```json
{
  "status": "UP",
  "details": {
    "mongo": {
      "status": "UP",
      "details": {
        "version": "7.0.5"
      }
    },
    "ping": {
      "status": "UP",
      "details": {}
    }
  }
}
```

##### Success (200) - Partial details (non-composite)
```json
{
  "status": "UP",
  "details": {}
}
```

##### Validation error (405) - Method not allowed
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Method Not Allowed",
  "message": "This endpoint does not support POST requests."
}
```

##### Authentication error (401) - Invalid API key provided
```text
Unauthorized: Missing or invalid API key
```

##### Server-side error (503) - DOWN
```json
{
  "status": "DOWN",
  "details": {
    "mongo": {
      "status": "DOWN",
      "details": {
        "error": "Connection refused"
      }
    },
    "ping": {
      "status": "UP",
      "details": {}
    }
  }
}
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/test/public
Public connectivity test.

Authentication: Not required.

#### Responses
##### Success (200)
```json
{
  "message": "Public endpoint reachable without API key"
}
```

##### Partial or empty response (not applicable)
This endpoint always returns a non-null `message` field when successful.

##### Validation error (405) - Method not allowed
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Method Not Allowed",
  "message": "This endpoint does not support POST requests."
}
```

##### Authentication error (401) - Invalid API key provided
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

### GET /v1/test/secured
Secured connectivity test. Returns the API key used for authentication.

Authentication: Required API key.

#### Responses
##### Success (200)
```json
{
  "message": "Secured endpoint reached with valid API key",
  "apiKeyUsed": "key_live_abc123"
}
```

##### Partial or empty response (not applicable)
This endpoint always returns both `message` and `apiKeyUsed` when successful.

##### Validation error (405) - Method not allowed
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Method Not Allowed",
  "message": "This endpoint does not support POST requests."
}
```

##### Authentication error (401)
```text
Unauthorized: Missing or invalid API key
```

##### Server error (500)
```json
{
  "timestamp": "2024-05-24T12:34:56.789",
  "error": "Internal Server Error",
  "message": "Oops! Something went wrong. Please try again later."
}
```

## Common Problems and Troubleshooting
- `401 Unauthorized`: ensure `X-API-KEY` header is present (avoid only using the query param), the key is active, and clock skew is not stripping headers via proxies.
- `400 Bad Request` on date filters: use `yyyy-MM-dd` (UTC); example `value=2024-05-24` on `/v1/events/by-date` or `/v1/pipeline-logs/by-date`.
- Empty pages: check `page` is zero-based and `size` is >0; if sorting ascending, older data may be on higher page numbers.
- `404 Not Found`: confirm IDs are copied exactly; articles, events, and pipeline logs live in separate collections.
- Slow responses on large payloads: lower `size`, filter by date/category/topic, or fetch a single ID endpoint.

## CORS
- Articles, events, and pipeline log endpoints allow all origins (`*`).
- Auth endpoints allow all origins (`*`).
- Health and test endpoints do not declare CORS explicitly; browser clients may need additional CORS configuration.

## Helpful Conventions
- Dates: `yyyy-MM-dd` strings are parsed in UTC and cover the full day (inclusive start, exclusive next-day).
- Sorting: `sort=ASC|DESC` sorts by `scrapedAt` (articles), `firstSeenAt` (events DTO and lite/raw), or `ts` (pipeline logs).
- Partial updates: `PUT /v1/events/{id}` only replaces fields provided with non-null values.
