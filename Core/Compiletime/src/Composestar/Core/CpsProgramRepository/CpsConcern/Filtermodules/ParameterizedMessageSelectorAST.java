package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

public class ParameterizedMessageSelectorAST extends MessageSelectorAST {
	public String parameter;
	public boolean list;
	
	public ParameterizedMessageSelectorAST() {
		super();
		list = false; //by default
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean isList) {
		this.list = isList;
	}
}
