#!/bin/bash

#!/bin/bash

echo "Insert commit message:"
read message
git add . && \
git commit -m "$message" && \
IMAGE_NAME=fabiopav/edi-ng_server && \
npm version patch && \
VERSION=$(cat package.json | jq -r ".version") && \
cd EDI-T && \
docker build -t $IMAGE_NAME:$VERSION -t $IMAGE_NAME:latest . && \
docker push $IMAGE_NAME:$VERSION
docker push $IMAGE_NAME:latest
