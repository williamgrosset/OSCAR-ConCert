# OSCAR-ConCert
### Objective
The objective of this project (nicknamed ConCert - for Continuous Certification) is to develop software that aids automatic auditing of medical information systems that are subject to certification. In particular, the project will focus on the OSCAR Electronic Medical Record (EMR) open-source system.

### Contributor
William Grosset (Software Engineer Intern) at [LEAD Lab](http://leadlab.ca/about-us/). Supervised by [Dr. Raymond Rusk](https://github.com/rrusk) and [Dr. Jens Weber](https://github.com/jenshweber).

I completed a 100-hour contract during **Fall 2016** while taking 3 courses. I was offered a full-time position for **Spring 2016** as a co-op programmer to continue contributing to the open-source medical web application. I completed all my work remotely and managed my own weekly hours.

### ConCert
Below outlines the project structure and where all the sub-directories/files can be located within `ConCert/`:
- Committed code or in the process of code review:
    + **Audit** src in `src/main/audit` (review of feature request can be found [here](https://bitbucket.org/oscaremr/oscar/commits/d5a866c62c4e88323fefb6efecf4d1ee8a18fe1a)).
    + **Audit** tests in `src/test/audit`
- Prototypes:
    + **PropertyCheck** src in `src/prototype/propertycheck`
    + **PropertyCheck** tests in `src/test/propertycheck`
    + **Audit Web Service** w/ (...) in `src/prototype/webservices`
- Deprecated or old utility:
    + Work in `src/oldutil`
