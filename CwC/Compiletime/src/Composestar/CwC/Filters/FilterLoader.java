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

package Composestar.CwC.Filters;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Composestar.Core.CKRET.SECRETResources;
import Composestar.Core.COPPER3.FilterTypeFactory;
import Composestar.Core.Config.CustomFilter;
import Composestar.Core.CpsRepository2.Filters.FilterTypeNames;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.Master.Master;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Class responsible for loading the filters used in Compose* / CwC
 * 
 * @author Michiel Hendriks
 */
public class FilterLoader
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Master.MODULE_NAME + ".FilterLoader");

	public static final String RESOURCE_KEY = "CwC.Filters.FilterLoader";

	protected Set<CustomCwCFilters> customFilters;

	public void load(CommonResources resources) /* throws Exception */
	{
		resources.put(RESOURCE_KEY, this);
		customFilters = new HashSet<CustomCwCFilters>();

		FilterTypeFactory filterFactory = new CwCFilterFactory(resources.repository());
		String[] filters = { FilterTypeNames.DISPATCH, FilterTypeNames.SEND, FilterTypeNames.ERROR,
				FilterTypeNames.BEFORE, FilterTypeNames.AFTER, FilterTypeNames.SUBSTITUTION, FilterTypeNames.VOID, };

		filterFactory.createDefaultFilterTypes(filters);
		resources.put(FilterTypeFactory.RESOURCE_KEY, filterFactory);

		SECRETResources secretResc = resources.getResourceManager(SECRETResources.class, true);

		List<URL> urls = new ArrayList<URL>();
		List<String> classNames = new ArrayList<String>();
		for (CustomFilter cf : resources.configuration().getFilters().getCustomFilters())
		{
			// Library should be in the form of:
			// URL/to/the/package#binary.name.of.the.class
			String pkg = cf.getLibrary();
			int idx = pkg.indexOf('#');
			if (idx == -1)
			{
				logger.error(String.format("Invalid location declaration for the custom filter: %s", pkg));
			}
			String className = pkg.substring(idx + 1);
			pkg = pkg.substring(0, idx);
			if (pkg.length() == 0)
			{
				// no package, use current classpath?
				classNames.add(className);
				continue;
			}
			URL url;
			try
			{
				url = new URL(pkg);
			}
			catch (MalformedURLException e)
			{
				File fl = new File(pkg);
				if (!fl.exists())
				{
					logger.error(String
							.format("'%s' is not valid URL and can not be resolved to an existing file", pkg));
					continue;
				}
				try
				{
					url = fl.toURI().toURL();
				}
				catch (MalformedURLException e1)
				{
					continue;
				}
			}
			// everything ok, add it to the loading list
			urls.add(url);
			classNames.add(className);
		}

		URL[] urlz = urls.toArray(new URL[0]);
		URLClassLoader clsLoader = URLClassLoader.newInstance(urlz, FilterLoader.class.getClassLoader());
		for (String className : classNames)
		{
			Class<?> cls;
			try
			{
				cls = clsLoader.loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				logger.error(e, e);
				continue;
			}
			if (!CustomCwCFilters.class.isAssignableFrom(cls))
			{
				logger.error(String.format("Class '%s' does not implement the interface %s", cls.toString(),
						CustomCwCFilters.class.toString()));
				continue;
			}
			CustomCwCFilters ccwcf;
			try
			{
				ccwcf = (CustomCwCFilters) cls.newInstance();
			}
			catch (InstantiationException e)
			{
				logger.error(e, e);
				continue;
			}
			catch (IllegalAccessException e)
			{
				logger.error(e, e);
				continue;
			}
			logger.info(String.format("Loaded CustomCwCFilters instance %s", className));
			customFilters.add(ccwcf);
			ccwcf.registerFilters(resources.repository(), filterFactory);
			ccwcf.registerSecretResources(secretResc);
		}
	}

	public Collection<FilterActionCodeGenerator<String>> getCodeGenerators()
	{
		Set<FilterActionCodeGenerator<String>> generators = new HashSet<FilterActionCodeGenerator<String>>();
		for (CustomCwCFilters cflt : customFilters)
		{
			Collection<FilterActionCodeGenerator<String>> col = cflt.getCodeGenerators();
			if (col != null)
			{
				generators.addAll(col);
			}
		}
		return generators;
	}
}
