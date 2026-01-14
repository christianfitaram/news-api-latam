# Events API Endpoints

Base path: `/v1/events`

## Response envelope
All endpoints respond with `ApiResponse<T>`:

```json
{
  "status": "ok",
  "message": "Paginated events",
  "data": { }
}
```

### Paginated responses
List endpoints return `PaginatedResponse<T>` in `data`:

```json
{
  "samples": [/* items */],
  "totalItems": 123,
  "totalPages": 13,
  "currentPage": 0,
  "pageSize": 10
}
```

## Common query parameters (list endpoints)
- `page` (int, default `0`)
- `size` (int, default `10`)
- `sort` (`ASC` or `DESC`, default `DESC`) — sorted by `firstSeenAt`

## Date filters
Where supported, these are optional:
- `start_date` (string, format `yyyy-MM-dd`)
- `end_date` (string, format `yyyy-MM-dd`)

If either `start_date` or `end_date` is missing, the date filter is not applied (all records for that filter are returned).

## Event shapes
### EventDTO (full)
Used by non-`/lite` endpoints.

Key fields (not exhaustive):
- `id`, `label`, `summary`, `category`, `language`
- `location`, `locationConfidence`, `locationGranularity`, `locationSource`
- `lat`, `lng`, `url`, `country`, `region`, `source`
- `tags`, `centroidEmbedding`, `articleIds`
- `firstSeenAt`, `lastSeenAt`
- `importanceScore`, `status`, `sourceStats`, `miscFlags`
- `verified`, `createdBy`

### Event (lite/raw)
Used by `/lite` and `/raw` endpoints. This is a slimmer shape with different field names.

Key fields:
- `id`, `title`, `description`, `category`, `language`
- `location`, `lat`, `lng`, `url`, `country`, `region`, `source`
- `status`, `verified`, `createdBy`
- `timestamp` (JSON name; maps to `first_seen_at` in Mongo)

## Endpoints
### List all events (DTO)
`GET /v1/events`

Query params: `page`, `size`, `sort`

---

### List all events (lite)
`GET /v1/events/lite`

Query params: `page`, `size`, `sort`

---

### List all events (raw alias)
`GET /v1/events/raw`

Query params: `page`, `size`, `sort`

---

### By category (DTO)
`GET /v1/events/by-category`

Query params:
- `value` (string, category)
- `page`, `size`, `sort`

---

### By category (lite)
`GET /v1/events/lite/by-category`

Query params:
- `value` (string, category)
- `page`, `size`, `sort`

---

### By date (DTO)
`GET /v1/events/by-date`

Query params:
- `value` (string, format `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By date (lite)
`GET /v1/events/lite/by-date`

Query params:
- `value` (string, format `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By status (DTO)
`GET /v1/events/by-status`

Query params:
- `value` (string, status)
- `page`, `size`, `sort`

---

### By status (lite)
`GET /v1/events/lite/by-status`

Query params:
- `value` (string, status)
- `page`, `size`, `sort`

---

### By created_by and verified (DTO)
`GET /v1/events/by-created-by-and-verified`

Query params:
- `created_by` (string)
- `verified` (boolean)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By created_by and verified (lite)
`GET /v1/events/lite/by-created-by-and-verified`

Query params:
- `created_by` (string)
- `verified` (boolean)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By created_by with verified null (DTO)
`GET /v1/events/by-created-by-and-verified-null`

Query params:
- `created_by` (string)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By created_by with verified null (lite)
`GET /v1/events/lite/by-created-by-and-verified-null`

Query params:
- `created_by` (string)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By created_by (DTO)
`GET /v1/events/by-created-by`

Query params:
- `value` (string, created_by)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By created_by (lite)
`GET /v1/events/lite/by-created-by`

Query params:
- `value` (string, created_by)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By verified (DTO)
`GET /v1/events/is-verified`

Query params:
- `value` (boolean, verified)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### By verified (lite)
`GET /v1/events/lite/is-verified`

Query params:
- `value` (boolean, verified)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### Verified is null/absent (DTO)
`GET /v1/events/is-verified-null`

Query params:
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### Verified is null/absent (lite)
`GET /v1/events/lite/is-verified-null`

Query params:
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)
- `page`, `size`, `sort`

---

### Average time_to_verify (seconds)
`GET /v1/events/avg-time-to-verify`

Filters use `first_seen_at` for the date range. Only includes events with `verified=true`, `verified_at` present, and `time_to_verify` present.

Query params:
- `created_by` (optional, string)
- `start_date` (optional, `yyyy-MM-dd`)
- `end_date` (optional, `yyyy-MM-dd`)

Response data:
- `averageSeconds`
- `totalItems`

---

### Get event by id (DTO)
`GET /v1/events/{id}`

---

### Get event by id (lite)
`GET /v1/events/lite/{id}`

---

### Get event by id (raw alias)
`GET /v1/events/raw/{id}`

---

### Create event (DTO)
`POST /v1/events`

Body: `EventDTO`

---

### Update event (DTO)
`PUT /v1/events/{id}`

Body: `EventDTO`

---

### Delete event
`DELETE /v1/events/{id}`

Response body:
```json
{
  "status": "ok",
  "message": "Event deleted",
  "data": {
    "id": "string",
    "title": "string"
  }
}
```

## Example requests

```bash
# List events
curl "http://localhost:8080/v1/events?page=0&size=10&sort=DESC"

# Lite events by created_by with date filter
curl "http://localhost:8080/v1/events/lite/by-created-by?value=alice&start_date=2024-01-01&end_date=2024-01-31"

# Events by created_by and verified
curl "http://localhost:8080/v1/events/by-created-by-and-verified?created_by=alice&verified=true"
```
