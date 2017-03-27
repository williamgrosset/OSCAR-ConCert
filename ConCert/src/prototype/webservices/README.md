# OSCAR Web Services - Audit REST
## Overview
The purpose of this project is to provide a web service for live auditing of an OSCAR application. The services will be accessible through a REST API. Authorized clients will be able to make a request to a specific resource on the server over an HTTP protocol. OAUTH will require clients to provide credentials (client id and secret) in exchange for an access token, which will authorize the request.

## Best Practices for REST API Design:
These are my personal notes for this [article](http://vinaysahni.com/best-practices-for-a-pragmatic-restful-api). A few key requirements for the design of an API are:
+ Use web standards where they make sense
+ Friendly to the developer and explorable by the URL address bar
+ Throw hard errors on non-SSL access to API URL
+ Strong documentation (see [GitHub](https://developer.github.com/v3/) for reference)
+ Include versioning (see [Stripe](https://stripe.com/docs/api#versioning) for reference)
+ Local values (i.e. id=5, action=refresh, page=2) can be a part of the resource URL path
+ Result filtering, sorting, and searching can be implemented as query parameters (i.e. ?order=backwards/?query=Obama)
on top of the base URL
+ Can use a fields query parameter that takes a comma seperated list to limit which fields are returned by the API request

### Use RESTful URLS and actions
+ Seperate API into logical **resources**, which are manipulated using HTTP requests 
    - where the methods ```GET, POST, ..., PUT``` have specific meaning
+ **Resources** should be nouns that make sense from the perspective of the API consumer 
    - i.e. ```GET /tickets/12```

### JSON data
+ Use camelCase for JSON field names
+ Consistent format of results
+ Endpoint URL should include .json extension

### Errors
+ An API should provide a useful error message with its own set of fields (including HTTP status codes)
+ An API that accepts JSON encoded requests should also require the Content-Type header be set to ```application/json``` or throw a 415 Unsupported Media Type HTTP status code

## OSCAR RESTful Web Services
OSCAR provides it's web services to only authorized users ([OAUTH 1.0a](https://oauth.net/core/1.0a/)).

### Data formats
+ Use JSON for all responses/requests
+ OSCAR src code has both JSON and legacy services that use XML

### Documentation
+ REST API documentation will be done using [RAML](http://raml.org)
+ Can generate base RAML using [RAML for JAX-RS](https://github.com/mulesoft-labs/raml-for-jax-rs) (?)

### Implementation
+ OSCAR uses [Apache's CXF](https://en.wikipedia.org/wiki/Apache_CXF) implementation for [JAX-RS](https://en.wikipedia.org/wiki/Java_API_for_XML_Web_Services) (Java API for creating RESTful web services)
    - pairs well with Struts framework (OSCAR runs on Struts 1.2.7)
    - allows annotations of methods to indicate their roles in the REST API
+ see ```OSCAR-ConCert/src/main/resources/applicationContextREST.xml``` for web services
+ see **RESTful Web Service** section in ```resources/architectural_notes.pdf```

### Testing
...

## OSCAR Audit Web Service
The goal of the REST API is to provide authorized access to the auditing information found in ```../../main/audit/```.

### API requests
The following JSON responses for each API call assume that the HTTP status code returns 200 (OK).
+ ```GET /audit/serverVersion``` Returns the Linux distribution version.  
  
  **Example Response**:
  ```
  {
    "version": "Ubuntu 14.04"
  }
  ```
+ ```GET /audit/databaseInfo``` Returns the connected database type and version.  
  
  **Example Response**:
  ```
  {
    "type": "MySQL",
    "version": "5.5.53"
  }
  ```
+ ```GET /audit/tomcat/jvmVersion``` Returns the JVM version.  
  
  **Example Response**:
  ```
  {
    "version": "1.7.0_111"
  }
  ```
+ ```GET /audit/tomcat/tomcatVersion``` Returns Tomcat web container version.  
  
  **Example Response**:
  ```
  {
    "version": "Apache Tomcat/7.0.52"
  }
  ```
+ ```GET /audit/tomcat/memoryAllocation``` Returns maximum (xmx) and minimum (xms) heap size for memory allocation.  
  
  **Example Response**:
  ```
  {
    "xmx": "1024m",
    "xms": "1024m"
  }
  ```
+ ```GET /audit/oscarBuild``` Returns OSCAR build tag.  
  
  **Example Response**:
  ```
  {
    "build": "Gerrit_OSCAR-697"
  }
  ```
+ ...some stuff w/ ```oscar.properties``` and ```drugref.properties```

### HTTP status codes
| HTTP Code | Message            | Meaning                                         | Response Body                         |
| --------- | ------------------ | ----------------------------------------------- | ------------------------------------- |
| 200       | OK                 | Success!                                        | JSON response                         |
| 400       | Bad Request        | You've made an error in your request.           | Error message                         |
| 403       | Forbidden          | You've exceeded rate limits or data is private. | Error message                         |
| 404       | Not Found          | The request resource could not be found.        | Error message                         |

### Java classes
+ **AuditWebService.class**: This class will handle all related web service requests. Request handlers will take in arguments that match the HTTP request parameters and return a response object.
    - This class will use **SecurityInfoManager.class** to control access to the audited information.
    - The response object (**AuditResponse.class**) will ```... extends GenericRESTReponse implements Serializable```.
+ **AuditManager.class**: This class will provide access to relevant data and business logic classes that are required by the AuditWebService route handlers. A web service class may use several manager classes to access the required data.
+ **AuditConverter.class**: This class will handle converting the objects returned by the business logic classes to a transfer object.
+ **AuditTo1.class**: This class will represent the transfer object. Transfer objects implement the Serializable interface and can be wrapped by a response object (i.e. JSON) to be sent back to the client.

An authorized client will make an API request using an available route handler. **AuditWebService** will check admin permissions using **SecurityInfoManager**. If permission is granted, **AuditManager** will handle the request and retrieve the relevent data and business logic. Once this data is received, **AuditConverter** will transform this data into a response object (**AuditResponse**) that can be returned as JSON back to the client.

## Resources
+ [OSCAR Drug REST Web Service](https://github.com/williamgrosset/OSCAR-ConCert/commit/4964b70cf4963b44cc3d2feba17d5e9b7df159a5)
+ OAUTH and REST documentation in ```OSCAR-ConCert/docs/webservices```
+ Examples and notes in ```resources/``` (credits to [Simon Diemert](https://github.com/sdiemert))
+ [Etsy API documentation](https://etsy.com/developers/documentation/getting_started/api_basics)
+ [RESTful Objects URIs and HTTP Methods](https://youtube.com/watch?v=grXnAMIQ_1Q)
