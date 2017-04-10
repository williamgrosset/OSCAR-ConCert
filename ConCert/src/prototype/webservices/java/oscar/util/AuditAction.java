/**
 * Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Computer Science
 * LeadLab
 * University of Victoria
 * Victoria, Canada
 */

package oscar.util;

import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.managers.SecurityInfoManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
*  Action class that contains auditing information for the view (oscarAudit.jsp)
*  of the OSCAR Administration page.
*  
*  https://github.com/williamgrosset
*/
public class AuditAction extends Action {

    private SecurityInfoManager securityInfoManager;

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            Audit audit = new Audit();
            String tomcatVersion = servletRequest.getSession().getServletContext().getServerInfo();
            String webAppName = servletRequest.getSession().getServletContext().getContextPath().replace("/", "");
            securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

            servletRequest.setAttribute("serverVersion", audit.serverVersion());
            servletRequest.setAttribute("databaseInfo", audit.databaseInfo());
            servletRequest.setAttribute("verifyTomcat", audit.verifyTomcat(tomcatVersion));
            servletRequest.setAttribute("verifyOscar", audit.verifyOscar(tomcatVersion, webAppName));
            servletRequest.setAttribute("verifyDrugref", audit.verifyDrugref(tomcatVersion, webAppName));
            servletRequest.setAttribute("tomcatReinforcement", audit.tomcatReinforcement(tomcatVersion));
        } catch (Exception e) {
            return actionMapping.findForward("failure");
        }

        String roleName = (String)servletRequest.getSession().getAttribute("userrole") + "," + (String)servletRequest.getAttribute("user");
        if (!roleName.contains("admin") || securityInfoManager == null) {
            return actionMapping.findForward("unauthorized");
        }

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(servletRequest), "_admin", "r", null)) {
            throw new SecurityException("Missing required security object (_admin)");
        }

        return actionMapping.findForward("success");
    }
}
