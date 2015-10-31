#!/bin/bash

if [ -z ${DDD_LEAVEN_AKKA_HOME} ]; then
    echo "DDD_LEAVEN_AKKA_HOME (absolute path) must be set."
    exit 1
fi

docker run --name ecommerce-test-bench -it -v ${DDD_LEAVEN_AKKA_HOME}/commons/stress-test:/mnt/stress-test newion/wrk /bin/bash