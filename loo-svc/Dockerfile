FROM openjdk:8-alpine

ARG JAR_FILE

# 新建用户java-app
RUN set -eux; \
    mkdir -p /home/java-app/lib /home/java-app/etc /home/java-app/logs /home/java-app/tmp;

COPY docker-entrypoint.sh /home/java-app/docker-entrypoint.sh
COPY target/${JAR_FILE} /home/java-app/lib/app.jar

RUN chmod +x /home/java-app/docker-entrypoint.sh
ENTRYPOINT ["/home/java-app/docker-entrypoint.sh"]

EXPOSE 8080
