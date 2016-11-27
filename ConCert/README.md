# OSCAR-ConCert
### Objective
The objective of this project (nicknamed ConCert - for Continuous Certification) is to develop software that aids automatic auditing of medical information systems that are subject to certification. In particular, the project will focus on the OSCAR Electronic Medical Record (EMR) system.

### Design Decisions
1. Why utilize a **Stack** when grabbing and sorting files? *(Audit.java)*<br><br>
It does not really matter what data structure we use here. I chose the **Stack** to simply help other developers have a clearer understanding of the file verification process.

2. Why are all methods **private** and **static?** *(Audit.java)*<br><br> 
Encapsulation played a large factor in the design of this code. The **private** methods only belong to this class and cannot be accessed outside. This allows developers to not worry about changes affecting exterior classes. Also, all of these methods belong to the class, not to an instance or an object of the class. The **static** methods within this class can be considered as *utility functions*, which do not modify or effect any state.

3. How are you checking for Tomcat reinforcement? *(Audit.java)*<br><br> 
ConCert focuses on auditing of a live Oscar application. We can track the process status of the currently running application(s) and find all possible Tomcat(s) that are live. The **"-Xmx"** value is the maximum Java heap size. The **"-Xms"** value is the initial and minimum Java heap size.

4. Why use JSTL tags with JavaServer pages? *(Test.jsp)*<br><br> 
With all projects, I want to be able to keep my code readable and easily understood. JSTL allows us to encapsulate and hide away the details of the main Java work. This allows us to not mix all the source code with the HTML markup. In my experience, this was fairly similar to using the *JQuery* library over vanilla *JavaScript.*

### Utilizing the Struct framework

### What else needs to be done?

### Screenshots
#### Mac OS X (Console Test):
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/ConCert/screenshots/osx_test.png "Mac OS X")
#### Windows (Console Test):
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/ConCert/screenshots/windows_test.png "Windows")
#### Java Servlet (Audit Test Page):
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/ConCert/screenshots/jsp_test.png "JSP")
#### Administration Page
![alt-test](https://github.com/williamgrosset/OSCAR-ConCert/blob/master/ConCert/screenshots/administration.png "OSCAR Audit")
