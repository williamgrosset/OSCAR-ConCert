<%@ page language="java" contentType="text/html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar"%>
    
<%@page import="java.util.LinkedList, org.oscarehr.hospitalReportManager.*, org.oscarehr.hospitalReportManager.model.*, java.util.List, org.oscarehr.util.SpringUtils, org.oscarehr.PMmodule.dao.ProviderDao, java.util.Date" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
HRMDocument hrmDocument = (HRMDocument) request.getAttribute("hrmDocument");
HRMReport hrmReport = (HRMReport) request.getAttribute("hrmReport");
Integer hrmReportId = (Integer) request.getAttribute("hrmReportId");

HRMDocumentToDemographic demographicLink = (HRMDocumentToDemographic) request.getAttribute("demographicLink");
List<HRMDocumentToProvider> providerLinkList = (List<HRMDocumentToProvider>) request.getAttribute("providerLinkList");

ProviderDao providerDao = (ProviderDao) SpringUtils.getBean("providerDao");
%>


<html>
<head>
<title>HRM Report</title>
<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="../js/jquery-ui-1.7.3.custom.min.js"></script>
<script language="javascript" type="text/javascript" src="../share/javascript/Oscar.js" ></script>
<script type="text/javascript" src="../share/javascript/prototype.js"></script>
<script type="text/javascript" src="../share/javascript/effects.js"></script>
<script type="text/javascript" src="../share/javascript/controls.js"></script>

<script type="text/javascript" src="../share/yui/js/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../share/yui/js/connection-min.js"></script>
<script type="text/javascript" src="../share/yui/js/animation-min.js"></script>
<script type="text/javascript" src="../share/yui/js/datasource-min.js"></script>
<script type="text/javascript" src="../share/yui/js/autocomplete-min.js"></script>
<script type="text/javascript" src="../js/demographicProviderAutocomplete.js"></script>


<link rel="stylesheet" href="../js/jquery_css/smoothness/jquery-ui-1.7.3.custom.css" type="text/css" />  
<link rel="stylesheet" type="text/css" href="../share/yui/css/fonts-min.css"/>
<link rel="stylesheet" type="text/css" href="../share/yui/css/autocomplete.css"/>
<link rel="stylesheet" type="text/css" media="all" href="../share/css/demographicProviderAutocomplete.css"  />


<style type="text/css">
#hrmReportContent {
	position: relative;
	float: left;
	padding: 25px;
	margin: 10px;
	border: 1px solid black;
	width: 550px;
}

#infoBox {
	position: relative;
	float: left;
	padding: 25px;
	margin: 10px;
	border: 1px solid black;
	width: 300px;
}

#infoBox th {
	text-align: right;
	vertical-align: top;
}

#hrmNotice {
	border-bottom: 1px solid black;
	padding-bottom: 15px;
	margin-bottom: 15px;
	font-style: italic;
}

.documentLink_statusC {
	background-color: red;
}

#metadataBox {
	clear: both;
	border: 1px solid black;
	margin: 20px;
}

#metadataBox th {
	text-align: right;
}

@media print {
	#infoBox {
		display: none;
	}
}
</style>

<script type="text/javascript">
function makeIndependent(reportId) {
	jQuery.ajax({
		type: "POST",
		url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
		data: "method=makeIndependent&reportId=" + reportId,
		success: function(data) {
			
		}
	});
}

function addDemoToHrm(reportId) {
	var demographicNo = $("demofind" + reportId + "hrm").value;
	jQuery.ajax({
		type: "POST",
		url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
		data: "method=assignDemographic&reportId=" + reportId + "&demographicNo=" + demographicNo,
		success: function(data) {
			if (data != null)
				$("demostatus" + reportId).innerHTML = data;
		}
	});
}

function removeDemoFromHrm(reportId) {
	jQuery.ajax({
		type: "POST",
		url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
		data: "method=removeDemographic&reportId=" + reportId,
		success: function(data) {
			if (data != null)
				$("demostatus" + reportId).innerHTML = data;
		}
	});
}

