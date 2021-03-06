<%--L
   Copyright RTI International

   Distributed under the OSI-approved BSD 3-Clause License.
   See http://ncip.github.com/webgenome/LICENSE.txt for details.
L--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<h1 align="center">Remote System Login (<bean:write name="data.source.name"/>)</h1>

<center>
	<html:errors property="global"/>
	<html:form action="/upload/remoteSysLogin" focus="name">

	<table cellpadding="5" cellspacing="0" border="0">

	<%-- User name --%>
		<tr>
			<td valign="middle" align="left">
				<html:errors property="name"/>
				User name:
			</td>
			<td valign="middle" align="left">
				<html:text property="name"/>
			</td>
		</tr>

	<%-- Password --%>
		<tr>
			<td valign="middle" align="left">
				<html:errors property="password"/>
				Password:
			</td>
			<td valign="middle" align="left">
				<html:password property="password"/>
			</td>
		</tr>

	<%-- Submit button --%>
		<tr>
			<td colspan="2" valign="middle" align="center">
				<html:submit value="OK"/>
			</td>
		</tr>

	</table>

	</html:form>
	
	<%--
	Link deactivated to prevent malicious use.  This functionality
	will be moved to the administrator module eventually.
	<p>
		<html:link action="/user/newAccount">Create Account</html:link>
	</p>
	--%>
</center>