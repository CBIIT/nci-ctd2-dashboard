# Demo Application of CTD² API

## how to run the application

- run from cloud
   `./deploy.sh`

After the test from cloud, you could clean up by
```sh
sudo gcloud compute instances delete ctd2demo-instance --project=$GCP_PROJECT --zone=us-east1-b
sudo gcloud compute firewall-rules delete rule-allow-tcp-15500 --project=$GCP_PROJECT
```

- run with docker locally (from port 3000)

```bash
docker build -t demo-app .
docker run -p 3000:3000 -d demo-app
```
After the test, you could clean up by `docker container stop [your container name]` and `docker container rm [your container name]`.

- test run without docker
`node server.js`

Go to this URL to see the application http://hostname:3000

## backend technology
node.js

## test cases

* compounds: bortezomib;dasatinib
* genes: MYC;TP53
* incorrect query type could potentially lead to worst case search. For example, MYC + "compound query" causes slow response; TP53 + "compound query" lead to totally empty result.

## example test run with docker image

Note that *CTD2_API_HOST* and *CTD2_API_PORT* here are just examples. They should eventaully point to the production ones, namely `ctd2-dashboard.nci.nih.gov` and `80`.

`docker run -d -e CTD2_API_HOST=156.145.29.93 -e CTD2_API_PORT=9001 -p 3000:3000 $DOCKER_USER/ctd2api-demo:v0`