<%--

    Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
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
    Department of Computer Science
    LeadLab
    University of Victoria
    Victoria, Canada

--%>

<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/casemgmt/taglibs.jsp"%>
<html>
    <link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">
    <link href="<%=request.getContextPath() %>/css/bootstrap.css" rel="stylesheet" type="text/css">
    <link href="<%=request.getContextPath() %>/css/propertyCheck.css" rel="stylesheet" type="text/css">
    <title>Property Check</title>
    <head>
        <div class="page-header">
            <h4>ConCert Property Check</h4>
        </div>
    </head>
    <body>
        <h5>
        <h5>Search for a property:</h5>
        <p><i>NOTE: Property will either exist, not exist, or be invalid. Double check capitalization and "=" is not required in the tag that is being searched for.</i></p>
        <form action="${pageContext.request.contextPath}/admin/propertyCheck.do" method="post">
            <input type="text" name="property" placeholder="e.g. buildtag"/>
            <input type="submit" value="Submit"/>
        </form>
    </body>
</html>