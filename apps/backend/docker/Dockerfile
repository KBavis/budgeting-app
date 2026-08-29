# build stage
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle gradle.properties ./
COPY src/ src/
RUN ./gradlew bootJar --no-daemon

# production stage
FROM eclipse-temurin:17-jre-alpine

# create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# copy JAR file from builder stage
COPY --from=builder --chown=spring:spring /app/build/libs/budget-app-*.jar app.jar

USER spring

EXPOSE 8080

# optimize jvm settings to reduce memory usage
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=80.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -XX:MinHeapFreeRatio=5 \
    -XX:MaxHeapFreeRatio=10 \
    -XX:GCTimeRatio=4 \
    -XX:AdaptiveSizePolicyWeight=90 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.jmx.enabled=false"

# Start the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
