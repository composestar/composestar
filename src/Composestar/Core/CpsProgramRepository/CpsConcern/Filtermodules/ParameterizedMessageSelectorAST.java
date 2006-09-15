package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

public class ParameterizedMessageSelectorAST extends MessageSelectorAST {
	public String parameter;
	
	public ParameterizedMessageSelectorAST() {
		super();
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
