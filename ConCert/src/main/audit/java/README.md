# AuditAction
## Compiling
Since I do all my work entirely in the Linux shell with Vim (no Eclipse IDE), I manually set my classpath for compiling my Java classes for a quick iterative process of developing, compiling, and seeing my live changes.
+ **Prerequisites**: Using the default setup of an OSCAR installation with **Tomcat 7**.
1. Move ```oscar/util/AuditAction.java``` into ```<tomcat-path>/webapps/oscar/WEB-INF/classes/oscar/util/```.
2. Ensure that you have the following packages and compile with the following command from the ```WEB-INF/classes/``` directory:
    ```javac -cp .:/usr/share/java/commons-io-2.4.jar:/usr/share/tomcat7/lib/servlet-api.jar:<tomcat-path>/webapps/oscar/WEB-INF/lib/struts-1.2.7.jar```
