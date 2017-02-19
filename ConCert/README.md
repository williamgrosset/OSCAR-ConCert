# OSCAR-ConCert
### Objective
The objective of this project (nicknamed ConCert - for Continuous Certification) is to develop software that aids automatic auditing of medical information systems that are subject to certification. In particular, the project will focus on the OSCAR Electronic Medical Record (EMR) system.

### Contributor
William Grosset (100-hour work term) at [LEAD Lab](http://leadlab.ca/about-us/). Supervised by Dr. Raymond Rusk and Dr. Jens Weber.  
The accepted feature request (via JIRA) can be found [here](https://oscaremr.atlassian.net/browse/OSCAREMR-6051/).

### ConCert
Currently, the ConCert project audits the OSCAR deployment environment:
* Ubuntu supported version
* Java/Open JDK version
* OSCAR database type and version
* OSCAR build version
* OSCAR/Drugref file properties
* Tomcat memory allocation

This project focuses on allowing OSCAR Service Providers (OSP) to easily audit the OSCAR application from a static web page. All 33 **JUnit tests** can be found in the ```src/test/``` directory of ConCert.

### Design Decisions
1. Why utilize a **Stack** when grabbing and sorting files? *(src/main/oldutil/MultiInstanceAudit.java)*<br><br>
It does not really matter what data structure we use here. I chose the **Stack** to simplify the verification process, so other developers have a clearer understanding. We could of chosen mostly any of the available data structures in the ```java.util.*``` package.

2. Why use a reverse line input stream? *(src/main/java/Audit.java)*<br><br> 
Since configured parameters in properties files can be overwritten sequentially, we can make an easy optimization to read the executed parameters bottom-up. We begin at the end of the file, find our desired tags and break when we have collected the information needed. Also, this makes the code more maintainable and robust without requiring extra checks if we were to read top-bottom.

3. How are you checking for Tomcat reinforcement? *(src/main/java/Audit.java)*<br><br> 
ConCert focuses on auditing of a live OSCAR application. We can track the process status of the currently running Tomcat. The **"-Xmx"** value is the maximum Java heap size. The **"-Xms"** value is the initial and minimum Java heap size. See ```tomcatReinforcement()``` method for further details.

4. Why is there a ```<br />``` (and sometimes ```<b>```) tag after every output line? *(src/main/java/Audit.java)*<br><br> 
The browser will interpret these characters as HTML tags, allowing for breaks between lines. Using the ```\n``` escape sequence will not render correctly.

5. Why use JSTL tags on JSPs? *(src/main/jsp/oscarAudit.jsp)*<br><br> 
Keeping code readable, maintainable, and easily understood is crucial for the development of an open source project. JSTL allows us to encapsulate and hide away the details of the main Java work away from the view. Also, this keeps the Java code seperate from mixing with HTML.

### Utilizing the Struts framework
The Struts framework utilizes the Java Servlet API (Java Enterprise Edition) and formulates a model, view, controller (MVC) architecture. This framework is used for flexible and maintainable Java web-based applications. Currently, OSCAR is running on Struts version 1.2.7.

The **Action class** *(Audit.java)* represents our model (M). This class contains our logic and processes the request by the client. Our Action class receives the appropriate data (calling our audit methods below) and then forwards the data back to the presentation layer. Overriding the execute method allows us to handle the HTTP request:
```java
public ActionForward execute(ActionMapping actionMapping, ActionForm 
                                actionForm, HttpServletRequest 
                                servletRequest, HttpServletResponse 
                                servletResponse) 
{
    servletRequest.setAttribute("serverVersion", serverVersion());
    servletRequest.setAttribute("mysqlVersion", mysqlVersion());
    servletRequest.setAttribute("verifyTomcat", verifyTomcat());
    servletRequest.setAttribute("verifyOscar", verifyOscar());
    servletRequest.setAttribute("verifyDrugref", verifyDrugref());
    servletRequest.setAttribute("tomcatReinforcement", tomcatReinforcement());
    return actionMapping.findForward("success");
}
... // audit methods below
```

The **struts-config** file *(struts-config.xml)* represents our controller (C). This file handles and designates the appropriate request by the client. The action element corresponds with the appropriate Action class. The controller forwards the request to the correct URL path that coordinates with the Action class:
```xml
<action path="/admin/oscarAudit" scope="request" parameter="method" validate="false" type="oscar.util.Audit">
    <forward name="success" path="/admin/oscarAudit.jsp" />
    <forward name="failure" path="/failure.jsp" />
    <forward name="unauthorized" path="/securityError.jsp" />
</action>
```

The **JSP** file *(oscarAudit.jsp)* represents our view (V). This file displays the request to the client. The JSTL tags allow us to access the attributes of the request specified in the Action class:
```jsp
<body>
    <h5>Server Version:</h5>
    <pre>${serverVersion}</pre>
    <h5>Database Version:</h5>
    <pre>${databaseVersion}</pre>
    <h5>Verify Tomcat:</h2>
    <pre>${verifyTomcat}</pre>
...
```
### Where do we go from here?
Auditing of hardware, network requirements, and interfaces to external providers (i.e. labs being received, OLIS setup correctly).

### Spring 2017 Update
I am now working as a full-time co-op programmer with the LEAD Lab for the Spring term.
