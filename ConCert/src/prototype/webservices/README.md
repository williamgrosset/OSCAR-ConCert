# OSCAR Web Services - Audit REST
## Overview
The purpose of this project is to provide a web service for live auditing of an OSCAR application. The services will be accessible through a REST API. Authorized clients will be able to make a request to a specific resource on the server over an HTTP protocol. OAUTH will require clients to provide credentials (client id and secret) in exchange for an access token, which will authorize the request.

## OSCAR RESTful Web Services
OSCAR provides it's web services to only authorized users ([OAUTH 1.0a](https://oauth.net/core/1.0a/) or user session with admin access). OSCAR's source code uses both JSON and XML (legacy services).

### Implementation
+ OSCAR uses [Apache's CXF](https://en.wikipedia.org/wiki/Apache_CXF) implementation for [JAX-RS](https://en.wikipedia.org/wiki/Java_API_for_XML_Web_Services) (Java API for creating RESTful web services)
    - pairs well with Struts framework (OSCAR runs on Struts 1.2.7)
    - allows annotations of methods to indicate their roles in the REST API
+ see ```OSCAR-ConCert/src/main/resources/applicationContextREST.xml``` for web services
+ see **RESTful Web Service** section in ```resources/architectural_notes.pdf```

### Using web services
OSCAR web services can be accessed in two different ways:
+ **OAuth**: Can use endpoint ```/ws/services/<path>?<query>```
+ **User session**: Can use endpoint ```/ws/rs/<path>?<query>```

## OSCAR Audit Web Service
The goal of the REST API is to provide authorized access to the auditing information found in ```../../main/audit/```.

### API requests
The following JSON responses for each API call assume that the HTTP status code returns 200 (OK) - all information below is subject to change.
+ #### ```GET /audit```

  Returns all auditing information of the current OSCAR instance.

  **Example Response**:
  ```
  {
    "timestamp": "2017-04-21 04:10.632",
    "serverVersion": "Ubuntu 14.04",
    "dbType": "MySQL",
    "dbVersion": "5.5.53",
    "jvmVersion": "1.7.0_111",
    "tomcatVersion": "Apache Tomcat/7.0.52",
    "xmx": "1024m",
    "xms": "1024m",
    "build": "Gerrit_OSCAR-697",
    "buildDate": "2017-05-01 1:20AM",
    "hl7TextLabs": "no",
    "singlePageChart": "false",
    "tmpDir": "/etc/tmp/",
    "drugrefUrl": "http://<ip_address>:<port_number>,
    "dbUser": "oscar",
    "dbUrl": "jdbc:mysql://127.0.0.1:drugref2",
    "dbDriver": "com.mysql.jdbc.Driver"
  }
  ```
+ #### ```GET /audit/serverInfo```

  Returns the Linux distribution version.  
  
  **Example Response**:
  ```
  {
    "timestamp": "2017-04-21 04:10.632",
    "serverVersion": "Ubuntu 14.04"
  }
  ```
+ #### ```GET /audit/databaseInfo```

  Returns the connected database type and version.  
  
  **Example Response**:
  ```
  {
    "timestamp": "2017-04-21 04:10.632",
    "dbType": "MySQL",
    "dbVersion": "5.5.53"
  }
  ```
+ #### ```GET /audit/tomcatInfo```

  Returns the JVM version, Tomcat version, and maximum/minimum (xmx/xms) heap size for Tomcat memory allocation.  
  
  **Example Response**:
  ```
  {
    "timestamp": "2017-04-21 04:10.632",
    "jvmVersion": "1.7.0_111",
    "tomcatVersion": "Apache Tomcat/7.0.52",
    "xmx": "1024m",
    "xms": "1024m"
  }
  ```
+ #### ```GET /audit/oscarInfo```

  Returns OSCAR build tag and property values for HL7TEXT_LABS, SINGLE_PAGE_CHART, TMP_DIR, and drugref_url. 
  
  **Example Response**:
  ```
  {
    "timestamp": "2017-04-21 04:10.632",
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
  {
    "timestamp": "2017-04-21 04:10.632",
    "dbUser": "oscar",
    "dbUrl": "jdbc:mysql://127.0.0.1:drugref2",
    "dbDriver": "com.mysql.jdbc.Driver"
  }
  ```

### HTTP status codes
| HTTP Code | Message            | Meaning                                         | Response Body                         |
| --------- | ------------------ | ----------------------------------------------- | ------------------------------------- |
| 200       | OK                 | Success!                                        | JSON response                         |
| 400       | Bad Request        | You've made an error in your request.           | Error message                         |
| 403       | Forbidden          | You've exceeded rate limits or data is private. | Error message                         |
| 404       | Not Found          | The request resource could not be found.        | Error message                         |

### Java classes
+ **AuditService.class**: This class will handle all related web service requests. Request handlers will take in arguments that match the HTTP request parameters and return a response object.
    - This class will use **SecurityInfoManager.class** to control access to the audited information.
    - The response object (**AuditResponse.class**) will ```... extends GenericRESTReponse implements Serializable```.
+ **AuditManager.class**: This class will provide access to relevant data and business logic classes that are required by the AuditWebService route handlers. A web service class may use several manager classes to access the required data.
+ **AuditConverter.class**: This class will handle converting the objects returned by the business logic classes to a transfer object.
+ **AuditTo1.class**: This class will represent the transfer object. Transfer objects implement the Serializable interface and can be wrapped by a response object (i.e. JSON) to be sent back to the client.
+ **TODO**: Each class should have a header comment.

An authorized client will make an API request using an available route handler. **AuditWebService** will check admin permissions using **SecurityInfoManager**. If permission is granted, **AuditManager** will handle the request and retrieve the relevent data and business logic. Once this data is received, **AuditConverter** will transform this data into a response object (**AuditResponse**) that can be returned as JSON back to the client.

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
+ [RESTful Objects URIs and HTTP Methods](https://youtube.com/watch?v=grXnAMIQ_1Q)
