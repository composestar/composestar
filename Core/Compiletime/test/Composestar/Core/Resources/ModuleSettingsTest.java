/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.Resources;

import junit.framework.TestCase;
import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;

/**
 * @author Michiel Hendriks
 */
public class ModuleSettingsTest extends TestCase
{
	protected CommonResources resc;

	protected TestModule module;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		resc = new CommonResources();
		BuildConfig conf = new BuildConfig();
		resc.setConfiguration(conf);
		module = new TestModule();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		resc = null;
	}

	public void testNoConfig()
	{
		resc.inject(module);
		assertEquals(12345, module.getIntValue());
		assertEquals("12345", module.getStringValue());
		assertEquals(1234.5f, module.getFloatValue());
		assertEquals(1234.5, module.getDoubleValue());
		assertEquals(false, module.isBoolValue());
		assertEquals(false, module.isRootItem());
		assertEquals(TestEnum.EVAL1, module.getEnumValue());
		assertEquals(12345, module.getSetterInt());
		assertEquals(12345, module.getSetterInt2());
	}

	public void testConfig()
	{
		resc.configuration().addSetting("TEST.intValue", "54321");
		resc.configuration().addSetting("TEST.string", "54321");
		resc.configuration().addSetting("CORE.longValue", "54321");
		resc.configuration().addSetting("TEST.floatValue", "5432.1");
		resc.configuration().addSetting("TEST.doubleValue", "5432.1");
		resc.configuration().addSetting("TEST.boolValue", "true");
		resc.configuration().addSetting("rootItem", "true");
		resc.configuration().addSetting("TEST.enumValue", "EVAL2");
		resc.configuration().addSetting("TEST.setterInt", "54321");
		resc.configuration().addSetting("TEST.setterInt2", "string is 16 long");
		resc.inject(module);
		assertEquals(54321, module.getIntValue());
		assertEquals("54321", module.getStringValue());
		assertEquals(5432.1f, module.getFloatValue());
		assertEquals(5432.1, module.getDoubleValue());
		assertEquals(true, module.isBoolValue());
		assertEquals(true, module.isRootItem());
		assertEquals(TestEnum.EVAL2, module.getEnumValue());
		assertEquals(54321, module.getSetterInt());
		assertEquals(17, module.getSetterInt2());
	}

	public void testInvalidFormats()
	{
		resc.configuration().addSetting("TEST.intValue", "not an integer");
		resc.configuration().addSetting("CORE.longValue", "not a long");
		resc.configuration().addSetting("TEST.floatValue", "not a float");
		resc.configuration().addSetting("TEST.doubleValue", "not a double");
		resc.configuration().addSetting("TEST.enumValue", "not an enum");
		resc.configuration().addSetting("TEST.setterInt", "not an int");
		resc.inject(module);
		assertEquals(12345, module.getIntValue());
		assertEquals(1234.5f, module.getFloatValue());
		assertEquals(1234.5, module.getDoubleValue());
		assertEquals(TestEnum.EVAL1, module.getEnumValue());
		assertEquals(12345, module.getSetterInt());
	}

	public enum TestEnum
	{
		EVAL1, EVAL2
	}

	public static class TestModule implements CTCommonModule
	{
		@ModuleSetting
		private int intValue = 12345;

		@ModuleSetting(ID = "string")
		private String stringValue = "12345";

		@ModuleSetting(ID = "CORE.longValue")
		private long longValue = 12345;

		@ModuleSetting
		private float floatValue = 1234.5f;

		@ModuleSetting
		private double doubleValue = 1234.5;

		@ModuleSetting
		private TestEnum enumValue = TestEnum.EVAL1;

		@ModuleSetting
		private boolean boolValue = false;

		@ModuleSetting(ID = ".rootItem")
		private boolean rootItem = false;

		@ModuleSetting(setter = "setSetterInt")
		private int setterInt = 12345;

		@ModuleSetting(setter = "setSetterInt2", setterTakesString = true)
		private int setterInt2 = 12345;

		public TestModule()
		{}

		/**
		 * @return the intValue
		 */
		public int getIntValue()
		{
			return intValue;
		}

		/**
		 * @param intValue the intValue to set
		 */
		public void setIntValue(int intValue)
		{
			this.intValue = intValue;
		}

		/**
		 * @return the stringValue
		 */
		public String getStringValue()
		{
			return stringValue;
		}

		/**
		 * @param stringValue the stringValue to set
		 */
		public void setStringValue(String stringValue)
		{
			this.stringValue = stringValue;
		}

		/**
		 * @return the longValue
		 */
		public long getLongValue()
		{
			return longValue;
		}

		/**
		 * @param longValue the longValue to set
		 */
		public void setLongValue(long longValue)
		{
			this.longValue = longValue;
		}

		/**
		 * @return the floatValue
		 */
		public float getFloatValue()
		{
			return floatValue;
		}

		/**
		 * @param floatValue the floatValue to set
		 */
		public void setFloatValue(float floatValue)
		{
			this.floatValue = floatValue;
		}

		/**
		 * @return the doubleValue
		 */
		public double getDoubleValue()
		{
			return doubleValue;
		}

		/**
		 * @param doubleValue the doubleValue to set
		 */
		public void setDoubleValue(double doubleValue)
		{
			this.doubleValue = doubleValue;
		}

		/**
		 * @return the enumValue
		 */
		public TestEnum getEnumValue()
		{
			return enumValue;
		}

		/**
		 * @param enumValue the enumValue to set
		 */
		public void setEnumValue(TestEnum enumValue)
		{
			this.enumValue = enumValue;
		}

		/**
		 * @return the boolValue
		 */
		public boolean isBoolValue()
		{
			return boolValue;
		}

		/**
		 * @param boolValue the boolValue to set
		 */
		public void setBoolValue(boolean boolValue)
		{
			this.boolValue = boolValue;
		}

		/**
		 * @return the setterInt
		 */
		public int getSetterInt()
		{
			return setterInt;
		}

		/**
		 * @param setterInt the setterInt to set
		 */
		public void setSetterInt(int setterInt)
		{
			this.setterInt = setterInt;
		}

		/**
		 * @return the setterInt2
		 */
		public int getSetterInt2()
		{
			return setterInt2;
		}

		/**
		 * @param setterInt2 the setterInt2 to set
		 */
		public void setSetterInt2(String setterInt2)
		{
			this.setterInt2 = setterInt2.length();
		}

		/**
		 * @return the rootItem
		 */
		public boolean isRootItem()
		{
			return rootItem;
		}

		/**
		 * @param rootItem the rootItem to set
		 */
		public void setRootItem(boolean rootItem)
		{
			this.rootItem = rootItem;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
		 */
		public String getModuleName()
		{
			return "TEST";
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
		 */
		public String[] getDependencies()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see Composestar.Core.Master.CTCommonModule#getImportance()
		 */
		public ModuleImportance getImportance()
		{
			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
		 * .CommonResources)
		 */
		public ModuleReturnValue run(CommonResources resources) throws ModuleException
		{
			return null;
		}
	}
}
