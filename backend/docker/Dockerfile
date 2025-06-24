# build stage
FROM openjdk:17-jdk-slim as builder
ARG VERSION
WORKDIR /app
COPY build/libs/budget-app-${VERSION}.jar budget-app-${VERSION}.jar

# extract JAR layers for better caching
RUN java -Djarmode=layertools -jar budget-app-${VERSION}.jar extract

# production stage
FROM openjdk:17-jre-slim
ARG VERSION

# create non-root user
RUN groupadd -r spring && useradd -r -g spring spring

# install essential packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# copy extracted layers in optimal order
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

# change ownership to new spring user
RUN chown -R spring:spring /app
USER spring

EXPOSE 8080

# optimize jvm settings to reduce memory usage
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.jmx.enabled=false"


HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1


# Start the application
CMD java $JAVA_OPTS org.springframework.boot.loader.JarLauncher
