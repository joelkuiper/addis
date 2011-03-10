/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.util;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBException;

import org.drugis.addis.entities.data.AddisData;
import org.drugis.addis.util.JAXBHandler;
import org.junit.Test;
import org.xml.sax.SAXException;

public class JAXBHandlerTest {
	@Test
	public void testUnmarshallMarshallXMLCompare() throws JAXBException, SAXException, FileNotFoundException {
		// read xml file
		AddisData data = JAXBHandler.unmarshallAddisData(new FileInputStream("schema/schematestxml.xml"));

		// write out
		JAXBHandler.marshallAddisData(data, new FileOutputStream("schema/jaxb_marshall_test.xml"));

		// read back generated xml
		AddisData data_clone = JAXBHandler.unmarshallAddisData(new FileInputStream("schema/jaxb_marshall_test.xml"));
		
		// compare
		assertEquals(data, data_clone);
	}
}