# Java Classes
## Compiling `oscar/util/AuditAction.java`
Since I complete all my work entirely in the Linux shell with Vim (no Eclipse IDE), I manually set my classpath for compiling my Java classes. The method below is a quick iterative process of adding changes to ```AuditAction.java```, compiling, and restarting tomcat entirely from the Linux terminal.
+ **Prerequisites**: Using master branch of OSCAR that is currently deployed with **Tomcat 8**. Steps with **Tomcat 7** can be found [here](https://github.com/williamgrosset/OSCAR-ConCert/tree/master/ConCert/src/main/audit/java).
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

    ```javac -cp .:<path>/javax.ws.rs.jar:<path>/spring-context-3.1.0.RELEASE.jar org/oscarehr/ws/rest/AuditService.java```
3. Make any necessary changes to ```AuditService.java``` and repeat *Step 2*
4. After successful compilation, restart **Tomcat 8**: 

    ```/opt/tomcat/bin/shutdown.sh && /opt/tomcat/bin/startup.sh```
5. Login to your OSCAR application and view ...*to be continued*
