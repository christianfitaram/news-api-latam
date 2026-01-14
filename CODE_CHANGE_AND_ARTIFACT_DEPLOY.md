# Code change and Artifact Registry deploy guide

End-to-end steps to modify the codebase and publish a Docker image to Google Artifact Registry (GAR).

## 1) Update code
1. Create a branch: `git checkout -b feature/<desc>`
2. Edit source files under `src/` as needed.
3. Build/test locally:
   ```bash
   ./mvnw clean test
   # or quick compile
   ./mvnw -DskipTests compile
   ```
4. Commit: `git commit -am "Describe the change"`

## 2) Build the Docker image
From the repo root:
```bash
docker build -t news-api:local .
```

## 3) Tag for Artifact Registry
Replace placeholders:
```bash
REGION="<region>"              # e.g. us-central1
PROJECT_ID="<project-id>"      # e.g. my-gcp-project
REPO="<repo-name>"             # e.g. news-api-repo
IMAGE="news-api"
TAG="latest"

REMOTE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE}:${TAG}"

gcloud auth configure-docker "${REGION}-docker.pkg.dev"
docker tag news-api:local "${REMOTE}"
```

## 4) Push to GAR
```bash
docker push "${REMOTE}"
```

## Optional: Cloud Build
Build and push without a local Docker daemon:
```bash
gcloud builds submit --tag "${REMOTE}" .
```

## Runtime notes
- Default app port: `8080`
- Provide config via env vars/properties (e.g., `admin.api.key`, Mongo credentials).
- Adjust JVM flags with `JAVA_OPTS` when running the container.
