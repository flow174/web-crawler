#!/bin/bash

VERSION="1.0"

# build docker image
docker build -t "beck/web-crawler-application:${VERSION}" .