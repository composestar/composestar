/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.SECRET3;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.SECRET3.SECRET;
import Composestar.Core.SECRET3.ResourceOperationLabelerEx;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Config.Xml.XmlConfiguration;

/**
 * @author Michiel Hendriks
 */
public class SecretConfigTest extends TestCase
{
	protected SECRETResources resources;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		resources = new SECRETResources();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		resources = null;
	}

	public void testLoading() throws ConfigurationException
	{
		XmlConfiguration.loadBuildConfig(SECRET.class.getResourceAsStream("SECRETConfig.xml"), resources);
		resources.setLabeler(new ResourceOperationLabelerEx(resources));
	}

	public static void main(String[] args)
	{
		BasicConfigurator.configure();
		SecretConfigTest t = new SecretConfigTest();
		try
		{
			t.setUp();
			t.testLoading();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
