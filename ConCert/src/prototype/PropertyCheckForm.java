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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionErrors;

/*
*  github.com/williamgrosset
*/
public class PropertyCheckForm extends ActionForm {

    private String property;
    private String value;

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest servletRequest) {
        ActionErrors actionErrors = new ActionErrors();
        if (property == null || property.equals("") || property.contains("=")) {
            actionErrors.add("property", new ActionMessage("propertycheck.property.invalid"));
        }
        if (value == null || value.equals("")) {
            actionErrors.add("value", new ActionMessage("propertycheck.value.invalid"));
        }

        if (actionErrors.size() == 0)
            servletRequest.setAttribute("status", "success");

        return actionErrors;
    }

    /*
    @Override
    public void reset(ActionMapping actionMapping, HttpServletRequest servletRequest) {
        this.property = null;
        this.value = null;
    }*/
}
