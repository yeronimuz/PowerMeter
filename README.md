# PowerMeter
This project:
* is a dropwizard project
* it reads the serial interface that is configured in the application.yml
* at this interface, a smart p1 meter is connected
* this meter produces a p1 datagram each 10 seconds
* the data is read, parsed and stored (well actually, that needs some work)

Later, when I grow up, I'm going to:
* make it send it's data to an mqtt server
* will put a small UI on it in order to get daily stats of power consumpsion and production
