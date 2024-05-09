#!/bin/bash

./mvnw exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="UpdateSecurePassword"
./mvnw exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="VerifyPassword"
