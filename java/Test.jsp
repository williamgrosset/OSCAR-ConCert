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
        <h3>${serverVersion}</h3>

        <h2>MySQL Version:</h2>
        <h3>${mysqlVersion}</h3>

        <h2>Verify Tomcat:</h2>
        <h3>${verifyTomcat}</h3>

        <h2>Verify Oscar:</h2>
        <h3>${verifyOscar}</h3>
        
        <h2>Tomcat Reinforcement:
        <h3>${tomcatReinforcement}</h3>
    </body>
</html>
