package Composestar.Repository.LanguageModel.Inlining;


public class ContextInstruction extends Instruction {
	private String type;
	private String method;
	private boolean enabled;

	public ContextInstruction(String type, String method)
	{
		this.type = type;
		this.method = method;
	}

	/**
     * @return the method
	 * @property 
	 */
	public String get_Method()
	{
		return method;
	}

	/**
     * @return the type
	 * @property 
     */
	public String get_Type()
	{
			return type;
	}

	/**
     * @return the enabled
	 * @property 
     */
	public boolean get_IsEnabled()
	{
			return enabled;
	}

	/**
     * @param enabled the enabled to set
	 * @property 
     */
	public void set_IsEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

}
