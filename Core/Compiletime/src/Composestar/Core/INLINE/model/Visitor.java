/*
 * Created on 21-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

public interface Visitor{
    public Object visitBlock( Block block );
    public Object visitBranch( Branch branch );
    public Object visitContextInstruction( ContextInstruction contextInstruction );
    public Object visitFilterAction( FilterAction filterAction );
    public Object visitJump( Jump jump );
}
