FROM gradle:jdk17-alpine AS build-image

ENV APP_HOME=/game-of-three

WORKDIR $APP_HOME

COPY --chown=gradle:gradle settings.gradle.kts $APP_HOME/

COPY --chown=gradle:gradle build.gradle.kts $APP_HOME/

COPY --chown=gradle:gradle src/main/java $APP_HOME/src/main/java

COPY --chown=gradle:gradle src/test $APP_HOME/src/test

COPY --chown=gradle:gradle src/main/resources $APP_HOME/src/main/resources

# Build the project to cache dependencies
RUN gradle build --no-daemon


FROM openjdk:17-jdk-alpine

ENV APP_HOME=/game-of-three
COPY --from=build-image $APP_HOME/build/libs/game-of-three-0.0.1-SNAPSHOT.jar app.jar
ARG PORT_APP=8080
ENV PORT $PORT_APP
EXPOSE $PORT
ENTRYPOINT java -jar app.jar