function addProvToHrm(reportId, providerNo) {
	jQuery.ajax({
		type: "POST",
		url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
		data: "method=assignProvider&reportId=" + reportId + "&providerNo=" + providerNo,
		success: function(data) {
			if (data != null)
				$("provstatus" + reportId).innerHTML = data;
		}
	});
}

function removeProvFromHrm(mappingId, reportId) {
	jQuery.ajax({
		type: "POST",
		url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
		data: "method=removeProvider&providerMappingId=" + mappingId,
		success: function(data) {
			if (data != null)
				$("provstatus" + reportId).innerHTML = data;
		}
	});
}

function makeActiveSubClass(reportId, subClassId) {
	jQuery.ajax({
		type: "POST",
		url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
		data: "method=makeActiveSubClass&reportId=" + reportId + "&subClassId=" + subClassId,
		success: function(data) {
			if (data != null)
				$("subclassstatus" + reportId).innerHTML = data;
		}
	});
}


function doSignOff(reportId, isSign) {
	var data;
	if (isSign)
		data = "method=signOff&signedOff=1&reportId=" + reportId;
	else
		data = "method=signOff&signedOff=0&reportId=" + reportId;
	
	$("signoff" + reportId).disabled = "disabled";
	
	jQuery.ajax({
		type: "POST",
		url: "<%=request.getContextPath() %>/hospitalReportManager/Modify.do",
		data: data,
		success: function(data) {
			$("signoffstatus" + reportId).innerHTML = data;
		}
	});
}

function signOffHrm(reportId) {
	
	doSignOff(reportId, true);	
}

function revokeSignOffHrm(reportId) {
	doSignOff(reportId, false);
}
</script>
</head>
<body>

<div id="hrmReportContent">
	<div id="hrmNotice">
	This report was received from the Hospital Report Manager (HRM) at <%=(String) request.getAttribute("hrmReportTime") %>.
	<% if (request.getAttribute("hrmDuplicateNum") != null && ((Integer) request.getAttribute("hrmDuplicateNum")) > 0) { %><br /><i>OSCAR has received <%=(Integer) request.getAttribute("hrmDuplicateNum") %> duplicates of this report.</i><% } %>
	<%
	List<HRMDocument> allDocumentsWithRelationship = (List<HRMDocument>) request.getAttribute("allDocumentsWithRelationship");
	if (allDocumentsWithRelationship != null && allDocumentsWithRelationship.size() >= 1) {
	%>
		OSCAR has detected that this is similar to the following reports: 
		<% 
		List<Integer> seenBefore = new LinkedList<Integer>();
		for (HRMDocument relationshipDocument : allDocumentsWithRelationship) { 
			if (!seenBefore.contains(relationshipDocument.getId().intValue())) { %>
			<span class="documentLink_status<%=relationshipDocument.getReportStatus() %>" title="<%=relationshipDocument.getReportDate().toString() %>">
			<% if (relationshipDocument.getId().intValue() != hrmReportId.intValue()) { %><a href="<%=request.getContextPath() %>/hospitalReportManager/Display.do?id=<%=relationshipDocument.getId() %>"><% } %>[<%=relationshipDocument.getId() %>]<% if (relationshipDocument.getId().intValue() != hrmReportId.intValue()) { %></a><% } %>
			</span>&nbsp;&nbsp;
		<% 	seenBefore.add(relationshipDocument.getId().intValue());
			}
		} %>
		 <input type="button" onClick="makeIndependent('<%=hrmReportId %>')" value="Make Independent" />
	<% } %>
	</div>

	<%=hrmReport.getFirstReportTextContent().replaceAll("\n", "<br />") %>
</div>

