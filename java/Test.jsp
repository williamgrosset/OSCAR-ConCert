<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="audit.*"%>
<html>
    <title>Oscar Audit</title>
    <head>
        <h1>OSCAR AUDIT TEST</h1>
    </head>
    <body>
        <h2>Server Version:</h2>
        <pre>${serverVersion}</pre>

        <h2>MySQL Version:</h2>
        <pre>${mysqlVersion}</pre>

        <h2>Verify Tomcat:</h2>
        <pre>${verifyTomcat}</pre>

        <h2>Verify Oscar:</h2>
        <pre>${verifyOscar}</pre>
        
        <h2>Verify Drugref:</h2>
        <pre>${verifyDrugref}</pre>

        <h2>Tomcat Reinforcement:</h2>
        <pre>${tomcatReinforcement}</pre>
    </body>
</html>
