/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.integration.nclass.clientRegistry;

import org.oscarehr.common.model.Demographic;
import org.oscarehr.integration.nclass.ReferencedType;
import org.oscarehr.integration.nclass.Storyboard;

@Storyboard("PRPA_ST101311CA")
@ReferencedType("PRPA_AR101201CA")
public interface PersonComprehensivePlacer {

	/**
	 * Adds specified demographic to the registry.
	 * 
	 * @param demographic
	 * 		New demographic to be added to the registry
	 * 
	 * @see PersonRegistryQueryPlacer
	 * 
	 * @return
	 * 		Returns the external ID assigned withing the registry
	 */
	public String addPerson(Demographic demographic);

	/**
	 * Changes the person information in the registry
	 * 
	 * @param demographic
	 * 		Demographic be changed
	 * 		 
	 */
	public void revisePerson(Demographic demographic);

}
