# build stage
FROM eclipse-temurin:17-jdk-alpine AS builder
ARG VERSION
WORKDIR /app
COPY build/libs/budget-app-${VERSION}.jar budget-app-${VERSION}.jar

# production stage
FROM eclipse-temurin:17-jre-alpine
ARG VERSION

# create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# copy JAR file directly
COPY --from=builder --chown=spring:spring /app/budget-app-${VERSION}.jar app.jar

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

# Start the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
