/*
 * Created on 26-sep-2006
 *
 */
package Composestar.Utils;

import junit.framework.TestCase;

public class LinkedListTest extends TestCase{

    public void testLinkedList(){
        LinkedList list = new LinkedList();
        Integer int1 = new Integer(1);
        Integer int2 = new Integer(2);
        Integer int3 = new Integer(3);
        Integer int4 = new Integer(4);
        
        list.add( int1 );
        list.add( int2 );
        list.add( int3 );
        list.add( int4 );
        
        Object[] objs = list.toArray();
        assertSame( objs[0], int1 );
        assertSame( objs[1], int2 );
        assertSame( objs[2], int3 );
        assertSame( objs[3], int4 );
    }
}
