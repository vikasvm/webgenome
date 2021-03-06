<%--L
   Copyright RTI International

   Distributed under the OSI-approved BSD 3-Clause License.
   See http://ncip.github.com/webgenome/LICENSE.txt for details.
L--%>

<%@page isErrorPage="true" %>
<%@ taglib uri="/WEB-INF/webgenome.tld" prefix="webgenome" %>
<%@page import="org.rti.webgenome.util.Email" %>
<%@page import="org.rti.webgenome.util.SystemUtils" %>
<p><br></p>
<p align="center">
	<font color="red">
		<b>
			WebGenome was unable to complete this request due to the
			failure of a system component.
		</b>
	</font>
</p>
<%--
  //
  //    D I S P L A Y    T H E    E R R O R    M E S S A G E
  //
  --%>
<center>
<webgenome:errorEmail exceptionMsg="<%= exception.getMessage() %>"/>
<div style="border:1px solid gray;">	
<h3>Error Log</h3>
<p>
	<%= exception.getMessage() %>
</p>
</center>
</div>