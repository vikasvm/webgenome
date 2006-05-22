<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/webcgh.tld" prefix="webcgh" %>

<%@ page import="org.rti.webcgh.webui.util.WebUtils" %>

<%@ page errorPage="/WEB-INF/jsp/exception/jspException.jsp" %>

<script language="JavaScript">
	function resizeImage(width, height) {
		var image = document.getElementById("plotImg");
		image.setAttribute("width", width);
		image.setAttribute("height", height);
	}
</script>

<script language="JavaScript">
<!--
function newWindow(file,window) {
    msgWindow=open(file,window,'resizable=yes,width=800,height=500,status=1,scrollbars=1');
    if (msgWindow.opener == null) msgWindow.opener = self;
}
//-->
</script>

<center>
	<a href="javascript:newWindow('<html:rewrite page="/configPlotParams.do?plotType=ideogram"/>','ideogramParams')">Change ideogram-plot specific parameters</a>
	<br/>
	<br/>
</center>

<table border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td>
			<html:form action="/plot/setup">
				<webcgh:beanPropsToHiddenFields name="plotParamsForm"
					exclusions="genomeIntervals,units,plotType"/>
				<html:hidden property="plotType" value="ideogram"/>
				<html:img styleClass="pointer" 
					page="/images/helpicon.gif" align="absmiddle" 
					onclick="help('param-genomeIntervals')"/>
				Genome Interval &nbsp;&nbsp;
				<html:text property="genomeIntervals"/>
				<html:select name="plotParamsForm" property="units">
   						<webcgh:unitOptions name="plotParamsForm" property="units"/>
   				</html:select>
				&nbsp;&nbsp;<html:submit value="Go"/>
   			</html:form>
   		</td>
	</tr>
</table>

<center>
	<embed
		id="plotImg"
		src="<html:rewrite page="/plot/svg.do"/><%= WebUtils.paramListFromProps(pageContext.findAttribute("plotParamsForm")) %>"
		width="<bean:write name="plotParamsForm" property="width"/>"
		height="<bean:write name="plotParamsForm" property="height"/>"
		type="image/svg-xml"
		pluginspage="http://www.adobe.com/svg/viewer/install">
</center>

<script language="JavaScript" src="<html:rewrite page="/js/plot/svgSaveAsImage.js"/>" type="text/javascript"></script>
<%-- THIS FORM MUST EXIST TO ALLOW THE PLOT TO BE SAVED AS A GRAPHIC --%>
<html:form action="/plot/saveAs" target="plotSaveAs">
	<html:hidden property="svgDOM" value=""/>
</html:form>

<br>

<table border="0" cellpadding="0" cellspacing="0" align="center">
	<tr>
		<td>
			<html:form action="/plot/setup">
				<webcgh:beanPropsToHiddenFields name="plotParamsForm"
					exclusions="genomeIntervals,units,plotType"/>
				<html:hidden property="plotType" value="ideogram"/>
				<html:img styleClass="pointer" 
					page="/images/helpicon.gif" align="absmiddle" 
					onclick="help('param-genomeIntervals')"/>
				Genome Interval &nbsp;&nbsp;
				<html:text property="genomeIntervals"/>
				<html:select name="plotParamsForm" property="units">
   						<webcgh:unitOptions name="plotParamsForm" property="units"/>
   				</html:select>
				&nbsp;&nbsp;<html:submit value="Go"/>
   			</html:form></br>
   			<script language="JavaScript">
   			renderSaveAsLink();
   			</script>
   		</td>
	</tr>
</table>
</br>