# Container build and push (Google Artifact Registry)

This project has a Dockerfile for building a small Java 21 runtime image with Spring Boot.

## Prerequisites
- `gcloud` CLI installed and configured (`gcloud init`).
- Artifact Registry repository created (type: Docker) in your Google Cloud project.
- Docker installed locally.

## Build locally
```bash
# From repo root
docker build -t news-api:local .
```

## Tag for Artifact Registry
Replace values with your own: `<REGION>`, `<PROJECT_ID>`, `<REPO_NAME>`.
```bash
IMAGE="news-api"
TAG="latest"
REGION="europe-southwest1"              # e.g. us-central1
PROJECT_ID="liquid-fuze-475206-f7" # e.g. my-gcp-project
REPO="api-store"        # e.g. news-api-repo

REMOTE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE}:${TAG}"

# Configure Docker to use Artifact Registry
gcloud auth configure-docker "${REGION}-docker.pkg.dev"

# Tag locally built image
docker tag news-api:local "${REMOTE}"
```

## Push
```bash
docker push "${REMOTE}"
```

## (Optional) Build and push via Cloud Build
```bash
gcloud builds submit --tag "${REMOTE}" .
```

## Runtime environment
- Default port: 8080.
- Optional JVM flags: set `JAVA_OPTS` at runtime, e.g. `-Xmx512m`.
- Configure `admin.api.key` and any other Spring properties via environment variables or a mounted `application.properties`.
