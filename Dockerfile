FROM gradle:jdk17-alpine as build
WORKDIR /
COPY . .
RUN gradle build

FROM eclipse-temurin:17-jre-alpine as final
COPY . /app/
WORKDIR /app
COPY --from=build ./build ./build

CMD ["bash", "./bash/launch.sh"]