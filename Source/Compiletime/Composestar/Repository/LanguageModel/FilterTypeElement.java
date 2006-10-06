/*
 * Created on 4-okt-2006
 *
 */
package Composestar.Repository.LanguageModel;

public class FilterTypeElement
{
	private String name;

	private String acceptCallAction;

	private String rejectCallAction;

	private String acceptReturnAction;

	private String rejectReturnAction;

	/**
	 * @return the name
	 * @property
	 */
	public String get_Name()
	{
		return name;
	}

	/**
	 * @param value the name to set
	 * @property
	 */
	public void set_Name(String value)
	{
		this.name = value;
	}

	/**
	 * @return the acceptCallAction
	 * @property
	 */
	public String get_AcceptCallAction()
	{
		return acceptCallAction;
	}

	/**
	 * @param value the acceptCallAction to set
	 * @property
	 */
	public void set_AcceptCallAction(String value)
	{
		this.acceptCallAction = value;
	}

	/**
	 * @return the acceptReturnAction
	 * @property
	 */
	public String get_AcceptReturnAction()
	{
		return acceptReturnAction;
	}

	/**
	 * @param value the acceptReturnAction to set
	 * @property
	 */
	public void set_AcceptReturnAction(String value)
	{
		this.acceptReturnAction = value;
	}

	/**
	 * @return the rejectCallAction
	 * @property
	 */
	public String get_RejectCallAction()
	{
		return rejectCallAction;
	}

	/**
	 * @param value the rejectCallAction to set
	 * @property
	 */
	public void set_RejectCallAction(String value)
	{
		this.rejectCallAction = value;
	}

	/**
	 * @return the rejectReturnAction
	 * @property
	 */
	public String get_RejectReturnAction()
	{
		return rejectReturnAction;
	}

	/**
	 * @param value the rejectReturnAction to set
	 * @property
	 */
	public void set_RejectReturnAction(String value)
	{
		this.rejectReturnAction = value;
	}

}
