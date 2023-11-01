#!/bin/bash

time=$(( `date +%s` ))
biz_array=("bar" "foo" "loo")
image_repo=""

build() {
    echo "===== build and push ${biz}-svc ====="
    rm -rf ${biz}-svc/target # remove before compile
    mvn package -f "./${biz}-svc/pom.xml"
    cd ./${biz}-svc
    docker build --build-arg JAR_FILE=${biz}-svc-0.1.0.jar -t ${image_repo}/springboot-${biz}-svc:${time} .
    docker push ${image_repo}/springboot-${biz}-svc:${time}
    cd ..
    echo "${biz}-svc image tag: springboot-${biz}-svc:${time}"
}

update() {
    kubectl config use-context c7;
    kubectl set image -n sb-tracing-demo deployment/${biz}-svc ${biz}-svc=${image_repo}/springboot-${biz}-svc:${time};
}

case $1 in
    bar)
        biz="bar"
        build;
        update;
        exit 0;;
    foo)
        biz="foo"
        build;
        update;
        exit 0;;
    loo)
        biz="loo"
        build;
        update;
        exit 0;;
    all)
        for b in "${biz_array[@]}"; do
            biz=$b
            echo "$biz"
            build;
            update;
        done
        exit 0;;
    *)
        exit 1;;
esac