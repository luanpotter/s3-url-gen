#!/bin/bash -e

mvn -q exec:java -Dexec.args="$*"
