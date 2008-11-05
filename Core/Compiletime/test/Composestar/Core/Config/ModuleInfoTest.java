/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Config;

import junit.framework.TestCase;
import Composestar.Core.Exception.ConfigurationException;

/**
 * @author Michiel Hendriks
 */
public class ModuleInfoTest extends TestCase
{
	protected ModuleInfo mi;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ModuleInfoManager.load(getClass().getResourceAsStream("moduleinfo.xml"));
		mi = ModuleInfoManager.get(TestDummy.class);
	}

	@Override
	protected void tearDown() throws Exception
	{
		ModuleInfoManager.clear();
		mi = null;
		super.tearDown();
	}

	public void testInfo()
	{
		assertNotNull(mi);
		assertEquals("test", mi.getId());
		assertEquals("ModuleInfo Name", mi.getName());
		assertEquals("ModuleInfo Description", mi.getDescription());
	}

	public void testSettings()
	{
		assertTrue(mi.getSetting("aBool", false));
		assertFalse(mi.getSetting("anotherBool", true));
		int x = mi.getSetting("anInteger", 0);
		assertEquals(12345, x);
		assertEquals("This is a string value", mi.getSetting("AString", "wrong"));

		try
		{
			ModuleSetting<Integer> ms = mi.getModuleSetting("anInteger");
			ms.setValue(54321);
			x = ms.getValue();
			assertEquals(54321, x);
		}
		catch (ConfigurationException e)
		{
			fail("Failed to get ModuleSetting for 'anInteger'");
		}

		try
		{
			mi.setSettingValue("AString", "newValue");
			assertEquals("newValue", mi.getSetting("AString"));
			mi.setSettingValue("AString", null); // back to default
			assertEquals("This is a string value", mi.getSetting("AString"));
		}
		catch (ConfigurationException e)
		{
			fail("Failed to set new value");
		}

		try
		{
			mi.setSettingValue("non existing setting", null);
			fail("No exception on non existing value");
		}
		catch (ConfigurationException e)
		{
		}
	}

	public void testNestedLoading()
	{
		try
		{
			ModuleInfo emi = ModuleInfoManager.get(TestDummyEx.class);
			assertEquals(TestDummyEx.class, emi.getModuleClass());
			assertNotSame(mi, emi);
			assertEquals("This is a string value", emi.getSetting("AString"));
			assertEquals("Additional Setting", emi.getSetting("extraString", "wrong"));

			mi.setSettingValue("anInteger", new Integer(54321));
			if (mi.getSetting("anInteger") == emi.getSetting("anInteger"))
			{
				fail("emi.settings.anInteger should not be equal to mi.settings.anInteger");
			}
		}
		catch (ConfigurationException e)
		{
			fail("Exception loading module info xml: " + e.getMessage());
		}
	}
}
