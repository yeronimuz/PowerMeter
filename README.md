# PowerMeter
This project:
* is a P1 smart meter agent
* it reads the serial interface that is configured in the application.yml
* at this interface, a smart p1 meter is connected via a FTDI converter (inverted signal) to a Raspberry Pi running the program.
* FTDI HW connection, see https://ic.tweakimg.net/camo/8aff4287e01cbd940a4a1aeb5b0f8aa98bd84928/?url=http%3A%2F%2Fbolneze.nl%2Frj11.jpg
* this meter produces a p1 datagram each 10 seconds
* the data is read, parsed and pushed to an mqtt broker (configurable in application.yml)
* Currently, only produced and consumed power are sent as well as the consumed gas

Expect the following features:
* Authentication
* make it auto-recoverable. When the service is not processing measurements anymore, an agent must restart the service. This will most probably be an external service.
* deliver operational data in order to report health (either via email or through a dedicated channel)
* Create a dynamic filter that can determine what items to send to the mqtt broker

Current bugs:
* Fix unit tests
* filter repeating values is broken. It produces an NPE
* TOPIC gas is not sent as TOPIC gas but under topic POWER