<div id="infoBox">
	<table>
		<tr>
			<th>Report Date</th>
			<td><%=(hrmReport.getFirstReportEventTime() != null ? hrmReport.getFirstReportEventTime().getTime().toString() : 
					((hrmReport.getFirstAccompanyingSubClassDateTime() != null ? hrmReport.getFirstAccompanyingSubClassDateTime().getTime().toString() : ""))) %></td>
		</tr>
		<tr>
			<th>Demographic Info</th>
			<td>
				<%=hrmReport.getLegalName() %><br />
				<%=hrmReport.getAddressLine1() %><br />
				<%=hrmReport.getAddressLine2() != null ? hrmReport.getAddressLine2() : "" %><br />
				<%=hrmReport.getAddressCity() %>
			</td>
		</tr>
		<tr>
			<th>Linked with Demographic</th>
			<td>
				<div id="demostatus<%=hrmReportId %>"></div>
				<% if (demographicLink != null) { %>
					<oscar:nameage demographicNo="<%=demographicLink.getDemographicNo() %>" /> <a href="#" onclick="removeDemoFromHrm('<%=hrmReportId %>')">(remove)</a>
				<% } else { %>
					<i>Not currently linked</i><br />
					<input type="hidden" id="demofind<%=hrmReportId %>hrm" value="" />
					<input type="hidden" id="routetodemo<%=hrmReportId %>hrm" value="" />
					<input type="text" id="autocompletedemo<%=hrmReportId %>hrm" onchange="checkSave('<%=hrmReportId%>hrm')" name="demographicKeyword" />
					<div id="autocomplete_choices<%=hrmReportId%>hrm" class="autocomplete"></div>
                                            
				<% } %>
			</td>
		</tr>
		<tr>
			<th>Assigned Providers</th>
			<td>
				<div id="provstatus<%=hrmReportId %>"></div>
				<% if (providerLinkList != null || providerLinkList.size() >= 1) {
					for (HRMDocumentToProvider p : providerLinkList) { 
						if (!p.getProviderNo().equalsIgnoreCase("-1")) { %>
						<%=providerDao.getProviderName(p.getProviderNo())%> <%=p.getSignedOff() !=null && p.getSignedOff()  == 1 ? "<abbr title='" + p.getSignedOffTimestamp() + "'>(Signed-Off)</abbr>" : "" %> <a href="#" onclick="removeProvFromHrm('<%=p.getId() %>', '<%=hrmReportId %>')">(remove)</a><br />
				<%		}  
					}
				} else { %>
					<i>No providers currently assigned</i><br />
				<% } %>
				<% if (hrmDocument.getUnmatchedProviders() != null && hrmDocument.getUnmatchedProviders().trim().length() >= 1) {
					String[] unmatchedProviders = hrmDocument.getUnmatchedProviders().substring(1).split("\\|");
					for (String unmatchedProvider : unmatchedProviders) { %>
						<i><abbr title="From the HRM document"><%=unmatchedProvider %></abbr></i><br />
					<% }
				} %>
				<div id="providerList<%=hrmReportId %>hrm"></div>
				<input type="hidden" name="provi" id="provfind<%=hrmReportId%>hrm" />
                <input type="text" id="autocompleteprov<%=hrmReportId%>hrm" name="demographicKeyword"/>
                <div id="autocomplete_choicesprov<%=hrmReportId%>hrm" class="autocomplete"></div>
			</td>
		</tr>
		<tr>
			<td colspan=2><hr /></td>
		</tr>
		<tr>
			<th>Report Class</th>
			<td><%=hrmReport.getFirstReportClass() %></td>
		</tr>
		<% if (hrmReport.getFirstReportClass().equalsIgnoreCase("Diagnostic Imaging Report") || hrmReport.getFirstReportClass().equalsIgnoreCase("Cardio Respiratory Report")) { %>
		<tr>
			<th>Accompanying Subclass</th>
			<td>
				<%
				List<List<Object>> subClassListFromReport = hrmReport.getAccompanyingSubclassList();
				List<HRMDocumentSubClass> subClassListFromDb = (List<HRMDocumentSubClass>) request.getAttribute("subClassList");
				
				if (subClassListFromReport.size() > 0) {
				%>
				<i>From the Report</i><br />
					<% for (List<Object> subClass : subClassListFromReport) { %>
						<abbr title="Type: <%=(String) subClass.get(0) %>; Date of Observation: <%=((Date) subClass.get(3)).toString() %>">(<%=(String) subClass.get(1) %>) <%=(String) subClass.get(2) %></abbr><br />
					<% }
				} %><br />
				<%
				if (subClassListFromDb != null && subClassListFromDb.size() > 0) { %>
				<i>Stored in Database</i><br />
					<div id="subclassstatus<%=hrmReportId %>"></div>
					<% for (HRMDocumentSubClass subClass : subClassListFromDb) { %>
						<abbr title="Type: <%=subClass.getSubClass() %>; Date of Observation: <%=subClass.getSubClassDateTime().toString() %>">(<%=subClass.getSubClassMnemonic() %>) <%=subClass.getSubClassDescription() %></abbr>
						<% if (!subClass.isActive()) { %> (<a href="#" onclick="makeActiveSubClass('<%=hrmReportId %>', '<%=subClass.getId() %>')">make active</a>)<% } %><br />
					<% }
				} %>
			</td>
		</tr>
		<% } else { %>
		<tr>
			<th>Subclass</th>
			<td>
				<%
				String[] subClassFromReport = hrmReport.getFirstReportSubClass().split("\\^");
				if (subClassFromReport.length == 2) {
				%>
				<abbr title="Subclass: <%=subClassFromReport[0] %>"><%=subClassFromReport[1] %></abbr>
				<% } %>
			</td>
		</tr>
		<% } %>
		<tr>
			<th>Categorization</th>
			<td>
				<%
				HRMCategory category = (HRMCategory) request.getAttribute("category");
				HRMSubClass subClassMapping = (HRMSubClass) request.getAttribute("subClassMapping");
				if (category != null) {
					%><abbr title="Categorized via Subclass: <%=subClassMapping.getSubClassName() %><%=(subClassMapping.getSubClassMnemonic() != null ? ", " + subClassMapping.getSubClassMnemonic() : "") %>">
					<%=category.getCategoryName() %>
					</abbr>
				<%
				} else {
				%>
				<i>No Category</i>
				<% } %>
			</td>
		</tr>
		<tr>
			<td colspan=2>
				<input type="button" value="Print" onClick="window.print()" />
				<input type="button" style="display: none;" value="Save" id="save<%=hrmReportId %>hrm" />
				<%
				if (request.getAttribute("thisProviderLink") != null) { 
					HRMDocumentToProvider hrmDocumentToProvider = (HRMDocumentToProvider) request.getAttribute("thisProviderLink");
					if (hrmDocumentToProvider != null && hrmDocumentToProvider.getSignedOff() != null && hrmDocumentToProvider.getSignedOff() != 1) {
				%>
				<input type="button" id="signoff<%=hrmReportId %>" value="Sign-Off" onClick="signOffHrm('<%=hrmReportId %>')" />
				<%  } else { %>
				<input type="button" id="signoff<%=hrmReportId %>" value="Revoke Sign-Off" onClick="revokeSignOffHrm('<%=hrmReportId %>')" />
				<%	} %>
				<div id="signoffstatus<%=hrmReportId %>"></div>
				<% } %>
			</td>
		</tr>
			
	</table>
