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

package Composestar.Core.Config.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.INCRE.INCREModule;
import Composestar.Core.INCRE.Config.ModulesHandler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Loaded a module info from an XML file
 * 
 * @author Michiel Hendriks
 */
public class ModuleInfoHandler extends CpsBaseHandler
{
	public static final String NAMESPACE = "http://composestar.sourceforge.net/schema/ModuleInfo";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger("ModuleInfo");

	/**
	 * Handling a module definition
	 */
	protected static final int STATE_MODULEINFO = 1;

	/**
	 * Handling the module name
	 */
	protected static final int STATE_NAME = 2;

	/**
	 * Handling the module description definition
	 */
	protected static final int STATE_DESC = 3;

	/**
	 * Processing the module settings
	 */
	protected static final int STATE_SETTINGS = 4;

	/**
	 * Processing the module dependencies
	 */
	protected static final int STATE_DEPENDS = 5;

	/**
	 * Processing an INCRE configuration
	 */
	protected static final int STATE_INCRE = 6;

	/**
	 * The resulting module definition
	 */
	protected ModuleInfo currentMi;

	/**
	 * The module settings XML handler
	 */
	protected ModuleSettingHandler moduleSetting;

	/**
	 * The INCRE configuration XML handler
	 */
	protected ModulesHandler increHandler;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public ModuleInfoHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	/**
	 * @return the defined module information
	 */
	public ModuleInfo getModuleInfo()
	{
		return currentMi;
	}

	/**
	 * Tries to load the extended module and requests a deep copy on the module
	 * info
	 * 
	 * @param source
	 * @return
	 */
	protected ModuleInfo processExtends(String source)
	{
		logger.info("Loading extended module info: " + source);
		ModuleInfo extMi = null;
		Class<?> extClass;
		try
		{
			extClass = Class.forName(source);
		}
		catch (ClassNotFoundException e)
		{
			extClass = null;
		}
		if (extClass != null)
		{
			extMi = ModuleInfoManager.get(extClass);
		}
		else
		{
			extMi = ModuleInfoManager.get(source);
		}
		return extMi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Config.Xml.CpsBaseHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		try
		{
			super.startElement(uri, localName, name, attributes);
			if (state == 0 && "moduleinfo".equals(name))
			{
				state = STATE_MODULEINFO;
				String extClass = attributes.getValue("extends");
				String id = attributes.getValue("id");
				ModuleInfo extMi = null;
				if (extClass != null && extClass.trim().length() != 0)
				{
					extMi = processExtends(extClass.trim());
					if (extMi == null)
					{
						logger.error(String.format("Unable to inherit from %s", extClass));
					}
				}
				if (id != null && extMi != null)
				{
					currentMi = new ModuleInfo(id, extMi);
				}
				else if (extMi != null)
				{
					currentMi = new ModuleInfo(extMi);
				}
				else if (id != null)
				{
					currentMi = new ModuleInfo(id);
				}
				else
				{
					throw new SAXParseException("ModuleInfo must have either an ID or extend an other module", locator);
				}
				try
				{
					currentMi.setModuleClass(attributes.getValue("class"));
				}
				catch (ClassNotFoundException e)
				{
					logger.error(String.format("Module class not found: %s", attributes.getValue("class")), e);
				}
			}
			else if (state == STATE_MODULEINFO && "name".equals(name))
			{
				state = STATE_NAME;
			}
			else if (state == STATE_MODULEINFO && "description".equals(name))
			{
				state = STATE_DESC;
			}
			else if (state == STATE_MODULEINFO && "settings".equals(name))
			{
				state = STATE_SETTINGS;
			}
			else if (state == STATE_SETTINGS && "setting".equals(name))
			{
				if (Boolean.valueOf(attributes.getValue("remove")))
				{
					// remove the setting
					currentMi.removeSetting(attributes.getValue("id"));
				}
				else
				{
					moduleSetting = new ModuleSettingHandler(reader, this);
					reader.setContentHandler(moduleSetting);
					moduleSetting.startElement(uri, localName, name, attributes);
				}
			}
			else if (state == STATE_MODULEINFO && "dependson".equals(name))
			{
				state = STATE_DEPENDS;
			}
			else if (state == STATE_DEPENDS && "module".equals(name))
			{
				// nop
			}
			else if (state == STATE_MODULEINFO && "incre".equals(name))
			{
				state = STATE_INCRE;
				increHandler = new ModulesHandler(reader, this);
				reader.setContentHandler(increHandler);
			}
			else
			{
				startUnknownElement(uri, localName, name, attributes);
			}
		}
		catch (IllegalArgumentException e)
		{
			throw new SAXParseException(e.getMessage(), locator);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Config.Xml.CpsBaseHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		try
		{
			super.endElement(uri, localName, name);
			if (state == STATE_MODULEINFO && "moduleinfo".equals(name))
			{
				if (currentMi.getIncreModule() == null)
				{
					INCREModule increModule = new INCREModule(currentMi.getId());
					increModule.setModuleClass(currentMi.getModuleClass());
					increModule.setEnabled(true);
					currentMi.setIncreModule(increModule);
				}
				returnHandler(uri, localName, name);
			}
			else if (state == STATE_NAME && "name".equals(name))
			{
				state = STATE_MODULEINFO;
				currentMi.setName(charData.toString());

			}
			else if (state == STATE_DESC && "description".equals(name))
			{

				state = STATE_MODULEINFO;
				currentMi.setDescription(charData.toString());

			}
			else if (state == STATE_SETTINGS && "settings".equals(name))
			{
				state = STATE_MODULEINFO;
			}
			else if (state == STATE_SETTINGS && "setting".equals(name))
			{
				if (moduleSetting != null && moduleSetting.getModuleSetting() != null)
				{
					try
					{
						currentMi.removeSetting(moduleSetting.getModuleSetting().getId()); // overloading
						currentMi.addModuleSetting(moduleSetting.getModuleSetting());
					}
					catch (ConfigurationException e)
					{
						throw new SAXParseException(e.getMessage(), locator);
					}
				}
			}
			else if (state == STATE_DEPENDS && "dependson".equals(name))
			{
				state = STATE_MODULEINFO;
			}
			else if (state == STATE_DEPENDS && "module".equals(name))
			{
				currentMi.addDepedency(charData.toString().trim());
			}
			else if (state == STATE_INCRE && "incre".equals(name))
			{
				state = STATE_MODULEINFO;
				INCREModule increModule = increHandler.getModule();
				increModule.setName(currentMi.getId());
				increModule.setModuleClass(currentMi.getModuleClass());
				currentMi.setIncreModule(increModule);
				increHandler = null;
			}
			else
			{
				endUnknownElement(uri, localName, name);
			}
		}
		catch (IllegalArgumentException e)
		{
			throw new SAXParseException(e.getMessage(), locator);
		}
	}
}
