# Java Melody Framework
## Overview
The [Java Melody framework](https://github.com/javamelody/javamelody/wiki) currently handles remote monitoring of:
+ Number of executions, mean execution times, and percentages of errors with HTTP requests, SQL requests, etc.
+ Memory/CPU Usage
+ Number of user sessions
+ Number of JDBC connections
+ and more...

See a demo of the Melody monitoring framework [here](http://javamelody.org/demo/monitoring).
## What's next
The idea is to extend the functionality of the monitoring framework and apply it to the Continuous Certification project. This can be done by adding a monitoring layer through [Java annotations](https://en.wikipedia.org/wiki/Java_annotation) to a Spring object or a Java class that implements the business facade pattern.

All the Spring beans in the `/src/main/resources/applicationContext.xml` file can be monitored through Melody.
