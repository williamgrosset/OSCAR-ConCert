# OSCAR Web Services - Audit REST
## Overview
The purpose of this project is to provide a web service for live auditing of an OSCAR application. The services will be accessible through a REST API. Authorized clients will be able to make a request to a specific resource on the server over an HTTP protocol. OAUTH will require clients to provide credentials (client id and secret) in exchange for an access token, which will authorize the request.

## Notes on Best Practices
A few key requirements for the design of an API are:
+ Use web standards where they make sense
+ Friendly to the developer and explorable by the URL address bar
+ Throw hard errors on non-SSL access to API URL
+ Strong documentation (see [GitHub](https://developer.github.com/v3/) for reference)
+ Include versioning (see [Stripe](https://stripe.com/docs/api#versioning) for reference)
+ Result filtering, sorting, and searching can be implemented as query parameters on top of the base URL
+ Can use a fields query parameter that takes a comma seperated list to limit which fields are returned by the API

### Use RESTful URLS and actions
+ Seperate API into logical **resources**, which are manipulated using HTTP requests (where the methods ```GET, POST, PUT, ...``` have specific meaning)
+ **Resources** should be nouns that make sense from the perspective of the API consumer and are generally plural (i.e. ```GET /tickets/12```)

### JSON data
+ Use camelCase for JSON field names
+ Wrap responses in envelopes only when needed
+ Endpoint URL should include .json extension (?)

### Errors
+ An API should provide a useful error message with its own set of fields (including HTTP status codes)
+ An API that accepts JSON encoded requests should also require the Content-Type header be set to application/json or throw a 415 Unsupported Meida Type HTTP status code

## Resources
[REST API Concepts & Examples](https://www.youtube.com/watch?v=7YcW25PHnAA)
[Best Practices for Designing a Pragmatic RESTful API](http://www.vinaysahni.com/best-practices-for-a-pragmatic-restful-api)
