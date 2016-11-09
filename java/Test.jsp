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
        <p>${serverVersion}</p>

        <h2>MySQL Version:</h2>
        <p>${mysqlVersion}</p>

        <h2>Verify Tomcat:</h2>
        <p>${verifyTomcat}</p>

        <h2>Verify Oscar:</h2>
        <p>${verifyOscar}</p>
        
        <h2>Verify Drugref:</h2>
        <p>${verifyDrugref}</p>

        <h2>Tomcat Reinforcement:</h2>
        <p>${tomcatReinforcement}</p>
    </body>
</html>
