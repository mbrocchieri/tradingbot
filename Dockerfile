FROM maven:3.8.7-openjdk-18 AS build

ENV HOME=/usr/app
ARG DATABASE_URL
ENV DATABASE_URL $DATABASE_URL
ARG DATABASE_LOGIN
ENV DATABASE_LOGIN $DATABASE_LOGIN
ARG DATABASE_PASSWORD
ENV DATABASE_PASSWORD $DATABASE_PASSWORD
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
ADD src/test/resources/application.properties.docker src/test/resources/application.properties
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests
# CMD sleep infinity

FROM build AS test
CMD mvn test

FROM openjdk:18 AS runner

COPY --from=0 /usr/app/target/tradingbot-1.0-SNAPSHOT.jar ./tradingbot.jar

EXPOSE 8080

ARG DATABASE_URL
ARG DATABASE_LOGIN
ARG DATABASE_PASSWORD
ARG STOCK_URL
ENV STOCK_URL $STOCK_URL
ENV DATABASE_URL $DATABASE_URL
ENV DATABASE_LOGIN $DATABASE_LOGIN
ENV DATABASE_PASSWORD $DATABASE_PASSWORD

# RUN chmod +x /opt/tradingbot/bin/tradingbot ; touch /opt/tradingbot/log/tradingbot.log ; ln -sf /dev/stdout /opt/tradingbot/log/tradingbot.log
RUN mkdir -p /opt/tradingbot/log ; mkdir -p /opt/tradingbot/conf ; touch /opt/tradingbot/log/tradingbot.log ; ln -sf /dev/stdout /opt/tradingbot/log/tradingbot.log
COPY src/main/resources/logback.xml /opt/tradingbot/conf/logback.xml

CMD java -Xms256m -Xmx256m -jar tradingbot.jar
# --spring.config.location=file:/opt/tradingbot/conf/application.properties
