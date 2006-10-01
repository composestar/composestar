/*
 * Created on 26-sep-2006
 *
 */
package Composestar.Repository.LanguageModel;

/**
 * Platform independant linked list.
 * 
 * 
 * @author Arjan de Roo
 */
public class LinkedList{
	private LinkedListEntry header;
    private int count;
    
    public LinkedList(){
		header = new LinkedListEntry(null, null, null);
        header.next = header;
        header.previous = header;
    }
    
    public void add( Object obj ){
        addAfter( obj, header.previous );
    }
    
    public Object[] toArray(){
        Object[] objects = new Object[count];
		LinkedListEntry entry = header.next;
        for (int i=0; i<count; i++){
            objects[i] = entry.obj;
            entry = entry.next;
        }
        
        return objects;
    }


	private void addAfter(Object obj, LinkedListEntry entry)
	{
		LinkedListEntry newEntry = new LinkedListEntry(obj, entry, entry.next);
        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        count++;
    }
       
}
