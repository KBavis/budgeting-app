# build stage
FROM openjdk:17-jdk-slim AS builder
ARG VERSION
WORKDIR /app
COPY build/libs/budget-app-${VERSION}.jar budget-app-${VERSION}.jar

# extract JAR layers for better caching
RUN java -Djarmode=layertools -jar budget-app-${VERSION}.jar extract

# production stage
FROM eclipse-temurin:17-jre-alpine
ARG VERSION

# create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

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

# start the application
CMD ["sh", "-c", "java $JAVA_OPTS -cp '/app/*:/app' com.bavis.budgetapp.BudgetAppApplication"]
