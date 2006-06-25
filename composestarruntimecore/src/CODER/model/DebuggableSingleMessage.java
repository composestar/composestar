package Composestar.RuntimeCore.CODER.Model;

/**
 * Summary description for DebuggableSingleMessage.
 */
public interface DebuggableSingleMessage extends DebuggableMessage 
{
	public Object getTarget();
	public String getSelector();
}
