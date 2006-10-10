/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.FIRE2.model.Message;

public class FilterAction extends Instruction{
    private String type;
    private Message message;
    private Message substitutedMessage;

    public FilterAction( String type, Message message, Message substitutedMessage )
    {
        this.type = type;
        this.message = message;
        this.substitutedMessage = substitutedMessage;
    }

    /**
     * @return the message
     */
    public Message getMessage(){
        return message;
    }
    

	/**
	 * @return the substitutedMessage
	 */
	public Message getSubstitutedMessage()
	{
		return substitutedMessage;
	}

	/**
     * @return the type
     */
    public String getType(){
        return type;
    }
    
    
	/**
     * @see Composestar.Core.INLINE.model.Visitable#accept(Composestar.Core.INLINE.model.Visitor)
     */
    public Object accept(Visitor visitor){
        return visitor.visitFilterAction( this );
    }

    
}
