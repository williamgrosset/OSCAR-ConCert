# OSCAR-ConCert
### Objective
The objective of this project (nicknamed ConCert - for Continuous Certification) is to develop software that aids automatic auditing of medical information systems that are subject to certification. In particular, the project will focus on the OSCAR Electronic Medical Record (EMR) system.
### Current To-do List
* OS compatability
* Merge Java file into working with a Java servlet
### Screenshots (Testing)
#### Mac OS X:
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/osx_test.png "Mac OS X")
#### Windows:
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/windows_test.png "Windows")
### Integerating with Java Servlet Pages...
* MVC Architecture (client-server-database)
* A Servlet is a Java-based server-side web technology
* Special class in Java EE that may respond to HTTP requests
* Servlets are generally used as the Controller component
So, what exactly are we utilizing to do this? **Objects.**
1. **Request object:** created by the web server when request arrives to hold information pertaining to the request
2. **Response object:** created by the web server when request arrives to hold information pertaining to the response
However, no persistence. A new set of request and response objects are created with each new call.
