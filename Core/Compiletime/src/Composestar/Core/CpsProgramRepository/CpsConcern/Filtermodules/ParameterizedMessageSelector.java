package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

public class ParameterizedMessageSelector extends MessageSelector
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5466576548506059667L;

	public String parameter;

	/**
	 * @deprecated
	 */
	public ParameterizedMessageSelector()
	{
		super();
	}

	public ParameterizedMessageSelector(MessageSelectorAST amsAST)
	{
		super(amsAST);
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
		return ((ParameterizedMessageSelectorAST) msAST).isList();
	}
}
