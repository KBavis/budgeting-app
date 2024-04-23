FROM openjdk:17-jdk
ARG VERSION
EXPOSE 8080
COPY build/libs/budget-app-${VERSION}.jar /app/budget-app-${VERSION}.jar
WORKDIR /app
CMD java -jar budget-app-$VERSION.jar