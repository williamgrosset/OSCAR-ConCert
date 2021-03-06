<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/casemgmt/taglibs.jsp"%>
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

        <h5>Database Information:</h5>
        <pre>${databaseInfo}</pre>

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