</div>

<div id="metadataBox">
	<table>
		<tr>
			<th>Message Unique ID</th>
			<td><%=hrmReport.getMessageUniqueId() %></td>
		</tr>
		<tr>
			<th>Sending Facility ID</th>
			<td><%=hrmReport.getSendingFacilityId() %></td>
		</tr>
		<tr>
			<th>Sending Facility Report No.</th>
			<td><%=hrmReport.getSendingFacilityReportNo() %></td>
		</tr>
		<tr>
			<th>Date and Time of Report</th>
			<td><%=HRMReportParser.getAppropriateDateFromReport(hrmReport) %></td>
		</tr>
		<tr>
			<th>Result Status</th>
			<td><%=(hrmReport.getResultStatus() != null && hrmReport.getResultStatus().equalsIgnoreCase("C")) ? "Cancelled" : "Signed by the responsible author and Released by health records"  %></td>
		</tr>
	</table>
</div>

<script type="text/javascript">
YAHOO.example.BasicRemote = function() {
    if($("autocompletedemo<%=hrmReportId%>hrm") && $("autocomplete_choices<%=hrmReportId%>hrm")){
           oscarLog('in basic remote');
          var url = "../demographic/SearchDemographic.do";
          var oDS = new YAHOO.util.XHRDataSource(url,{connMethodPost:true,connXhrMode:'ignoreStaleResponses'});
          oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;// Set the responseType
          // Define the schema of the delimited resultsTEST, PATIENT(1985-06-15)
          oDS.responseSchema = {
              resultsList : "results",
              fields : ["formattedName","fomattedDob","demographicNo"]
          };
          // Enable caching
          oDS.maxCacheEntries = 0;
          // Instantiate the AutoComplete
          var oAC = new YAHOO.widget.AutoComplete("autocompletedemo<%=hrmReportId%>hrm","autocomplete_choices<%=hrmReportId%>hrm",oDS);
          oAC.queryMatchSubset = true;
          oAC.minQueryLength = 3;
          oAC.maxResultsDisplayed = 25;
          oAC.formatResult = resultFormatter2;
          oAC.queryMatchContains = true;
          oAC.itemSelectEvent.subscribe(function(type, args) {
             var str = args[0].getInputEl().id.replace("autocompletedemo","demofind");
             $(str).value = args[2][2];//li.id;
             args[0].getInputEl().value = args[2][0] + "("+args[2][1]+")";
             $("routetodemo<%=hrmReportId %>hrm").value = args[0].getInputEl().value;
          	 
             addDemoToHrm('<%=hrmReportId %>');
          });


          return {
              oDS: oDS,
              oAC: oAC
          };
      }
      }();
      
      YAHOO.example.BasicRemote = function() {
          var url = "<%= request.getContextPath() %>/provider/SearchProvider.do";
          var oDS = new YAHOO.util.XHRDataSource(url,{connMethodPost:true,connXhrMode:'ignoreStaleResponses'});
          oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;// Set the responseType
          // Define the schema of the delimited resultsTEST, PATIENT(1985-06-15)
          oDS.responseSchema = {
              resultsList : "results",
              fields : ["providerNo","firstName","lastName"]
          };
          // Enable caching
          oDS.maxCacheEntries = 0;
          // Instantiate the AutoComplete
          var oAC = new YAHOO.widget.AutoComplete("autocompleteprov<%=hrmReportId%>hrm", "autocomplete_choicesprov<%=hrmReportId%>hrm", oDS);
          oAC.queryMatchSubset = true;
          oAC.minQueryLength = 3;
          oAC.maxResultsDisplayed = 25;
          oAC.formatResult = resultFormatter3;
          oAC.queryMatchContains = true;
          oAC.itemSelectEvent.subscribe(function(type, args) {
             var myAC = args[0];
             var str = myAC.getInputEl().id.replace("autocompleteprov","provfind");
             var oData=args[2];
             $(str).value = args[2][0];//li.id;
             myAC.getInputEl().value = args[2][2] + ","+args[2][1];
             var adoc = document.createElement('div');
             adoc.appendChild(document.createTextNode(oData[2] + " " +oData[1]));
             var idoc = document.createElement('input');
             idoc.setAttribute("type", "hidden");
             idoc.setAttribute("name","flagproviders");
             idoc.setAttribute("value",oData[0]);
             adoc.appendChild(idoc);

             var providerList = $('providerList<%=hrmReportId%>hrm');
             providerList.appendChild(adoc);

             myAC.getInputEl().value = '';//;oData.fname + " " + oData.lname ;

             addProvToHrm('<%=hrmReportId %>', args[2][0]);
          });


          return {
              oDS: oDS,
              oAC: oAC
          };
      }();
</script>

</body>
</html>