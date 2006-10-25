package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

public class ParameterizedMessageSelector extends MessageSelector {
	public String parameter;
	
	public ParameterizedMessageSelector() {
		super();
	}

	public ParameterizedMessageSelector(MessageSelectorAST amsAST) {
		super(amsAST);
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	
	public boolean isList(){
		return ((ParameterizedMessageSelectorAST) msAST).isList();
	}
}
