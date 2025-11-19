FROM eclipse-temurin:21
WORKDIR /app

COPY /target/itrum-transactionservice-0.0.1-SNAPSHOT.jar build/

WORKDIR /app/build
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "itrum-transactionservice-0.0.1-SNAPSHOT.jar"]