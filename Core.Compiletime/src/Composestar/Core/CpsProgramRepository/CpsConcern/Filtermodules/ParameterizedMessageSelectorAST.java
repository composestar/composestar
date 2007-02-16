package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

public class ParameterizedMessageSelectorAST extends MessageSelectorAST
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6097571553057860569L;

	public String parameter;

	public boolean list;

	public ParameterizedMessageSelectorAST()
	{
		super();
		list = false; // by default
	}

	public String getParameter()
	{
		return parameter;
	}

	public void setParameter(String inParameter)
	{
		parameter = inParameter;
	}

	public boolean isList()
	{
		return list;
	}

	public void setList(boolean isList)
	{
		this.list = isList;
	}
}
