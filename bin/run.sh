#!/bin/bash

# Navigate to the project directory
cd /s/parsons/g/under/mpbarbel/Projects/sqlGuide/sqlGuide

# Clean and compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="sqlGuide.sqlGuide"
