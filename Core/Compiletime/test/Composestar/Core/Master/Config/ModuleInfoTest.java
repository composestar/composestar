/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master.Config;

import junit.framework.TestCase;
import Composestar.Core.INCRE.INCREModule;

/**
 * @author Michiel Hendriks
 */
public class ModuleInfoTest extends TestCase
{
	protected ModuleInfo mi;

	protected void setUp() throws Exception
	{
		super.setUp();
		ModuleInfoManager.load(getClass().getResourceAsStream("moduleinfo.xml"));
		mi = ModuleInfoManager.get(TestDummy.class);
	}

	protected void tearDown() throws Exception
	{
		ModuleInfoManager.clear();
		mi = null;
		super.tearDown();
	}

	public void testInfo()
	{
		assertNotNull(mi);
		assertEquals(mi.getId(), "test");
		assertEquals(mi.getName(), "ModuleInfo Name");
		assertEquals(mi.getDescription(), "ModuleInfo Description");
	}

	public void testSettings()
	{
		assertTrue(mi.getBooleanSetting("aBool", false));
		assertFalse(mi.getBooleanSetting("anotherBool", true));
		assertEquals(mi.getIntSetting("anInteger", 12345), 12345);
		assertEquals(mi.getStringSetting("AString", "This is a string value"), "This is a string value");

		try
		{
			ModuleSetting ms = mi.getSetting("anInteger");
			ms.setValue(54321);
			assertEquals(ms.getIntValue(), 54321);

			try
			{
				ms.setValue(false);
				fail("No exception on invalid setValue");
			}
			catch (ConfigurationException e)
			{
			}

		}
		catch (ConfigurationException e)
		{
			fail("Failed to get ModuleSetting for 'anInteger'");
		}

		try
		{
			mi.setSettingValue("AString", "newValue");
			assertEquals(mi.getStringSetting("AString"), "newValue");
			mi.setSettingValue("AString", null); // back to default
			assertEquals(mi.getStringSetting("AString"), "This is a string value");
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

	public void testIncre()
	{
		INCREModule m = mi.getIncreModule();
		assertNotNull(m);
		assertEquals(m.getName(), "test");
		assertEquals(m.getModuleClass(), mi.getModuleClass());
		assertEquals(m.isIncremental(), false);
	}

	public void testNestedLoading()
	{
		try
		{
			ModuleInfo emi = ModuleInfoManager.get(TestDummyEx.class);
			assertEquals(emi.getModuleClass(), TestDummyEx.class);
			assertNotSame(emi, mi);
			assertEquals(emi.getStringSetting("AString"), "This is a string value");
			assertEquals(emi.getStringSetting("extraString", "wrong"), "Additional Setting");

			mi.setSettingValue("anInteger", new Integer(54321));
			if (mi.getIntSetting("anInteger") == emi.getIntSetting("anInteger"))
			{
				fail("emi.settings.anInteger should not be equal to mi.settings.anInteger");
			}

			INCREModule m = emi.getIncreModule();
			assertNotNull(m);
			assertEquals(m.getName(), "test");
			assertEquals(m.isIncremental(), false);
			assertEquals(m.getModuleClass(), emi.getModuleClass());
		}
		catch (ConfigurationException e)
		{
			fail("Exception loading module info xml: " + e.getMessage());
		}
	}
}
