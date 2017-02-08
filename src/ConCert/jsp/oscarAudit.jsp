<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>

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
