/*
 * Created on 12-sep-2006
 *
 */
package Composestar.Core.INLINE.model;


public class Jump extends Instruction{
    private Label target;

    public Jump( Label target ){
        this.target = target;
    }

    /**
     * @return the target
     */
    public Label getTarget(){
        return target;
    }
}
