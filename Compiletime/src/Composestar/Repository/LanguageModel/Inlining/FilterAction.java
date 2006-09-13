package Composestar.Repository.LanguageModel.Inlining;


public class FilterAction extends Instruction {
	private String type;
	private String selector;
	private String target;

	public FilterAction(String type, String selector, String target)
	{
		this.type = type;
		this.selector = selector;
		this.target = target;
	}

	/**
     * @return the message
	 * @property 
	 */
	public String get_Selector()
	{
		return selector;
	}

	/**
	 * @return the target
	 * @property 
	 */
	public String get_Target()
	{
		return target;
	}

	/**
     * @return the type
	 * @property 
     */
	public String get_Type()
	{
		return type;
	}
}
