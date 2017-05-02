# Audit
### Project Description
Currently, the ConCert project audits the OSCAR deployment environment:
* Ubuntu supported version
* Java/Open JDK version
* OSCAR database type and version
* OSCAR build version
* OSCAR/Drugref file properties
* Tomcat memory allocation

### Testing
All 33 **JUnit tests** can be found in the `ConCert/src/test/audit/java` directory.

### Design Decisions
1. Why use a reverse line input stream? *(Audit.java)*<br><br> 
Since configured parameters in properties files can be overwritten sequentially, we can make an easy optimization to read the executed parameters bottom-up. We begin at the end of the file, find our desired tags and break when we have collected the information needed. Also, this makes the code more maintainable and robust without requiring extra checks if we were to read top-bottom.

2. Why use JSTL tags on JSPs? *(main/audit/jsp/admin/oscarAudit.jsp)*<br><br> 
Keeping code readable, maintainable, and easily understood is necessary for the development of an open source project. JSTL allows us to encapsulate and hide away the details of the main Java work away from the view.

### Utilizing the Struts framework
The Struts framework utilizes the Java Servlet API (Java Enterprise Edition) and formulates a model, view, controller (MVC) architecture. This framework is used for flexible and maintainable Java web-based applications. Currently, OSCAR was running on Struts version 1.2.7 during the development of the ConCert project.

The **Action class** *(AuditAction.java)* represents our model (M). This class contains our logic and processes the request by the client. Our Action class receives the appropriate data (calling our audit methods below) and then forwards the data back to the presentation layer. Overriding the execute method allows us to handle the HTTP request:
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
