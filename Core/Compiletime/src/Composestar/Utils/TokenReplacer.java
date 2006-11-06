package Composestar.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marcus Klimstra
 */
public class TokenReplacer
{
	private Map m_replacements;
	
	public TokenReplacer()
	{
		m_replacements = new HashMap();		
	}
	
	public void addReplacement(String token, String replacement)
	{
		m_replacements.put(token, replacement);
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
		return (String)m_replacements.get(token);
	}
}
