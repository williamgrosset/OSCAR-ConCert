<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="/WEB-INF/security.tld" prefix="security"%>
<html>
    <link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">
    <link href="<%=request.getContextPath() %>/css/bootstrap.css" rel="stylesheet" type="text/css">
    <title>Oscar Audit</title>
    <head>
        <div class="page-header">
            <h4>OSCAR Audit</h4>
        </div>
    </head>
    <body>
        <h5>Server Version:</h5>
        <pre>${serverVersion}</pre>

        <h5>MySQL Version:</h5>
        <pre>${mysqlVersion}</pre>

        <h5>Verify Tomcat:</h2>
        <pre>${verifyTomcat}</pre>

        <h5>Verify Oscar:</h5>
        <pre>${verifyOscar}</pre>
        
        <h5>Verify Drugref:</h5>
        <pre>${verifyDrugref}</pre>

        <h5>Tomcat Reinforcement:</h5>
        <pre>${tomcatReinforcement}</pre>
    </body>
</html>
