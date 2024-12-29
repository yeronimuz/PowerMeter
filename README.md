# PowerMeter

This project:

* Is a Java based P1 smart meter reader
* It reads the serial interface that is configured in the application.yml
* At this interface, a smart p1 meter is connected via a FTDI converter (inverted signal) to a Computer that runs Java
  and 'cu'. Raspberry Pi is an excellent choice.
* FTDI HW connection,
  see https://ic.tweakimg.net/camo/8aff4287e01cbd940a4a1aeb5b0f8aa98bd84928/?url=http%3A%2F%2Fbolneze.nl%2Frj11.jpg
* This meter produces a p1 datagram each x seconds.
* The data is read, parsed and pushed to an mqtt broker (configurable in power-meter.yml)
* Currently, only produced and consumed power are sent as well as the consumed gas
* Repeated values for a {sensor, type} combination are ignored. A value latch is used.
* Readings are buffered in a blocking queue. The capacity is configurable but defaults to 10000 readings. At max, 7
  readings are produced per 10 seconds. 7 * 6
    * 60 = 2520 readings per hour. So little less than 4 hours of readings are stored when the mqtt broker is not
      reachable any longer. Note that when the mqtt
      connection is resumed, all readings are processed without delay. Note that for v2 generation of power meters the
      capacity is 1/10th (a datagram every second)
* Initial mqtt connection setup is done at maximum 10 times with 1 second intervals. When the connection is not
  available after 10 retries, the application will exit.
* The MAC address of the first NIC that returns a MAC address will be used to identify the sensor in the
  domotics system.
* The serial port reader operates in it's own process. Next to the main thread there are two other threads, one for
  creating sensor value readings out of the P1 datagrams, the second thread is responsible for pushing the sensor values
  to the mqtt broker.

## Repeat values after time

We don't want to flood the database with unnecessary data. We also want to visualize combined graphs. In order to do so
we need regular overlapping time stamps. If multi series graphs don't have shared points on the x axis, then they will
be drawn after each other instead of combined.
So if the sensor value is a duplicate it will not have to be sent, unless it is required to have a value on the
repeatValuesAfter time border.
For this reason it is important to repeat the same sensor values every period.
The repeatValuesAfter configuration parameter is the elapsed time after which a duplicate sensor value is sent,
regardless of being a duplicate value.
The software checks every timestamp to see if it is close enough to the boarder of the repeatValuesAfter time.
If the timestamp of the value is within 5% margin of the repeatValuesAfter offset, then the value will be repeated.

## Registering

* The device has a WIFI connection and already knows the MQTT configuration.
* Device publishes Device information and listens for config parameters on the 'config' topic with a matching MAC
  address and sensor type.
* The sensors are saved in the database and a sensorId is returned
* The backend constructs an updated deviceDto and publishes it on the "config" topic.
* The PowerMeter waits for the config and updates the sensors (with sensorIds) with the received mqtt config.
* The PowerMeter is now able to publish sensor values with sensorIds.
* The new configuration is saved in order for the next power cycle to have the sensorIds already available.
* It is possible to configure a new sensor in the configuration yaml file and start the powermeter. It will then
  register itself because not all sensors are known. The backend will save the new sensor and return the configuration
  with the new sensorId.

# Runtime configuration updates (TODO)
* In the remote management console application, the user may configure any device, actuator and/or sensor.
* The backend will publish the new configuration with a config topic
* All devices listen to config topic MQTT messages and will filter by MAC address whether the configuration is for them.

## Wishlist:

* report health
* Reset policy for the latch. This latch is currently not reset by a timer loop. The timer period is
  to be configurable.
* read configuration parameters from mqtt. The configuration is prepared for this. But there is no subscription to any
  mqtt topic yet.
    * Make it possible to reload operational parameters (Bootstrap process required.)
    * Considerations:
        * Necessary to keep the reading process running during update in order not to lose data?
        * websocket required for reload event? or polling?
        * Need to move to OSGi?
* Less manual steps in the build process (a.o. versioning)
* Use p1 properties file
* Instead of having fixed components like serialPort, for database storage it is better to make this a generic
  component (POC)
* Read config continuously instead of only at startup

# Software upgrade considerations (by ChatGPT)

Remote upgradability is a common requirement for many software applications, especially in the context of IoT devices,
embedded systems, and distributed systems. Implementing a reliable and secure mechanism for remote upgrades is crucial.
Here are some general steps and considerations for implementing a remote upgrade mechanism in a Java program:

### Define Upgrade Protocol:

Clearly define the protocol for upgrading. This includes the format of upgrade packages, how the upgrade process will be
initiated, and any communication protocols involved (e.g., HTTP, MQTT, CoAP).

### Versioning:

Implement versioning in your software. Keep track of the current version and the version of the available upgrade. This
helps in determining whether an upgrade is needed.

### Security:

Ensure that the upgrade process is secure. Use encryption and authentication mechanisms to protect the upgrade package
during transit and to ensure that only authorized devices can perform upgrades.

### Bootloader:

Consider implementing a bootloader. A bootloader is a small program responsible for loading the main application. It can
be designed to check for and install updates. This way, even if the main application is being upgraded, the bootloader
can still function and manage the upgrade process.

### Remote Update Server:

Set up a remote update server to store and serve the upgrade packages. This server needs to be secure, and access should
be controlled to prevent unauthorized modifications.

### Rollback Mechanism:

Implement a rollback mechanism in case an upgrade fails or causes issues. This allows the system to revert to the
previous version if needed.

### Graceful Handling of Upgrades:

Ensure that your application can handle upgrades gracefully. This includes handling the transition from one version to
another without causing disruption to the normal operation of the system.

### Error Handling and Logging:

Implement robust error handling and logging mechanisms. This is important for diagnosing issues during the upgrade
process and for identifying problems in the event of a failure.

### Testing:

Thoroughly test the upgrade process in different scenarios, including scenarios where the network connection may be
unreliable. This helps identify and address potential issues before deploying the upgrade mechanism in a production
environment.

### Documentation:

Provide clear documentation for users/administrators on how to perform upgrades, what to expect during the upgrade
process, and any precautions they should take.
Remember to tailor these considerations to the specific requirements and constraints of your application and
environment. Always prioritize security and reliability when implementing a remote upgrade mechanism.

## Current issues:

* Unit test coverage is poor
* The web-api is also used for MQTT. Introduce a Dto set for mqtt.
* The sensor name is sent along with sensor values. That is possible for a sensor running on an OS, but not for a
  general device. The sensor should only be identified by the MAC address of the device.

## Release notes

0.7.0

* Device registration added

0.6.0

* Now using data types 0.8.0 and domotics API 1.1.1
* Sensors configured separately

0.5.3

* Using cu als serial port reader. Major refactoring in the serial reading process

0.5.2 Released on 2022-06-05

* Major dusting off and dependency upgrades. No functions added.

0.5.1 Released on 2019-02-10

* Fixed a bug in the P1 definition; delivered and consumed power were mixed up for T1, prod and T2
* Made the internal storage queue size configurable

0.5 Released on 2019-01-22

* First final release

# Handy lines

scp build/libs/lnb-powermeter-0.5.2-SNAPSHOT.jar jeroen@192.168.2.16:pmagent

