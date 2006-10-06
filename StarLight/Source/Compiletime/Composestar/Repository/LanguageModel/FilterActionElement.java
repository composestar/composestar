/*
 * Created on 4-okt-2006
 *
 */
package Composestar.Repository.LanguageModel;

public class FilterActionElement
{
	public final static int FLOW_CONTINUE = 1;

	public final static int FLOW_EXIT = 2;

	public final static int FLOW_RETURN = 3;

	public final static int MESSAGE_ORIGINAL = 1;

	public final static int MESSAGE_SUBSTITUTED = 2;

	public final static int MESSAGE_ANY = 3;

	private int flowBehaviour;

	private int messageChangeBehaviour;

	private String name;

	private String fullName;

	/**
	 * @return the flowBehaviour
	 * @property
	 */
	public int get_FlowBehaviour()
	{
		return flowBehaviour;
	}

	/**
	 * @param value the flowBehaviour to set
	 * @property
	 */
	public void set_FlowBehaviour(int value)
	{
		this.flowBehaviour = value;
	}

	/**
	 * @return the messageChangeBehaviour
	 * @property
	 */
	public int get_MessageChangeBehaviour()
	{
		return messageChangeBehaviour;
	}

	/**
	 * @param value the messageChangeBehaviour to set
	 * @property
	 */
	public void set_MessageChangeBehaviour(int value)
	{
		this.messageChangeBehaviour = value;
	}

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
	 * @return the fullName
	 * @property
	 */
	public String get_FullName()
	{
		return fullName;
	}

	/**
	 * @param value the fullName to set
	 * @property
	 */
	public void set_FullName(String value)
	{
		this.fullName = value;
	}
	
}
