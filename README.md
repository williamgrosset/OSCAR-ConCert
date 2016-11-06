# OSCAR-ConCert
### Objective
The objective of this project (nicknamed ConCert - for Continuous Certification) is to develop software that aids automatic auditing of medical information systems that are subject to certification. In particular, the project will focus on the OSCAR Electronic Medical Record (EMR) system.

### Design Decisions
1. Why utilize a **Stack** when grabbing and sorting files? *Audit.java*    
It does not really matter what data structure we use here. I chose the **Stack** to simply help other developers have a clearer understanding of the file verification process.

2. Why are all methods **private** and **static?** *Audit.java*  
Encapsulation played a large factor in the design of this code. The **private** methods only belong to this class and cannot be accessed outside. This allows developers to not worry about changes affecting exterior classes. Also, all of these methods belong to the class, not to an instance or an object of the class. The **static** methods within this class can be considered *utility functions*, which do not modify or effect any state.

3. How are you checking for Tomcat reinforcement? *Audit.java*  
ConCert focuses on auditing of a live Tomcat application. We can track the process status of the currently running application(s) and find all possible Tomcat(s) that are live. The **"-Xmx"** value is the maximum Java heap size. The **"-Xms"** value is the initial and minimum Java heap size.

4. Why use JSTL tags with JavaServer pages? *Test.jsp*  
### Integerating with Java Servlet Pages...
#### What are Java Servlets?
Servlets are programs than run on a Web or Application server (Tomcat) and act as a middle layer between a request coming from the web server or other HTTP client and databases or applications on the HTTP server

#### So, what exactly are we utilizing to do this? 
1. **Request object:** created by the web server when request arrives to hold information pertaining to the request
2. **Response object:** created by the web server when request arrives to hold information pertaining to the response
However, no persistence. A new set of request and response objects are created with each new call.
 
### Current To-do List
* OS compatability
* Merge Java file into working with a Java servlet

### Screenshots (Testing)
#### Mac OS X:
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/osx_test.png "Mac OS X")
#### Windows:
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/windows_test.png "Windows")
#### Java Servlet:
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/jsp_test.png "JSP")
