# API key authentication

Spring Security protects the API with a simple API-key gate that accepts the key either as a header or query parameter.

## Where to put the key
- Header: `X-API-KEY: <your-key>`
- Query parameter: `?apiKey=<your-key>`

## What is protected
- Everything under `/v1/**` is authenticated except the explicit public paths below.
- Public paths (no key needed): `/v1/auth/**`, `/v1/health/**`, `/actuator/**`, and `/v1/test/public`.

## How it works under the hood
- `ApiKeyAuthenticationFilter` (a `OncePerRequestFilter`) runs before the username/password filter and extracts the key from the header or query parameter.
- Keys are validated against Mongo via `ApiKeyRepository.findByKeyAndActiveTrue`.
- On success, the filter sets an authenticated `ApiKeyAuthenticationToken` with role `ROLE_API_USER`.
- On failure or if the key is missing, the request returns `401 Unauthorized` with the message `Unauthorized: Missing or invalid API key`.
- Sessions are disabled (`STATELESS`); every request must supply the key.

## Logging
- `RequestLoggingInterceptor` records the API key used (header preferred, falls back to query) along with method, path, IP, status, and duration.

## Testing the flow
Replace `<api-key>` with a valid value:
```bash
# Public endpoint (no key required)
curl http://localhost:8080/v1/test/public

# Secured endpoint using header
curl -H "X-API-KEY: <api-key>" http://localhost:8080/v1/test/secured

# Secured endpoint using query parameter
curl "http://localhost:8080/v1/test/secured?apiKey=<api-key>"
```

## Admin and key management
- Admin key is configured via `admin.api.key` (see `src/main/resources/application.properties`).
- API-key issuance and log inspection endpoints live under `/v1/auth/**` and remain publicly reachable so the admin key can be provided as a request parameter.
