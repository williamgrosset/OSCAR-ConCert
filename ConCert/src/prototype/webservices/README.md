# OSCAR Web Services - Audit REST
## Overview
The purpose of this project is to provide a web service for live auditing of an OSCAR application. The services will be accessible through a REST API. Authorized clients will be able to make a request to a specific resource on the server over an HTTP protocol. OAUTH will require clients to provide credentials (client id and secret) in exchange for an access token, which will authorize the request.

## OSCAR RESTful Web Services
OSCAR provides it's web services to only authorized users ([OAUTH 1.0a](https://oauth.net/core/1.0a/) or user session with privileges). OSCAR's source code uses both JSON and XML (legacy services).

### Implementation
+ OSCAR uses [Apache's CXF](https://en.wikipedia.org/wiki/Apache_CXF) implementation for [JAX-RS](https://en.wikipedia.org/wiki/Java_API_for_XML_Web_Services) (Java API for creating RESTful web services)
    - pairs well with Struts framework (OSCAR runs on Struts 1.2.7)
    - allows annotations of methods to indicate their roles in the REST API
+ see ```OSCAR-ConCert/src/main/resources/applicationContextREST.xml``` for all Java web services

### Using web services
OSCAR web services can be accessed in two different ways:
+ **OAuth**: Can use endpoint ```/ws/services/<path>?<query>```
+ **User session**: Can use endpoint ```/ws/rs/<path>?<query>```
+ see **RESTful Web Service** section in ```resources/architectural_notes.pdf```

## OSCAR Audit Web Service
The goal of the REST API is to provide authorized access to auditing information of the OSCAR application and its connected components (i.e. Drugref).

### API requests
The following JSON responses for each API call assume that the HTTP status code returns 200 (OK). If a fieldname returns `null`, the property could not be detected. Currently, all information below is subject to change.
+ #### ```GET /audit/systemInfo```

  Returns the system (Linux distribution) and JVM version that the OSCAR application is active on.

  **Example Response**:
  ```
  ...
  "audit": {
    "timestamp": "2017-04-21 04:10.632",
    "systemVersion": "Ubuntu 14.04",
    "jvmVersion": "1.7.0_111"
  }
  ```
+ #### ```GET /audit/databaseInfo```

  Returns the connected OSCAR database type and version.  
  
  **Example Response**:
  ```
  ...
  "audit": {
    "timestamp": "2017-04-21 04:10.632",
    "dbType": "MySQL",
    "dbVersion": "5.5.53"
  }
  ```
+ #### ```GET /audit/tomcatInfo```

  Returns the Tomcat version, and maximum/minimum (xmx/xms) heap size for Tomcat memory allocation.  
  
  **Example Response**:
  ```
  ...
  "audit": {
    "timestamp": "2017-04-21 04:10.632",
    "tomcatVersion": "Apache Tomcat/7.0.52",
    "xmx": "1024m",
    "xms": "1024m"
  }
  ```
+ #### ```GET /audit/oscarInfo```

  Returns OSCAR web application name, build tag, build date, and property values for HL7TEXT_LABS, SINGLE_PAGE_CHART, TMP_DIR, and drugref_url. 
  
  **Example Response**:
  ```
  ...
  "audit": {
    "timestamp": "2017-04-21 04:10.632",
    "webAppName": "oscar14",
    "build": "Gerrit_OSCAR-697",
    "buildDate": "2017-05-01 1:20AM",
    "hl7TextLabs": "no",
    "singlePageChart": "false",
    "tmpDir": "/etc/tmp/",
    "drugrefUrl": "http://<ip_address>:<port_number>
  }
  ```
+ #### ```GET /audit/drugrefInfo```

  Returns Drugref property values for db_user, db_url, and db_driver.
  
  **Example Response**:
  ```
  ...
  "audit": {
    "timestamp": "2017-04-21 04:10.632",
    "dbUser": "oscar",
    "dbUrl": "jdbc:mysql://127.0.0.1:drugref2",
    "dbDriver": "com.mysql.jdbc.Driver"
  }
  ```

### Java classes
+ **AuditService.class**: Handles all related web service requests. Request handlers will take in arguments that match the HTTP request parameters and return a response object.
+ **AuditManager.class**: Provide access to relevant data and business logic classes that are required by the **AuditService** route handlers. A web service class may use several manager classes to access the required data.<br><br>
  The following response classes implement the Serializable interface, and inherit properties and behaviour of the GenericRESTResponse class. These response objects act as the JSON wrapper for their respective model object:
+ **AuditSystemResponseTo1**: Wrapper object for `auditService.getAuditSystemInfo()` API request.
+ **AuditDatabaseResponseTo1**: Wrapper object for `auditService.getAuditDatabaseInfo()` API request. 
+ **AuditTomcatResponseTo1**: Wrapper object for `auditService.getAuditTomcatInfo()` API request. 
+ **AuditOscarResponseTo1**: Wrapper object for `auditService.getAuditOscarInfo()` API request. 
+ **AuditDrugrefResponseTo1**: Wrapper object for `auditService.getAuditDrugrefInfo()` API request.<br><br>
  The following model classes implement the Serializable interface and are wrapped by an **AuditResponse** object to be sent back to the client as JSON. Model classes contain the fieldnames for the JSON object:
+ **AuditSystemTo1**: Represents the model object for `auditMananger.auditSystem()` request.
+ **AuditDatabaseTo1**: Represents the model object for `auditMananger.auditDatabase()` request.
+ **AuditTomcatTo1**: Represents the model object for `auditMananger.auditTomcat()` request.
+ **AuditOscarTo1**: Represents the model object for `auditMananger.auditOscar()` request.
+ **AuditDrugrefTo1**: Represents the model object for `auditMananger.auditDrugref()` request.

An authorized client will make an API request using an available route handler. **AuditService** will check admin permissions using **SecurityInfoManager**. If permission is granted, **AuditManager** will retrieve the data for the request by directly accessing the **Audit.class**. Once this data is received, a **Audit<System|Database|Tomcat|Oscar|Drugref>To1** object will be created to represent the model object and will contain the relevant data for the request. AuditService will return a wrapper object (**Audit<System|Database|Tomcat|Oscar|Drugref>Response**) that will sent back to the client as JSON.

### Design Decisions
1. Why have multiple **AuditFooTo1.class** objects? (see `java/org/oscarehr/ws/rest/to/model/\*`)<br><br>
As the OSCAR Audit Web Service functionality extends, the auditing information that is provided to OSCAR uses will also extend. Instead of using a single model JSON object (i.e. AuditTo1.class), I decided to modularize the model objects into their own individual objects (**AuditSystemTo1.class**, **AuditDatabaseTo1**, ...) for future development and additions to the REST API.

### UML Diagrams
...

### Testing
...

## Author
Author of the OSCAR Audit REST API can be contacted at williamhgrosset@gmail.com.

## Resources
+ [OSCAR Drug REST Web Service](https://github.com/williamgrosset/OSCAR-ConCert/commit/4964b70cf4963b44cc3d2feba17d5e9b7df159a5)
+ Examples and notes in ```resources/``` (credits to [Simon Diemert](https://github.com/sdiemert))
+ OAUTH and REST documentation in ```OSCAR-ConCert/docs/webservices```
+ [Etsy API documentation](https://etsy.com/developers/documentation/getting_started/api_basics)
+ [Best Practices For a Pragmatic Restful API](http://vinaysahni.com/best-practices-for-a-pragmatic-restful-api)
