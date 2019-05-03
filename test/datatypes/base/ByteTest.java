/*
 *  Project:    moba-server
 *
 *  Copyright (C) 2016 stefan
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/agpl.txt>.
 *
 */

package datatypes.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class ByteTest {
    @Test
    public void testConstructor()
    throws Exception {
        Byte instance = new Byte();
        assertEquals(0, instance.getValue());
        instance = new Byte(5);
        assertEquals(5, instance.getValue());
        instance = new Byte(255);
        assertEquals(255, instance.getValue());
        try {
            instance = new Byte(256);
            fail("Expected an IllegalArgumentException to be thrown");
        }catch(Exception e) {
        }
        try {
            instance = new Byte(-1);
            fail("Expected an IllegalArgumentException to be thrown");
        }catch(Exception e) {
        }
    }

    @Test
    public void testSetValue() {
        Byte instance = new Byte();
        instance.setValue(0);
        assertEquals(0, instance.getValue());
        instance.setValue(5);
        assertEquals(5, instance.getValue());
        instance.setValue(0);
        assertEquals(0, instance.getValue());
        instance.setValue(255);
        assertEquals(255, instance.getValue());
        try {
            instance.setValue(256);
            fail("Expected an IllegalArgumentException to be thrown");
        }catch(Exception e) {
        }
        try {
            instance.setValue(-1);
            fail("Expected an IllegalArgumentException to be thrown");
        }catch(Exception e) {
        }
    }

    @Test
    public void testToJsonString()
    throws Exception {
        boolean formated = false;
        int indent = 0;
        Byte instance = new Byte(5);
        String expResult = "5";
        String result = instance.toJsonString(formated, indent);
        assertEquals(expResult, result);
    }
}
