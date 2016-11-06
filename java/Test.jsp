<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="audit.*"%>
<html>
    <title>Oscar Audit</title>
    <head>
    <h5>THIS IS ONLY A TEST.. </h5>
    <h6>Testing a value: ${serverVersion}</h6>
    </head>
    <body>
    <b> Hello: <c:out value="${serverVersion}" default='guest'/></b>
    </body>
</html>
