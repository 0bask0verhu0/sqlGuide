#!/bin/bash

# Set the base directory to the parent directory of `bin/`
BASE_DIR=$(dirname "$0")/..

# Search for a directory containing `pom.xml` starting from BASE_DIR
PROJECT_PATH=$(find "$BASE_DIR" -type f -name "pom.xml" -exec dirname {} \; | head -n 1)

if [ -n "$PROJECT_PATH" ]; then
    echo "Navigating to project directory: $PROJECT_PATH"
    cd "$PROJECT_PATH" || exit

mkdir -p $BASE_DIR/apache/bin/
cd $BASE_DIR/apache/bin/
wget https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz
tar -xvzf appache-maven-3.9.9-bin.tar.gz
export PATH=$BASE_DIR/apache/bin/

    # Clean and compile the project
    echo "Running mvn clean compile"
    mvn clean compile

    # Run the application
    echo "Running mvn exec:java"
    mvn exec:java -Dexec.mainClass="sqlGuide.sqlGuide"
else
    echo "Project directory with pom.xml not found in $BASE_DIR."
    exit 1
fi
