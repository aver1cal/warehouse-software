#!/bin/bash

# Usage: ./helm-deploy.sh [tag]

NAMESPACE=backend
TAG=${1:-latest}

helm upgrade --install --namespace backend warehouse-backend helm \
  --set image.tag=${TAG}