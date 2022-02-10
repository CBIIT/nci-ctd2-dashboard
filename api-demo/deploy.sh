#!/bin/bash -x
# test run to deploy to google cloud
docker build -t $DOCKER_USER/ctd2api-demo .
docker login
docker push $DOCKER_USER/ctd2api-demo

sudo gcloud compute instances delete ctd2api-demo-instance --project=$GCP_PROJECT --zone=us-east1-b
sudo gcloud compute firewall-rules delete rule-allow-tcp-3000 --project=$GCP_PROJECT

sudo gcloud compute firewall-rules create rule-allow-tcp-3000 --source-ranges 0.0.0.0/0 --target-tags http-server --allow tcp:3000 --project=$GCP_PROJECT
sudo gcloud compute instances create-with-container ctd2api-demo-instance --container-image=$DOCKER_USER/ctd2api-demo --container-privileged --tags=http-server,https-server --project=$GCP_PROJECT --zone=us-east1-b
