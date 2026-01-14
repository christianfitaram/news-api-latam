# Contributing guide

Steps to modify the codebase and push updates.

## Prerequisites
- Java 21
- Maven (or use the included wrapper `./mvnw`)
- Docker (optional) for container builds
- Access to the remote Git repository

## Making changes
1. Create a feature branch:
   ```bash
   git checkout -b feature/<short-description>
   ```
2. Make code edits in `src/` as needed.
3. Run formatting/linting if applicable (none enforced here).
4. Build and run tests locally:
   ```bash
   ./mvnw clean test
   ```
   For faster feedback while iterating:
   ```bash
   ./mvnw -DskipTests compile
   ```
5. (Optional) Build the container:
   ```bash
   docker build -t news-api:local .
   ```

## Commit and push
1. Inspect changes:
   ```bash
   git status
   git diff
   ```
2. Commit with a clear message:
   ```bash
   git commit -am "Describe the change"
   ```
3. Push your branch:
   ```bash
   git push origin feature/<short-description>
   ```

## Opening a pull request
- Open a PR from your feature branch into the main branch.
- Summarize what changed, how it was tested, and any rollout notes.

## Notable paths
- API key security: `src/main/java/com/newsapi/newsAPI/security/`
- HTTP endpoints: `src/main/java/com/newsapi/newsAPI/controllers/`
- Docker packaging: `Dockerfile`, `DOCKER_DEPLOY.md`
- Auth docs: `API_KEY_AUTH.md`
