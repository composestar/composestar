/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

import Composestar.Core.FIRE2.model.Message;

public class FilterAction extends Instruction{
    private String type;
    private Message message;

    public FilterAction( String type, Message message ){
	this.type = type;
	this.message = message;
    }

    /**
     * @return the message
     */
    public Message getMessage(){
	return message;
    }

    /**
     * @return the type
     */
    public String getType(){
	return type;
    }

}
