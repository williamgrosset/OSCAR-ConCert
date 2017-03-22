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
+ **Resources** should be nouns that make sense from the perspective of the API consumer and are generally plural 
    - i.e. ```GET /tickets/12```

### JSON data
+ Use camelCase for JSON field names
+ Wrap responses in envelopes only when needed
+ Consistent format of results
+ Endpoint URL should include .json extension (?)

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
...

### Testing
...

## OSCAR Audit Web Service
The goal of the REST API is to provide authorized access to the auditing information found in ```../main/audit/```.

### API requests
+ ```GET /audit/serverInfo```
+ ```GET /audit/databaseInfo```
+ ```GET /audit/tomcat/jvmVersion```
+ ```GET /audit/tomcat/tomcatVersion```
+ ```GET /audit/tomcat/memoryAllocation```
+ ```GET /audit/oscarBuild```
+ ...some stuff w/ ```oscar.properties``` and ```drugref.properties```

### HTTP status codes
| HTTP Code | Message            | Meaning                                        | Response Body                         |
| --------- | ------------------ | ---------------------------------------------- | ------------------------------------- |
| 200       | OK                 | Success!                                       | JSON response                         |
| 201       | Created            | A new resource was successfully created.       | JSON response containing new resource |
| 400       | Bad Request        | You've made an error in your request.          | Error message                         |
| 403       | Forbidden          | You've eceeded rate limits or data is private. | Error message                         |
| 404       | Not Found          | The request resource could not be found.       | Error message                         |
| 500       | Server Error       | An internal error.                             | Error message                         |
| 503       | Server Unavailable | Access to API is unavailable.                  | Error message                         |

## Resources
+ [OSCAR Drug REST Web Service](https://github.com/williamgrosset/OSCAR-ConCert/commit/4964b70cf4963b44cc3d2feba17d5e9b7df159a5)
+ OAUTH and REST documentation in ```OSCAR-ConCert/docs/webservices```
+ Examples and notes in ```resources/``` (credits to [Simon Diemert](https://github.com/sdiemert))
+ [Etsy API documentation](https://etsy.com/developers/documentation/getting_started/api_basics)
