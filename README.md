# PowerMeter
This project:
* is a dropwizard project
* it reads the serial interface that is configured in the application.yml
* at this interface, a smart p1 meter is connected
* this meter produces a p1 datagram each 10 seconds
* the data is read, parsed and pushed to an mqtt broker (configurable in application.yml)
* Currently, only produced and consumed power are sent as well as the consumed gas

Later, when I grow up, I'm going to:
* make it auto-recoverable. When the service is not processing measurements anymore, an agent must restart the service. This will most probably be an external service.
* deliver operational data in order to report health (either via email or through a dedicated channel)
