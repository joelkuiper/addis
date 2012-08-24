/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

package org.drugis.addis.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Line2D;

import org.junit.Test;

public class LineTest {
	@Test
	public void testEqual() {
		Line expected = new Line(10, 20, 30, 40);
		Line2D actual = new Line2D.Double(10, 20, 30, 40);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiscriminateP1() {
		Line unexpected = new Line(15, 20, 30, 40);
		Line2D actual = new Line2D.Double(10, 20, 30, 40);
		assertTrue(!unexpected.equals(actual));
	}
	
	@Test
	public void testDiscriminateP2() {
		Line unexpected = new Line(10, 20, 30, 45);
		Line2D actual = new Line2D.Double(10, 20, 30, 40);
		assertTrue(!unexpected.equals(actual));
	}
	
	@Test
	public void testToString() {
		Line l = new Line(10,20,30,40);
		assertEquals("Line(x1 = 10.0, y1 = 20.0, x2 = 30.0, y2 = 40.0)", l.toString());
	}
}
