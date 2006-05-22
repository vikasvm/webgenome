/*

$Source: /share/content/gforge/webcgh/webgenome/src/org/rti/webcgh/webui/cart/UploadExperimentDispatchAction.java,v $
$Revision: 1.1 $
$Date: 2006-05-19 22:30:47 $

The Web CGH Software License, Version 1.0

Copyright 2003 RTI. This software was developed in conjunction with the National 
Cancer Institute, and so to the extent government employees are co-authors, any 
rights in such works shall be subject to Title 17 of the United States Code, 
section 105.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this 
list of conditions and the disclaimer of Article 3, below. Redistributions in 
binary form must reproduce the above copyright notice, this list of conditions 
and the following disclaimer in the documentation and/or other materials 
provided with the distribution.

2. The end-user documentation included with the redistribution, if any, must 
include the following acknowledgment:

"This product includes software developed by the RTI and the National Cancer 
Institute."

If no such end-user documentation is to be included, this acknowledgment shall 
appear in the software itself, wherever such third-party acknowledgments 
normally appear.

3. The names "The National Cancer Institute", "NCI", 
�Research Triangle Institute�, and "RTI" must not be used to endorse or promote 
products derived from this software.

4. This license does not authorize the incorporation of this software into any 
proprietary programs. This license does not authorize the recipient to use any 
trademarks owned by either NCI or RTI.

5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, 
(INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL 
CANCER INSTITUTE, RTI, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.rti.webcgh.webui.cart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import org.rti.webcgh.array.Experiment;
import org.rti.webcgh.array.GenomeAssembly;
import org.rti.webcgh.array.QuantitationType;
import org.rti.webcgh.array.persistent.PersistentDomainObjectMgr;
import org.rti.webcgh.array.persistent.PersistentGenomeAssembly;
import org.rti.webcgh.array.persistent.PersistentOrganism;
import org.rti.webcgh.array.persistent.PersistentQuantitationType;
import org.rti.webcgh.io.SmdDataStream;
import org.rti.webcgh.webui.UploadExperimentForm;
import org.rti.webcgh.webui.common.CommonLookupDispatchAction;
import org.rti.webcgh.webui.common.ForwardEnum;
import org.rti.webcgh.webui.common.RequestParamsEnum;
import org.rti.webcgh.webui.common.SessionParamsEnum;

public class UploadExperimentDispatchAction extends CommonLookupDispatchAction {
	private PersistentDomainObjectMgr persistentDomainObjectMgr;
	private SmdDataStream smdDataStream;
	
	protected Map getKeyMethodMap() {
		
		 Map map = new HashMap();
	     map.put("button.upload", "upload");
	     map.put("onOrganismChanged", "onOrganismChanged");
	      
	     return map;
	}
	
	/**
	 * This dispatch method will load Organism, GenomeAssembly and QuantitationType data.
	 *
	 * @param mapping Routing information for downstream actions
	 * @param form Data from calling form
	 * @param request Servlet request object
	 * @param response Servlet response object
	 * @return Identification of downstream action as configured in the
	 * struts-config.xml file
	 * @throws Exception
	 */
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		
		// load organisms in session 
		loadOrganismAssemblyMap(session);
		
		// load genome assemblies, first time Collection is empty because it depends of the
		// organism selection
		Collection<LabelValueBean> assembliesLabelValues = new ArrayList();
		request.setAttribute(RequestParamsEnum.assemblies.toString(), assembliesLabelValues);
				
		// load quantitation types. 
		loadQuantitationTypes(session);
		
		return mapping.findForward(ForwardEnum.viewUpload.toString());
	}

	/**
	 * This dispatch method will load GenomeAssemblies depending of organism choosen.
	 * TODO: Replace this with java script for faster loading
	 * 
	 * @param mapping Routing information for downstream actions
	 * @param form Data from calling form
	 * @param request Servlet request object
	 * @param response Servlet response object
	 * @return Identification of downstream action as configured in the
	 * struts-config.xml file
	 * @throws Exception
	 */
	public ActionForward onOrganismChanged(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		UploadExperimentForm uef = (UploadExperimentForm)form;
		
		HttpSession session = request.getSession();
		
		// get the map; should be already in the session
		Map<PersistentOrganism, Collection<PersistentGenomeAssembly>> orgAssembliesMap = (Map<PersistentOrganism, Collection<PersistentGenomeAssembly>>)session.getAttribute(SessionParamsEnum.organismAssemblyMap.toString());		
		
		// get the assemblies for selected organism
		Collection<PersistentGenomeAssembly> assemblies = (Collection<PersistentGenomeAssembly>)orgAssembliesMap.get(new GenomeAssembly(uef.getAssemblyIdLong()));
		
		// convert this to LabelValue object
		Collection<LabelValueBean> qAssembliesLableValue = new ArrayList(assemblies.size());		
		for (PersistentGenomeAssembly assembly : assemblies){
			LabelValueBean lvb = new LabelValueBean(assembly.getName(), assembly.getId().toString());
			qAssembliesLableValue.add(lvb);
		}
		request.setAttribute(RequestParamsEnum.assemblies.toString(), qAssembliesLableValue);	
		
		return mapping.findForward(ForwardEnum.viewUpload.toString());
	}


	
	/**
	 * Uploads the file and converts it to Experiment.
	 * 
	 * @param mapping Routing information for downstream actions
	 * @param form Data from calling form
	 * @param request Servlet request object
	 * @param response Servlet response object
	 * @return Identification of downstream action as configured in the
	 * struts-config.xml file
	 * @throws Exception
	 */
	public ActionForward upload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		UploadExperimentForm uef = (UploadExperimentForm)form;
		
		HttpSession session = request.getSession();
		GenomeAssembly selAssembly = getAssemblyForOrganism(request, uef);
		
		Experiment exp = smdDataStream.loadExperiment( uef.getExperimentFile().getInputStream(), new QuantitationType(uef.getQuantitationTypeId()), selAssembly ) ;
		
		return mapping.findForward(ForwardEnum.viewUpload.toString());
	}

	
	public PersistentDomainObjectMgr getPersistentDomainObjectMgr() {
		return persistentDomainObjectMgr;
	}

	public void setPersistentDomainObjectMgr(
			PersistentDomainObjectMgr persistentDomainObjectMgr) {
		this.persistentDomainObjectMgr = persistentDomainObjectMgr;
	}

	public SmdDataStream getSmdDataStream() {
		return smdDataStream;
	}

	public void setSmdDataStream(SmdDataStream smdDataStream) {
		this.smdDataStream = smdDataStream;
	}
	
	/**
	 * 
	 * @param request
	 * @param uef
	 * @return selected GenomeAssembly object
	 */
	private GenomeAssembly getAssemblyForOrganism(HttpServletRequest request, UploadExperimentForm uef){
		HttpSession session = request.getSession();
		
		// get the map; should be already in the session
		Map<PersistentOrganism, Collection<PersistentGenomeAssembly>> orgAssembliesMap = (Map<PersistentOrganism, Collection<PersistentGenomeAssembly>>)session.getAttribute(SessionParamsEnum.organismAssemblyMap.toString());		
		Collection<PersistentGenomeAssembly> assemblies = (Collection<PersistentGenomeAssembly>)orgAssembliesMap.get(new GenomeAssembly(uef.getAssemblyIdLong()));
		
		for (PersistentGenomeAssembly assembly: assemblies){
			if (assembly.getId() == uef.getAssemblyIdLong())
				return assembly;
		}
		return null;
	}
	
	/**
	 * Load organisms if not already loaded in the session. 
	 * TODO: load this Map on application level not session for better performance
	 * @param session
	 */
	private void loadOrganismAssemblyMap(HttpSession session){
				
		Map<PersistentOrganism, Collection<PersistentGenomeAssembly>> orgAssemblies = (Map<PersistentOrganism, Collection<PersistentGenomeAssembly>>)session.getAttribute(SessionParamsEnum.organismAssemblyMap.toString());
		if (orgAssemblies == null){
			orgAssemblies = persistentDomainObjectMgr.getOrganismAssemblyMap();
			session.setAttribute(SessionParamsEnum.organismAssemblyMap.toString(), orgAssemblies);
		}	
		Set<PersistentOrganism> organisms = orgAssemblies.keySet();
		
		// conver this to LabelValueBean collection and set in the request
		Collection<LabelValueBean> organismLabelValues = new ArrayList(organisms.size());		
		for (PersistentOrganism org : organisms){
			LabelValueBean lvb = new LabelValueBean(org.getDisplayName(), org.getId().toString());
			organismLabelValues.add(lvb);
		}		
		session.setAttribute(SessionParamsEnum.organisms.toString(), organismLabelValues);
		
	}

	/**
	 * Load quantitation types. Looks like they are not coming from the db but are hard coded in QuantitationType class.
	 * @param session
	 */
	private void loadQuantitationTypes(HttpSession session){		
		Collection<LabelValueBean> qTypesLableValue = new ArrayList();				
		LabelValueBean lvb = new LabelValueBean(QuantitationType.LOG_2_RATIO.getName(), QuantitationType.LOG_2_RATIO.getName());
		qTypesLableValue.add(lvb);
		lvb = new LabelValueBean(QuantitationType.LOH.getName(), QuantitationType.LOH.getName());
		qTypesLableValue.add(lvb);
		lvb = new LabelValueBean(QuantitationType.UNKNOWN.getName(), QuantitationType.UNKNOWN.getName());
		qTypesLableValue.add(lvb);
	
		session.setAttribute(SessionParamsEnum.quantitationTypes.toString(), qTypesLableValue);
	}	

}