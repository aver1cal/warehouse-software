#!/bin/bash

# Usage: ./build-docker.sh [tag]

##################################################################################
# Optionally, add aws ecr login command if using ECR for images, exit on failure #
##################################################################################

IMAGE_TAG=${1:-latest}

docker build -t warehouse-backend:${IMAGE_TAG} .

echo "Docker image 'warehouse-backend:${IMAGE_TAG}' built successfully."

###############
# Push to ecr #
###############