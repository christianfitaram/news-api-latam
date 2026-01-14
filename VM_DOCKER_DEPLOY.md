# Deploying the `news-api` Docker image to a VM

Step-by-step guide to pull the Artifact Registry image onto a VM and run it securely.

## 1) Prerequisites on the VM
- Debian/Ubuntu host with `sudo`.
- Docker installed (if not: `sudo apt-get update && sudo apt-get install -y docker.io`).
- `gcloud` CLI installed and initialized (`gcloud init`) or a service-account key file on the VM.
- Network access to MongoDB and the external port you plan to expose.

## 2) Authenticate Docker to Google Artifact Registry (GAR)
You need a principal with `roles/artifactregistry.reader` on the target repository.

### Option A: Using your user account (interactive)
```bash
gcloud auth login
gcloud auth configure-docker "<REGION>-docker.pkg.dev"
```

### Option B: Using a service account key (non-interactive/CI)
```bash
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account.json"
gcloud auth activate-service-account --key-file "$GOOGLE_APPLICATION_CREDENTIALS"
gcloud auth configure-docker "<REGION>-docker.pkg.dev"
```

### Option C: Short-lived access token (no gcloud config changes)
```bash
ACCESS_TOKEN="$(gcloud auth print-access-token)"
docker login -u oauth2accesstoken -p "$ACCESS_TOKEN" "https://<REGION>-docker.pkg.dev"
```

Replace `<REGION>` with the repo region (e.g., `europe-southwest1`, `us-central1`).

## 3) Pull the image from GAR
Set your coordinates, then pull:
```bash
REGION="<region>"              # e.g. europe-southwest1
PROJECT_ID="<project-id>"      # e.g. my-gcp-project
REPO="<repo-name>"             # e.g. api-store
IMAGE="news-api"
TAG="latest"
REMOTE="${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO}/${IMAGE}:${TAG}"

docker pull "${REMOTE}"
```

If you see `Unauthenticated requests do not have permission "artifactregistry.repositories.downloadArtifacts"`, the Docker login above did not succeed or the service account lacks the reader role.

## 4) Runtime configuration
Required environment variables:
- `MONGO_DB_USERNAME`
- `MONGO_DB_PASSWORD`

Common optional variables:
- `SPRING_DATA_MONGODB_URI` (override full Mongo URI if not using username/password env vars).
- `admin.api.key` (admin API key for auth endpoints).
- `JAVA_OPTS` for JVM tuning (e.g., `-Xmx512m`).

Default app port inside the container: `8080`.

## 5) Run the container
Example: expose host port `4000` to container port `8080`, pass Mongo creds, and run detached:
```bash
docker run -d \
  --name news-api \
  -p 4000:8080 \
  -e MONGO_DB_USERNAME="<mongo-user>" \
  -e MONGO_DB_PASSWORD="<mongo-pass>" \
  -e admin.api.key="<admin-key>" \
  ${JAVA_OPTS:+-e JAVA_OPTS="$JAVA_OPTS"} \
  "${REMOTE}"
```

Check status and logs:
```bash
docker ps
docker logs -f news-api
```

## 6) Firewall / networking
- Ensure the VM firewall allows inbound traffic on the host port you expose (example: 4000).
- On GCE, create a rule once per network/project:
  ```bash
  gcloud compute firewall-rules create allow-news-api \
    --allow tcp:4000 --direction=INGRESS --target-tags=news-api --description="Allow news-api" 
  # Tag your VM so the rule applies:
  gcloud compute instances add-tags <vm-name> --tags=news-api --zone <zone>
  ```
- If using another cloud/on-prem, open the equivalent inbound rule on that host/SG/NACL.

## 7) (Optional) Auto-start on reboot with systemd
Create `/etc/systemd/system/news-api.service`:
```ini
[Unit]
Description=news-api container
After=network.target docker.service
Requires=docker.service

[Service]
Restart=always
ExecStart=/usr/bin/docker start -a news-api
ExecStop=/usr/bin/docker stop -t 10 news-api

[Install]
WantedBy=multi-user.target
```
Then enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable --now news-api
```

## 8) Quick health check
```bash
curl -i http://localhost:4000/v1/health
curl -i -H "X-API-KEY: <api-key>" http://localhost:4000/v1/test/secured
```

If these succeed locally, expose the chosen host port (4000 in this example) through your load balancer or public IP as needed.
