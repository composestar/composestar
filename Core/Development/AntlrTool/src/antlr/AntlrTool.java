package antlr;

import java.util.List;

public class AntlrTool extends Tool
{
	private String m_language;
	
	public AntlrTool()
	{
		super();
		m_language = null;
	}
	
	public void setLanguage(String language)
	{
		m_language = language;
	}
	
	public String getLanguage(MakeGrammar behavior)
	{
		if (m_language == null)
		{
			return super.getLanguage(behavior);
		}
		else
		{
			return m_language;
		}
	}
	
	public int doEverything(List argList)
	{
		String[] args = new String[argList.size()];
		argList.toArray(args);
		
		return super.doEverything(args);
	}
}
