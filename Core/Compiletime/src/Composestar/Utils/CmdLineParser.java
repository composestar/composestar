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

package Composestar.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Utils.Logging.CPSLogger;

/**
 * Commandline parser. You can add accepted options by creating the appropiate
 * option class and add it to the CmdLineParser instance.
 * 
 * @author Michiel Hendriks
 */
public class CmdLineParser
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("CmdLineParser");

	protected static final String LONG_PREFIX = "--";

	protected static final String SHORT_PREFIX = "-";

	protected static final String LONG_VALUE_DELIM = "=";

	protected List<Option<?>> options;

	protected Map<Character, Option<?>> shortOptions;

	protected Map<String, Option<?>> longOptions;

	/**
	 * The default option, it will accept the commandline arguments without a -
	 * or -- prefix
	 */
	protected Option<?> defaultOption;

	public CmdLineParser()
	{
		options = new ArrayList<Option<?>>();
		shortOptions = new HashMap<Character, Option<?>>();
		longOptions = new HashMap<String, Option<?>>();
	}

	/**
	 * Parse the commandline argument list
	 * 
	 * @param args
	 */
	public void parse(String[] args)
	{
		int i = 0;
		while (i < args.length)
		{
			if (args[i].startsWith(LONG_PREFIX))
			{
				String lname = args[i].substring(LONG_PREFIX.length());
				String value = null;
				if (lname.contains(LONG_VALUE_DELIM))
				{
					int idx = lname.indexOf(LONG_VALUE_DELIM);
					value = lname.substring(idx + LONG_VALUE_DELIM.length());
					lname = lname.substring(0, idx);
				}
				Option<?> opt = longOptions.get(lname);
				if (opt == null)
				{
					logger.error(String.format("Unknown option %s%s", LONG_PREFIX, lname));
					i++;
					continue;
				}
				else
				{
					if (opt.needsValue() && value == null)
					{
						i++;
						if (i >= args.length)
						{
							logger.error(String.format("Missing value for %s%s", LONG_PREFIX, lname));
							continue;
						}
						value = args[i];
					}
					logger.debug(String.format("Long option %s: %s", lname, value));
					opt.parseValue(value);
				}
			}
			else if (args[i].startsWith(SHORT_PREFIX))
			{
				String lname = args[i].substring(SHORT_PREFIX.length());
				if (lname.length() < 1)
				{
					logger.error(String.format("Unknown option %s%s", SHORT_PREFIX, lname));
					i++;
					continue;
				}
				else
				{
					char sname = lname.charAt(0);
					lname = lname.substring(1);
					Option<?> opt = shortOptions.get(sname);
					if (opt == null)
					{
						logger.error(String.format("Unknown option %s%s", SHORT_PREFIX, lname));
						i++;
						continue;
					}
					else
					{
						if (opt.needsValue() && (lname == null || lname.length() == 0))
						{
							i++;
							if (i >= args.length)
							{
								logger.error(String.format("Missing value for %s%s", SHORT_PREFIX, lname));
								continue;
							}
							lname = args[i];
						}
						logger.debug(String.format("Short option %s: %s", sname, lname));
						opt.parseValue(lname);
					}
				}
			}
			else
			{
				if (defaultOption != null)
				{
					logger.debug(String.format("Default option: %s", args[i]));
					defaultOption.parseValue(args[i]);
				}
			}
			i++;
		}
	}

	public boolean addOption(Option<?> option)
	{
		char sname = option.getShortName();
		String lname = option.getLongName();
		if (sname != '\0' && shortOptions.containsKey(sname))
		{
			return false;
		}
		if (lname != null && longOptions.containsKey(lname))
		{
			return false;
		}
		options.add(option);
		if (sname != '\0')
		{
			shortOptions.put(sname, option);
		}
		if (lname != null)
		{
			longOptions.put(lname, option);
		}
		return true;
	}

	public void removeOption(Option<?> option)
	{
		if (option == null)
		{
			return;
		}
		options.remove(option);
		shortOptions.remove(option.getShortName());
		longOptions.remove(option.getLongName());
	}

	@SuppressWarnings("unchecked")
	public <T extends Option<?>> T getOption(char shortname)
	{
		Option<?> result = shortOptions.get(shortname);
		if (result == null)
		{
			return null;
		}
		else
		{
			return (T) result;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Option<?>> T getOption(String longname)
	{
		Option<?> result = longOptions.get(longname);
		if (result == null)
		{
			return null;
		}
		else
		{
			return (T) result;
		}
	}

	public void setDefaultOption(Option<?> option)
	{
		defaultOption = option;
	}

	/**
	 * A commandline option
	 * 
	 * @author
	 */
	public abstract static class Option<T>
	{
		protected String longName;

		protected char shortName;

		T value;

		protected boolean valueSet;

		protected Option()
		{}

		protected Option(String name)
		{
			this();
			longName = name;
		}

		protected Option(char name)
		{
			this();
			shortName = name;
		}

		protected Option(char sname, String lname)
		{
			this();
			shortName = sname;
			longName = lname;
		}

		/**
		 * @return the longName
		 */
		public String getLongName()
		{
			return longName;
		}

		/**
		 * @return the shortName
		 */
		public char getShortName()
		{
			return shortName;
		}

		/**
		 * @return the value
		 */
		public T getValue()
		{
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(T value)
		{
			this.value = value;
			valueSet = (value != null);
		}

		public boolean isSet()
		{
			return valueSet;
		}

		public abstract void parseValue(String rawValue);

		public boolean needsValue()
		{
			return true;
		}
	}

	public static class SwitchOption extends Option<Boolean>
	{
		public SwitchOption()
		{
			super();
		}

		public SwitchOption(char sname, String lname)
		{
			super(sname, lname);
		}

		public SwitchOption(char name)
		{
			super(name);
		}

		public SwitchOption(String name)
		{
			super(name);
		}

		@Override
		public boolean needsValue()
		{
			return false;
		}

		@Override
		public void parseValue(String rawValue)
		{
			if (rawValue != null && rawValue.trim().length() > 0)
			{
				throw new IllegalArgumentException("Switch does not take a value");
			}
			value = true;
			valueSet = true;
		}
	}

	public static class StringOption extends Option<String>
	{

		public StringOption()
		{
			super();

		}

		public StringOption(char sname, String lname)
		{
			super(sname, lname);
		}

		public StringOption(char name)
		{
			super(name);
		}

		public StringOption(String name)
		{
			super(name);
		}

		@Override
		public void parseValue(String rawValue)
		{
			if (rawValue == null)
			{
				throw new IllegalArgumentException("Value can not be null");
			}
			value = rawValue;
			valueSet = true;
		}

	}

	public static class IntOption extends Option<Integer>
	{
		public IntOption()
		{
			super();
		}

		public IntOption(char sname, String lname)
		{
			super(sname, lname);
		}

		public IntOption(char name)
		{
			super(name);
		}

		public IntOption(String name)
		{
			super(name);
		}

		@Override
		public void parseValue(String rawValue)
		{
			if (rawValue == null || rawValue.trim().length() == 0)
			{
				throw new IllegalArgumentException("Value can not be null or empty");
			}
			value = Integer.parseInt(rawValue.trim());
			valueSet = true;
		}
	}

	public static class FloatOption extends Option<Float>
	{

		public FloatOption()
		{
			super();
		}

		public FloatOption(char sname, String lname)
		{
			super(sname, lname);
		}

		public FloatOption(char name)
		{
			super(name);
		}

		public FloatOption(String name)
		{
			super(name);
		}

		@Override
		public void parseValue(String rawValue)
		{
			if (rawValue == null || rawValue.trim().length() == 0)
			{
				throw new IllegalArgumentException("Value can not be null or empty");
			}
			value = Float.parseFloat(rawValue.trim());
			valueSet = true;
		}
	}

	public static class SwitchListOption extends Option<Integer>
	{

		public SwitchListOption()
		{
			super();
		}

		public SwitchListOption(char sname, String lname)
		{
			super(sname, lname);
		}

		public SwitchListOption(char name)
		{
			super(name);
		}

		public SwitchListOption(String name)
		{
			super(name);
		}

		@Override
		public boolean needsValue()
		{
			return false;
		}

		@Override
		public void parseValue(String rawValue)
		{
			if (rawValue != null && rawValue.trim().length() > 0)
			{
				throw new IllegalArgumentException("Switch does not take a value");
			}
			value++;
			valueSet = true;
		}
	}

	public static class StringListOption extends Option<List<String>>
	{

		public StringListOption()
		{
			super();
			value = new ArrayList<String>();
		}

		public StringListOption(char sname, String lname)
		{
			super(sname, lname);
		}

		public StringListOption(char name)
		{
			super(name);
		}

		public StringListOption(String name)
		{
			super(name);
		}

		@Override
		public void parseValue(String rawValue)
		{
			if (rawValue == null)
			{
				throw new IllegalArgumentException("Value can not be null");
			}
			value.add(rawValue);
			valueSet = true;
		}
	}

	public static class IntListOption extends Option<List<Integer>>
	{

		public IntListOption()
		{
			super();
			value = new ArrayList<Integer>();
		}

		public IntListOption(char sname, String lname)
		{
			super(sname, lname);
		}

		public IntListOption(char name)
		{
			super(name);
		}

		public IntListOption(String name)
		{
			super(name);
		}

		@Override
		public void parseValue(String rawValue)
		{
			if (rawValue == null || rawValue.trim().length() == 0)
			{
				throw new IllegalArgumentException("Value can not be null or empty");
			}
			value.add(Integer.parseInt(rawValue.trim()));
			valueSet = true;
		}
	}

	public static class FloatListOption extends Option<List<Float>>
	{

		public FloatListOption()
		{
			super();
			value = new ArrayList<Float>();
		}

		public FloatListOption(char sname, String lname)
		{
			super(sname, lname);
		}

		public FloatListOption(char name)
		{
			super(name);
		}

		public FloatListOption(String name)
		{
			super(name);
		}

		@Override
		public void parseValue(String rawValue)
		{
			if (rawValue == null || rawValue.trim().length() == 0)
			{
				throw new IllegalArgumentException("Value can not be null or empty");
			}
			value.add(Float.parseFloat(rawValue.trim()));
			valueSet = true;
		}
	}
}
