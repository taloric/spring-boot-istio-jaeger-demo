#!/bin/sh

set -ex;

# 以java-app用户身份启动程序
exec "/usr/bin/java" \
    $JVM_OPTS \
    $JAVA_ARGS \
    -Djava.io.tmpdir="/home/java-app/tmp" \
    -jar \
    /home/java-app/lib/app.jar \
    "$@"
