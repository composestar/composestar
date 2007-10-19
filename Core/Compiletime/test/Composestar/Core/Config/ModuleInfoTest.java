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
import Composestar.Core.INCRE.INCREModule;

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
		assertTrue(mi.getBooleanSetting("aBool", false));
		assertFalse(mi.getBooleanSetting("anotherBool", true));
		assertEquals(12345, mi.getIntSetting("anInteger", 0));
		assertEquals("This is a string value", mi.getStringSetting("AString", "wrong"));

		try
		{
			ModuleSetting ms = mi.getSetting("anInteger");
			ms.setValue(54321);
			assertEquals(54321, ms.getIntValue());

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
			assertEquals("newValue", mi.getStringSetting("AString"));
			mi.setSettingValue("AString", null); // back to default
			assertEquals("This is a string value", mi.getStringSetting("AString"));
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
		assertEquals("test", m.getName());
		assertEquals(mi.getModuleClass(), m.getModuleClass());
		assertEquals(false, m.isIncremental());
	}

	public void testNestedLoading()
	{
		try
		{
			ModuleInfo emi = ModuleInfoManager.get(TestDummyEx.class);
			assertEquals(TestDummyEx.class, emi.getModuleClass());
			assertNotSame(mi, emi);
			assertEquals("This is a string value", emi.getStringSetting("AString"));
			assertEquals("Additional Setting", emi.getStringSetting("extraString", "wrong"));

			mi.setSettingValue("anInteger", new Integer(54321));
			if (mi.getIntSetting("anInteger") == emi.getIntSetting("anInteger"))
			{
				fail("emi.settings.anInteger should not be equal to mi.settings.anInteger");
			}

			INCREModule m = emi.getIncreModule();
			assertNotNull(m);
			assertEquals("test", m.getName());
			assertEquals(false, m.isIncremental());
			assertEquals(m.getModuleClass(), emi.getModuleClass());
		}
		catch (ConfigurationException e)
		{
			fail("Exception loading module info xml: " + e.getMessage());
		}
	}
}
