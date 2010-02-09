package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.ContinuousVariable;
import org.drugis.addis.entities.Variable;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("serial")
public class SelectPopulationCharsPresentationTest {
	private Variable d_var1 = ExampleData.buildAgeVariable();
	private Variable d_var2 = ExampleData.buildGenderVariable();
	private Variable d_var3 = new ContinuousVariable("Blood Pressure");
	private ListHolder<Variable> d_list;
	private SelectPopulationCharsPresentation d_pm;
	
	@Before
	public void setUp() {
		d_list = new AbstractListHolder<Variable>() {
			@Override
			public List<Variable> getValue() {
				List<Variable> l = new ArrayList<Variable>();
				l.add(d_var1);
				l.add(d_var2);
				return l;
			}
		};
		
		d_pm = new SelectPopulationCharsPresentation(d_list);
	}
	
	@Test
	public void testGetTypeName() {
		assertEquals("Population Characteristic", d_pm.getTypeName());
	}
	
	@Test
	public void testHasAddOptionDialog() {
		assertFalse(d_pm.hasAddOptionDialog());
	}
	
	@Test
	public void testGetTitle() {
		assertEquals("Select Population Characteristics", d_pm.getTitle());
		assertEquals("Please select the appropriate population characteristics.", d_pm.getDescription());
	}
	
	@Test
	public void testGetOptions() {
		assertEquals(d_list.getValue(), d_pm.getOptions().getValue());
		d_list.getValue().add(d_var3);
		assertEquals(d_list.getValue(), d_pm.getOptions().getValue());
	}
	
	@Test
	public void testAddSlot() {
		assertEquals(0, d_pm.countSlots());
		d_pm.addSlot();
		assertEquals(1, d_pm.countSlots());
	}
	
	@Test
	public void testGetSlot() {
		d_pm.addSlot();
		d_pm.getSlot(0).setValue(d_var2);
		assertEquals(d_var2, d_pm.getSlot(0).getValue());
	}
	
	@Test
	public void testRemoveSlot() {
		d_pm.addSlot();
		assertEquals(1, d_pm.countSlots());
		d_pm.removeSlot(0);
		assertEquals(0, d_pm.countSlots());
		
		d_pm.addSlot();
		d_pm.getSlot(0).setValue(d_var1);
		d_pm.addSlot();
		d_pm.getSlot(1).setValue(d_var2);
		d_pm.removeSlot(0);
		assertEquals(d_pm.getSlot(0).getValue(), d_var2);
	}

	@Test
	public void testAddSlotsEnabledModel() {
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.TRUE);
		
		d_pm.addSlot();
		
		// Make sure adding is disabled when we have as many slots as options
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm.getAddSlotsEnabledModel(), "value",
				Boolean.TRUE, Boolean.FALSE);
		d_pm.getAddSlotsEnabledModel().addValueChangeListener(mock);
		d_pm.addSlot();
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.FALSE);
		verify(mock);
		
		// Make sure removing slots gets it back to normal
		mock = JUnitUtil.mockListener(d_pm.getAddSlotsEnabledModel(), "value",
				Boolean.FALSE, Boolean.TRUE);
		d_pm.getAddSlotsEnabledModel().addValueChangeListener(mock);
		d_pm.removeSlot(1);
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.TRUE);
		verify(mock);
	}
	
	@Test
	public void testInputCompleteModel() {
		assertEquals(Boolean.TRUE, d_pm.getInputCompleteModel().getValue());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm.getInputCompleteModel(), "value",
				Boolean.TRUE, Boolean.FALSE);
		d_pm.getInputCompleteModel().addValueChangeListener(mock);
		d_pm.addSlot();
		assertEquals(Boolean.FALSE, d_pm.getInputCompleteModel().getValue());
		verify(mock);
		
		mock = JUnitUtil.mockListener(d_pm.getInputCompleteModel(), "value",
				Boolean.FALSE, Boolean.TRUE);
		d_pm.getInputCompleteModel().addValueChangeListener(mock);
		d_pm.getSlot(0).setValue(d_var2);
		assertEquals(Boolean.TRUE, d_pm.getInputCompleteModel().getValue());
		verify(mock);
	}
	
	@Test
	public void testSelectSameValueTwiceRemovesFromFirst() {
		d_pm.addSlot();
		d_pm.addSlot();
		d_pm.getSlot(1).setValue(d_var1);
		assertEquals(d_var1, d_pm.getSlot(1).getValue());
		d_pm.getSlot(0).setValue(d_var1);
		assertEquals(d_var1, d_pm.getSlot(0).getValue());
		assertEquals(null, d_pm.getSlot(1).getValue());
	}
}
