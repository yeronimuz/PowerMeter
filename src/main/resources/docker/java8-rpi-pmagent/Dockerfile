FROM armhf-alpine-rpi-java8
MAINTAINER jeroenlankheet <yeronimuz@gmail.com>

WORKDIR /app

# Copy jar and application.yml to docker build directory!
# application.yml host's mqtt-broker is at 172.17.0.1. This is a temp workaround until mosquitto is in it's own container

ADD application.yml /app
ADD lnb-powermeter-0.5.2-SNAPSHOT.jar /app

CMD ["java", "-jar", "/app/lnb-powermeter-0.5.2-SNAPSHOT.jar", "/app/application.yml"]
