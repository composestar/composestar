/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;


public class FilterBlock extends Block{
    
    private String type;

    public FilterBlock( String type ){
	this.type = type;
    }
    

    /**
     * @return the type
     */
    public String getType(){
	return type;
    }
}
