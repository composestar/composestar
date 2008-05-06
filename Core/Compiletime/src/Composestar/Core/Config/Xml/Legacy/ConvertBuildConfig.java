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

package Composestar.Core.Config.Xml.Legacy;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Convert an old buildconfig to the new format
 * 
 * @author Michiel Hendriks
 */
public class ConvertBuildConfig
{
	static ConvertBuildConfig instance;

	/**
	 * Xslt factory
	 */
	protected TransformerFactory factory;

	/**
	 * Xslt transformer
	 */
	protected Transformer xform;

	/**
	 * @return an instance to the build configuration file translator
	 */
	public static ConvertBuildConfig getInstance()
	{
		if (instance == null)
		{
			instance = new ConvertBuildConfig();
		}
		return instance;
	}

	protected ConvertBuildConfig()
	{}

	/**
	 * Get the transformer instance
	 * 
	 * @throws TransformerConfigurationException
	 */
	protected void getTransformer() throws TransformerConfigurationException
	{
		if (xform != null)
		{
			return;
		}
		if (factory == null)
		{
			factory = TransformerFactory.newInstance();
		}
		Source source = new StreamSource(ConvertBuildConfig.class.getResourceAsStream("bc1to2.xslt"));
		xform = factory.newTransformer(source);
	}

	/**
	 * Convert the source input to the latest version of the Build Configuration
	 * 
	 * @param is
	 * @param os
	 * @param sourceVersion
	 * @throws TransformerException
	 */
	public void convert(InputStream is, OutputStream os, String sourceVersion) throws TransformerException
	{
		getTransformer();
		Source source = new StreamSource(is);
		Result result = new StreamResult(os);
		xform.transform(source, result);
	}
}
