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
public class LinkedListEntry
{

	Object obj;
	LinkedListEntry previous;
	LinkedListEntry next;

	public LinkedListEntry() { }

	public LinkedListEntry(Object obj, LinkedListEntry previous, LinkedListEntry next)
	{
		this.obj = obj;
		this.previous = previous;
		this.next = next;
	}
}

