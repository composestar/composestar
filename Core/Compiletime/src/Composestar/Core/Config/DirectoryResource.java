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

package Composestar.Core.Config;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import Composestar.Utils.Logging.CPSLogger;

/**
 * A FileCollection that scans a directory for files. This behaves much like the
 * fileset construction of Ant. By default it will include all files in the
 * given directory. The following wildcards are allowed:
 * <dl>
 * <dt>?</dt>
 * <dd>Match a single character</dd>
 * <dt>*</dt>
 * <dd>Match any number of characters except for file separator: /.</dd>
 * <dt>**</dt>
 * <dd>Match all characters include /.</dd>
 * </dl>
 * <em>Always use forward slashes for directories.</em> Common temporary files
 * and CVS/SVN files will always be ignored. Include patterns take preference
 * over exclude patterns.
 * 
 * @author Michiel Hendriks
 */
public class DirectoryResource extends FileCollection implements FileFilter
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Configuration.DirectoryResource");

	private static final long serialVersionUID = 7389476188523500459L;

	/**
	 * The base path, the directory to scan for files. It can be relative. In
	 * this case it is required to provide an absolute base directory in the
	 * getFiles() method.
	 */
	protected File path;

	/**
	 * Include filters
	 */
	protected Set<String> includes;

	/**
	 * Exclude filters
	 */
	protected Set<String> excludes;

	/**
	 * Cached list of found files. Chaning the patterns, path when a different
	 * absolute base is given this list will be purged.
	 */
	protected transient Set<File> cache;

	/**
	 * The cached absolute base used to populate the current cache.
	 */
	protected transient File cacheBase;

	/**
	 * Cache of compiled patterns.
	 */
	protected transient Set<Pattern> includePatterns;

	/**
	 * Cache of compiled patterns.
	 */
	protected transient Set<Pattern> excludePatterns;

	public DirectoryResource()
	{
		includes = new HashSet<String>();
		excludes = new HashSet<String>();
	}

	/**
	 * Set the base path
	 * 
	 * @param newPath
	 */
	public void setPath(File newPath)
	{
		if (newPath == null)
		{
			throw new IllegalArgumentException("Path can not be null");
		}
		path = newPath;
	}

	/**
	 * Return the base path. Can be absolute or relative
	 * 
	 * @return
	 */
	public File getPath()
	{
		return path;
	}

	/**
	 * Add an include directive
	 * 
	 * @param mask
	 */
	public void addInclude(String mask)
	{
		if (mask == null || mask.length() == 0)
		{
			throw new IllegalArgumentException("Mask can not be null or empty");
		}
		if (includes.add(mask))
		{
			cache = null;
			includePatterns = null;
		}
	}

	/**
	 * Add an exclude directive
	 * 
	 * @param mask
	 */
	public void addExclude(String mask)
	{
		if (mask == null || mask.length() == 0)
		{
			throw new IllegalArgumentException("Mask can not be null or empty");
		}
		if (excludes.add(mask))
		{
			cache = null;
			excludePatterns = null;
		}
	}

	/**
	 * Remove an include directive
	 * 
	 * @param mask
	 */
	public void removeInclude(String mask)
	{
		if (mask == null || mask.length() == 0)
		{
			throw new IllegalArgumentException("Mask can not be null or empty");
		}
		if (includes.remove(mask))
		{
			cache = null;
			includePatterns = null;
		}
	}

	/**
	 * Remove an exclude directive
	 * 
	 * @param mask
	 */
	public void removeExcludes(String mask)
	{
		if (mask == null || mask.length() == 0)
		{
			throw new IllegalArgumentException("Mask can not be null or empty");
		}
		if (excludes.remove(mask))
		{
			cache = null;
			excludePatterns = null;
		}
	}

	/**
	 * Returns a readonly list of include patterns.
	 * 
	 * @return
	 */
	public Set<String> getIncludes()
	{
		return Collections.unmodifiableSet(includes);
	}

	/**
	 * Returns a readonly list of exclude patterns.
	 * 
	 * @return
	 */
	public Set<String> getExcludes()
	{
		return Collections.unmodifiableSet(excludes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Config.FileCollection#addFile(java.lang.String)
	 */
	@Override
	public void addFile(String newFile)
	{
		throw new UnsupportedOperationException("DirectoryResource does not support adding files.");
	}

	/**
	 * Retrieve a list of files. This will assume the set path value is
	 * absolute. Otherwise an empty set will be returned.
	 */
	@Override
	public Set<File> getFiles()
	{
		return getFiles(null);
	}

	/**
	 * Return a list of files matching the pattern that are contained within the
	 * current base+path combination. The provided base will be used to resolve
	 * the relative path to an absolute path before scanning. When neither an
	 * absolute base or absolute path are available an empty set is returned.
	 * The returned files are all absolute.
	 */
	@Override
	public Set<File> getFiles(File base)
	{
		if (cache == null || !cacheBase.equals(base))
		{
			if (path.isAbsolute())
			{
				cacheBase = path;
			}
			else if (base != null && base.isAbsolute())
			{
				cacheBase = new File(base, path.toString());
			}
			else
			{
				logger.warn("DirectoryResource has an relative path and no abolute base was given.");
				return Collections.emptySet();
			}
			cache = new HashSet<File>();
			scanDirectory();
		}
		return cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Config.FileCollection#toString()
	 */
	@Override
	public String toString()
	{
		return path.toString();
	}

	/**
	 * Scans the cacheBase directory for files.
	 */
	protected void scanDirectory()
	{
		if (cacheBase == null || !cacheBase.isDirectory())
		{
			return;
		}
		if (includePatterns == null)
		{
			includePatterns = new HashSet<Pattern>();
			for (String str : includes)
			{
				includePatterns.add(convertPattern(str));
			}
		}
		if (excludePatterns == null)
		{
			excludePatterns = new HashSet<Pattern>();
			for (String str : excludes)
			{
				excludePatterns.add(convertPattern(str));
			}
			if (excludePatterns.size() == 0 && includePatterns.size() == 0)
			{
				excludePatterns.add(convertPattern("**/CVS/**"));
				excludePatterns.add(convertPattern("**/.cvsignore"));
				excludePatterns.add(convertPattern("**/.svn/**"));

				excludePatterns.add(convertPattern("**/#*#"));
				excludePatterns.add(convertPattern("**/*~"));
				excludePatterns.add(convertPattern("**/%*%"));
				excludePatterns.add(convertPattern("**/.#*"));
				excludePatterns.add(convertPattern("**/._*"));
			}
		}

		Stack<File> stack = new Stack<File>();
		stack.add(cacheBase);
		while (stack.size() > 0)
		{
			File dir = stack.pop();
			for (File file : dir.listFiles(this))
			{
				if (file.isDirectory())
				{
					stack.add(file);
				}
				else
				{
					cache.add(file);
				}
			}
		}
	}

	/**
	 * If true the current file matches the filters, e.g. is accepted
	 */
	public boolean accept(File file)
	{
		String str = file.toString().substring(cacheBase.toString().length()).replace("\\", "/");
		if (file.isDirectory())
		{
			str += "/";
		}
		logger.trace("Inspect file: " + str);
		for (Pattern inc : includePatterns)
		{
			if (inc.matcher(str).matches())
			{
				logger.trace("Include");
				return true;
			}
		}
		for (Pattern exc : excludePatterns)
		{
			if (exc.matcher(str).matches())
			{
				logger.trace("Exclude");
				return false;
			}
		}
		return includes.size() == 0;
	}

	/**
	 * Compiles the pattern to a regular expression.
	 */
	protected Pattern convertPattern(String inpattern)
	{
		String pattern = inpattern;
		if (pattern.endsWith("/"))
		{
			pattern += "**";
		}
		pattern = pattern.replaceAll("([.\\$^()])", "\\\\$1");
		if (inpattern.startsWith("/"))
		{
			pattern = "^" + pattern;
		}
		pattern = pattern.replaceAll("([^*])\\*([^*])", "$1([^/]*)$2");
		pattern = pattern.replaceAll("\\*\\*/", "(.*/)");
		pattern = pattern.replaceAll("/\\*\\*", "(/.*)");
		pattern = pattern.replaceAll("\\?", "(.)");
		pattern = pattern + "$";
		if (!inpattern.startsWith("/") && !inpattern.startsWith("*"))
		{
			pattern = "^(.*/)" + pattern;
		}
		logger.debug("Convert pattern from: " + inpattern + " to: " + pattern);
		return Pattern.compile(pattern);
	}
}
