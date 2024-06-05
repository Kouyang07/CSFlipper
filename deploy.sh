#!/bin/bash

# Define variables for user and remote server
USER="root"
REMOTE_SERVER="134.122.120.150"
DEST_PATH="/root"
JAR_NAME="CSFlipper-1.0-SNAPSHOT.jar"
CONTAINER_NAME="CSFlipper"
IMAGE_NAME="csflipper"

# Step 1: Build the Fat JAR
echo "Building the fat JAR..."
mvn clean package

# Check if Maven build was successful
if [ $? -ne 0 ]; then
  echo "Maven build failed!"
  exit 1
fi

# Step 2: Upload the Fat JAR to the Remote Server
echo "Uploading the fat JAR to the remote server..."
scp target/$JAR_NAME $USER@$REMOTE_SERVER:$DEST_PATH

# Check if SCP was successful
if [ $? -ne 0 ]; then
  echo "Failed to upload the JAR file to the remote server!"
  exit 1
fi

# Step 3: Build and Run the Docker Container on the Remote Server
echo "Running the Docker container on the remote server..."
ssh $USER@$REMOTE_SERVER << EOF
cd $DEST_PATH
docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true
docker build -t $IMAGE_NAME .
docker run -d --name $CONTAINER_NAME $IMAGE_NAME
EOF

# Check if SSH command was successful
if [ $? -ne 0 ]; then
  echo "Failed to run Docker on the remote server!"
  exit 1
fi

echo "Deployment successful!"
