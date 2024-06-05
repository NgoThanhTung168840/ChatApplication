FROM maven:3.8.0-openjdk-17

RUN mkdir -p "/app" && \
    mkdir uploads

WORKDIR /app

COPY ChatApplication ./

RUN mvn clean install

CMD ["mvn", "spring-boot:run","-Dspring-boot.run.profiles=dev"]