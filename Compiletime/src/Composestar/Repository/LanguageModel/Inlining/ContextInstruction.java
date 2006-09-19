package Composestar.Repository.LanguageModel.Inlining;

import Composestar.Repository.LanguageModel.Inlining.Visitor.*;

public class ContextInstruction extends InlineInstruction implements IVisitable
{
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

	public void Accept(IVisitor visitor)
	{
		visitor.VisitContextInstruction(this); 
	}
}
