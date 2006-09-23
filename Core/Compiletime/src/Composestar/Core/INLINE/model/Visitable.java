/*
 * Created on 21-sep-2006
 *
 */
package Composestar.Core.INLINE.model;

public interface Visitable{
    public Object accept( Visitor visitor );
}
