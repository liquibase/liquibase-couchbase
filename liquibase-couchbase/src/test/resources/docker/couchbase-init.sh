#!/usr/bin/env bash

## From example at https://www.madhur.co.in/blog/2016/07/07/create-couchbase-bucket-docker.html

echo "Setup index and memory quota..."
curl -v http://127.0.0.1:8091/pools/default -d memoryQuota=300 -d indexMemoryQuota=300

echo "Setup services..."
curl -v http://127.0.0.1:8091/node/controller/setupServices -d services=kv%2Cn1ql%2Cindex

echo "Setup credentials..."
curl -v http://127.0.0.1:8091/settings/web -d port=8091 -d username=$COUCHBASE_USERNAME -d password="$COUCHBASE_PASSWORD"

echo "Create lbcat bucket..."
curl -v -u $COUCHBASE_USERNAME:$COUCHBASE_PASSWORD -d name=lbcat -d ramQuotaMB=100 -d authType=none -d replicaNumber=2 -d proxyPort=11215 http://127.0.0.1:8091/pools/default/buckets