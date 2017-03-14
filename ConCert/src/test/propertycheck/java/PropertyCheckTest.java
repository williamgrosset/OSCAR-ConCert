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
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.apache.commons.io.FileUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import static org.junit.Assert.*;
import oscar.util.PropertyCheckAction;

/*
*  github.com/williamgrosset
*/
public class PropertyCheckTest {

    PropertyCheckAction propertyCheck = new PropertyCheckAction();
    private Method propertyCompareBool;

    @Before
    public void initialize() throws IOException, IllegalAccessException, NoSuchMethodException {
        propertyCompareBool = propertyCheck.getClass().getDeclaredMethod("propertyCompareBool", String.class, String.class);
        propertyCompareBool.setAccessible(true);
    }

    @Test
    public void isMatchTruePropertyCompareBool() throws IOException, IllegalAccessException, InvocationTargetException {
        File correctFile = File.createTempFile("correctInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(correctFile, "buildtag=oscar15");
        Boolean result = (Boolean)propertyCompareBool.invoke(propertyCheck, "buildtag", "oscar15");
        assertTrue(result);
        correctFile.deleteOnExit();
    }

    @Test
    public void isMatchFalsePropertyCompareBool() throws IOException, IllegalAccessException, InvocationTargetException {
        File correctFile = File.createTempFile("noncorrectInfo", ".properties", new File("/tmp"));
        FileUtils.writeStringToFile(correctFile, "buildtag=diffvalue");
        Boolean result = (Boolean)propertyCompareBool.invoke(propertyCheck, "buildtag", "oscar15");
        assertTrue(!result);
        correctFile.deleteOnExit();
    }

    @After
    public void tearDown() {
        propertyCompareBool = null;
        assertNull(propertyCompareBool);
    }
}
