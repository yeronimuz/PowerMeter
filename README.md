# PowerMeter

This project:

* is a P1 smart meter agent
* it reads the serial interface that is configured in the application.yml
* at this interface, a smart p1 meter is connected via a FTDI converter (inverted signal) to a Raspberry Pi running the program.
* FTDI HW connection, see https://ic.tweakimg.net/camo/8aff4287e01cbd940a4a1aeb5b0f8aa98bd84928/?url=http%3A%2F%2Fbolneze.nl%2Frj11.jpg
* this meter produces a p1 datagram each 10 seconds, or every 1 second for next generation power meters
* the data is read, parsed and pushed to an mqtt broker (configurable in application.yml)
* Currently, only produced and consumed power are sent as well as the consumed gas
* Repeated values for a sensor, type combination are ignored. A value latch is used. This latch is currently reset by a timer loop. The timer period is
  configurable.
* Readings are buffered in a blocking queue. The capacity is configurable but defaults to 10000 readings. At max, 7 readings are produced per 10 seconds. 7 * 6
  * 60 = 2520 readings per hour. So little less than 4 hours of readings are stored when the mqtt broker is not reachable any longer. Note that when the mqtt
  connection is resumed, all readings are processed without delay. Note that for v2 generation of power meters the capacity is 1/10th (a datagram every second)
* Initial mqtt connection setup is done only once. When the connection is not available at startup time, the application will exit.
* The NIC adapter to bind to is configurable. The MAC address of the NIC will be used to identify the sensor in the domotics system.

Expect the following features:

* deliver operational data in order to report health (either via email or through a dedicated channel)
* Create a dynamic filter that can determine what items to send to the mqtt broker

Current issues:

* Unit test coverage is poor
* TOPIC gas is not sent as TOPIC gas but under topic POWER

## Release notes

0.5.2 Released on 2022-06-05
* Major dusting off and dependency upgrades. No functions added.

0.5.1 Released on 2019-02-10

* Fixed a bug in the P1 definition; delivered and consumed power were mixed up for T1, prod and T2
* Made the internal storage queue size configurable

0.5 Released on 2019-01-22

* First final release

# Handy lines
scp build/libs/lnb-powermeter-0.5.2-SNAPSHOT.jar jeroen@192.168.2.16:pmagent

