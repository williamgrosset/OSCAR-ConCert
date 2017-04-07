# Java Classes
Since I complete all my work entirely in the Linux terminal with Vim (no Eclipse IDE), I manually set my classpath for compiling my Java classes. The methods below outline the quick iterative process I use to make changes to the Java files, compile appropriately, and then restart Tomcat to see the live changes.
+ **Prerequisites**: Using master branch of OSCAR that is currently deployed with **Tomcat 7**. Steps with **Tomcat 8** can be found [here](https://github.com/williamgrosset/OSCAR-ConCert/tree/master/ConCert/src/prototype/webservices/java).

## Compiling `oscar/util/AuditAction.java`
1. Move ```oscar/util/AuditAction.java``` into ```<tomcat-path>/webapps/oscar/WEB-INF/classes/oscar/util/```
2. Ensure that you have the following packages and compile ```AuditAction.java``` with the following command from the ```WEB-INF/classes/``` directory:  
    
    ```javac -cp .:<path>/commons-io-2.4.jar:<path>/servlet-api.jar:<path>/struts-1.2.7.jar oscar/util/AuditAction.java```
3. Make any necessary changes to ```AuditAction.java``` and repeat *Step 2*
4. After successful compilation, restart **Tomcat 7**: ```sudo service tomcat7 restart```
5. Login to your OSCAR application and view the ```OSCAR Audit``` web page under the **Administration** panel
