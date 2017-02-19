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

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/*
*  github.com/williamgrosset
*/
public class PropertyCheck extends Action {
   
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

        PropertyCheckForm form = (PropertyCheckForm) actionForm;
        String property = form.getProperty();

        // Use OscarProperties class and verify if the tag is active.
        if (property.equals("") || property == null) {
            return actionMapping.findForward("hmm");
        } else {
            return actionMapping.findForward("unauthorized");
        }


        // if property does not exist (maybe should be handled by ActionForm validate()?)
        // return actionMapping.findForward("failure");

        // if property is active
        // return actionMapping.findForward("active");

        // if property is not active
        // return actionMapping.findForward("notActive");

        /*
        try {
            if (servletRequest.getSession().getAttribute("userrole") == null)
                servletResponse.sendRedirect("../logout.jsp");
        } catch (Exception e) {
            return actionMapping.findForward("failure");
        }

        String roleName = (String)servletRequest.getSession().getAttribute("userrole") + ","
                            + (String)servletRequest.getAttribute("user");
        if (!roleName.contains("admin")) {
            return actionMapping.findForward("unauthorized");
        }

        servletRequest.setAttribute("serverVersion", serverVersion());
        servletRequest.setAttribute("databaseInfo", databaseInfo());
        servletRequest.setAttribute("verifyTomcat", verifyTomcat());
        servletRequest.setAttribute("verifyOscar", verifyOscar());
        servletRequest.setAttribute("verifyDrugref", verifyDrugref());
        servletRequest.setAttribute("tomcatReinforcement", tomcatReinforcement());
        return actionMapping.findForward("success");
        */
    }
}
