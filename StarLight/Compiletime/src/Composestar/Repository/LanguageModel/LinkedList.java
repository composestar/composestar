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
    private Entry header;
    private int count;
    
    public LinkedList(){
        header = new Entry( null, null, null );
        header.next = header;
        header.previous = header;
    }
    
    public void add( Object obj ){
        addAfter( obj, header.previous );
    }
    
    public Object[] toArray(){
        Object[] objects = new Object[count];
        Entry entry = header.next;
        for (int i=0; i<count; i++){
            objects[i] = entry.obj;
            entry = entry.next;
        }
        
        return objects;
    }
    
    
    private void addAfter( Object obj, Entry entry ){
        Entry newEntry = new Entry( obj, entry, entry.next );
        newEntry.previous.next = newEntry;
        newEntry.next.previous = newEntry;
        count++;
    }
    
    public class Entry{
        Object obj;
        Entry previous;
        Entry next;

		public Entry() { } 
        
        public Entry( Object obj, Entry previous, Entry next ){
            this.obj = obj;
            this.previous = previous;
            this.next = next;
        }
    }
}
