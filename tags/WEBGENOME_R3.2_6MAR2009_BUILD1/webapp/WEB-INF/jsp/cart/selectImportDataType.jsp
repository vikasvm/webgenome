<%--L
   Copyright RTI International

   Distributed under the OSI-approved BSD 3-Clause License.
   See http://ncip.github.com/webgenome/LICENSE.txt for details.
L--%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<center>
<html:form action="/cart/import">
	
	<p>
		<html:errors property="global"/>
	</p>
	Select data type &nbsp;&nbsp;
	<html:select property="quantitationTypeId">
		<html:optionsCollection name="quantitationTypes"
			label="name" value="id"/>
	</html:select>
	
	<html:submit value="OK"/>
</html:form>
</center>