FROM openjdk:17

WORKDIR /app

COPY build/libs/OwO_bot.jar OwO_bot.jar

RUN useradd app
USER app

ENTRYPOINT ["java", "-jar", "/app/OwO_bot.jar", "-s", "/app/secrets.properties"]