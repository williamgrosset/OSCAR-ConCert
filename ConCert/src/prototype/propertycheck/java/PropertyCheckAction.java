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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oscar.OscarProperties;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.managers.SecurityInfoManager;

/*
*  github.com/williamgrosset
*/
public class PropertyCheckAction extends Action {

    private SecurityInfoManager securityInfoManager;
   
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String roleName = (String)servletRequest.getSession().getAttribute("userrole") + ","
                            + (String)servletRequest.getAttribute("user");
        PropertyCheckForm form = (PropertyCheckForm) actionForm;
        String property = form.getProperty();
        String value = form.getValue();
        securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

        if (!roleName.contains("admin")) {
            return actionMapping.findForward("unauthorized");
        }
        
        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(servletRequest), "_admin", "r", null)) {
            throw new SecurityException("Missing required security object (_admin)");
        }

        if (propertyCompareBool(property, value)) {
            return actionMapping.findForward("success");
        } else {
            return actionMapping.findForward("failure");
        }
    }
    
    private boolean propertyCompareBool(String property, String value) {
        return OscarProperties.getInstance().getBooleanProperty(property, value);
    }
}
