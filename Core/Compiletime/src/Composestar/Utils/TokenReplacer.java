/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Klimstra
 */
public class TokenReplacer
{
	private Map<String,String> replacements;

	public TokenReplacer()
	{
		replacements = new HashMap<String,String>();
	}

	public void addReplacement(String token, String replacement)
	{
		replacements.put(token, replacement);
	}

	public String process(String s)
	{
		int end = s.length() - 1;
		int pos = 0;

		StringBuffer sb = new StringBuffer();
		while (pos < end)
		{
			int lcurly = s.indexOf('{', pos);
			if (lcurly == -1)
			{
				break;
			}

			int rcurly = s.indexOf('}', lcurly);
			if (rcurly == -1 || rcurly - lcurly < 1)
			{
				pos = lcurly;
				continue;
			}

			String prefix = s.substring(pos, lcurly);
			sb.append(prefix);

			String token = s.substring(lcurly + 1, rcurly);
			String replacement = getReplacement(token);
			if (replacement == null)
			{
				pos = lcurly;
				continue;
			}

			sb.append(replacement);
			pos = rcurly + 1;
		}

		if (end - pos > 0)
		{
			sb.append(s.substring(pos));
		}

		return sb.toString();
	}

	private String getReplacement(String token)
	{
		return (String) replacements.get(token);
	}
}
