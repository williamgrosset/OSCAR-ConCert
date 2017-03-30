# Java Classes
## Compiling AuditAction.java
Since I do all my work entirely in the Linux shell with Vim (no Eclipse IDE), I manually set my classpath for compiling my Java classes. The method below is a quick iterative process of developing, compiling, restarting tomcat, and seeing your live changes.
+ **Prerequisites**: Using the default setup of an OSCAR installation with **Tomcat 7**.
1. Move ```oscar/util/AuditAction.java``` into ```<tomcat-path>/webapps/oscar/WEB-INF/classes/oscar/util/```.
2. Ensure that you have the following packages and compile ```AuditAction.java``` with the following command from the ```WEB-INF/classes/``` directory:  
    ```javac -cp .:/usr/share/java/commons-io-2.4.jar:/usr/share/tomcat7/lib/servlet-api.jar:<tomcat-path>/webapps/oscar/WEB-INF/lib/struts-1.2.7.jar oscar/util/AuditAction.java```
3. Restart Tomcat 7: ```sudo service tomcat7 restart```
