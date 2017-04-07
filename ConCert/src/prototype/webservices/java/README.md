# Java Classes
Since I complete all my work entirely in the Linux terminal with Vim (no Eclipse IDE), I manually set my classpath for compiling my Java classes. The methods below outline the quick iterative process I use to make changes to the Java files, compile appropriately, and then restart Tomcat to see the live changes.
+ **Prerequisites**: Using master branch of OSCAR that is currently deployed with **Tomcat 8**. Steps with **Tomcat 7** can be found [here](https://github.com/williamgrosset/OSCAR-ConCert/tree/master/ConCert/src/main/audit/java).

## Compiling `oscar/util/AuditAction.java`
1. Move ```oscar/util/AuditAction.java``` into ```<tomcat-path>/webapps/oscar/WEB-INF/classes/oscar/util/```
2. Ensure that you have the following packages and compile ```AuditAction.java``` with the following command from the ```WEB-INF/classes/``` directory:  
    
    ```javac -cp .:<path>/commons-io-2.4.jar:<path>/servlet-api.jar:<path>/struts-1.2.7.jar oscar/util/AuditAction.java```
3. Make any necessary changes to ```AuditAction.java``` and repeat *Step 2*
4. After successful compilation, restart **Tomcat 8**: 

    ```/opt/tomcat/bin/shutdown.sh && /opt/tomcat/bin/startup.sh```
5. Login to your OSCAR application and view the ```OSCAR Audit``` web page under the **Administration** panel

## Compiling `org/oscarehr/ws/rest/AuditService.java`
1. Move ```org/oscarehr/ws/rest/AuditService.java``` into ```<tomcat-path>/webapps/oscar/WEB-INF/classes/org/oscarehr/ws/rest/```
2. Ensure that you have the following packages and compile ```AuditService.java``` with the following command from the ```WEB-INF/classes/``` directory:  

    ```javac -cp .:<path>/javax.ws.rs.jar:<path>/spring-context-3.1.0.RELEASE.jar:<path>/log4j-1.2.17.jar org/oscarehr/ws/rest/AuditService.java```
3. Make any necessary changes to ```AuditService.java``` and repeat *Step 2*
4. After successful compilation, restart **Tomcat 8**: 

    ```/opt/tomcat/bin/shutdown.sh && /opt/tomcat/bin/startup.sh```
5. Login to your OSCAR application and view ...*to be continued*
