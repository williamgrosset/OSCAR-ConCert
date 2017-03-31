# Java Classes
## Compiling AuditAction.java
Since I complete all my work entirely in the Linux shell with Vim (no Eclipse IDE), I manually set my classpath for compiling my Java classes. The method below is a quick iterative process of adding changes to ```AuditAction.java```, compiling, and restarting tomcat entirely from the Linux terminal.
+ **Prerequisites**: Using master branch of OSCAR with **Tomcat 7**. Steps with **Tomcat 8** can be found [here](https://github.com/williamgrosset/OSCAR-ConCert/tree/master/ConCert/src/prototype/webservices/java).
1. Move ```oscar/util/AuditAction.java``` into ```<tomcat-path>/webapps/oscar/WEB-INF/classes/oscar/util/```
2. Ensure that you have the following packages and compile ```AuditAction.java``` with the following command from the ```WEB-INF/classes/``` directory:  
    
    ```javac -cp .:/usr/share/java/commons-io-2.4.jar:/usr/share/tomcat7/lib/servlet-api.jar:<tomcat-path>/webapps/oscar/WEB-INF/lib/struts-1.2.7.jar oscar/util/AuditAction.java```
3. Make any necessary changes to ```AuditAction.java``` and repeat Step 2
4. Restart Tomcat 7: ```sudo service tomcat7 restart```
5. Login to your OSCAR application and view the ```OSCAR Audit``` web page under the **Administration** panel
