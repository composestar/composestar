package Composestar.RuntimeCore.Utils;

import java.util.ArrayList;

public class List {
    private ArrayList inner;
    
    /**
     * @roseuid 404C4B600189
     */
    public List() {
		inner = new ArrayList();     
    }
    
    /**
     * @param o
     * @roseuid 40DE01EF0089
     */
    public void add(Object o) {
		inner.add(o);     
    }
    
    /**
     * @param list
     * @roseuid 40DE0202020D
     */
    public void addAll(List list) {
		for(int i=0; i<list.size(); i++)
		{
			inner.add(list.elementAt(i));
		}     
    }
    
    /**
     * @param obj
     * @roseuid 40DE02110100
     */
    public void addFirst(Object obj) {
		inner.add(0,obj);     
    }
    
    /**
     * @param obj
     * @return int
     * @roseuid 40DE0229029F
     */
    public int indexOf(Object obj) {
		return inner.indexOf(obj);     
    }
    
    /**
     * @param obj
     * @roseuid 40DE023D0063
     */
    public void remove(Object obj) {
		inner.remove(this.indexOf(obj));     
    }
    
    /**
     * @param i
     * @return java.lang.Object
     * @roseuid 40DE0250001A
     */
    public Object elementAt(int i) {
			return inner.get(i);     
    }
    
    /**
     * @return int
     * @roseuid 40DE02660225
     */
    public int size() {
		return inner.size();     
    }
    
    /**
     * @param obj
     * @return boolean
     * @roseuid 40DE026D0149
     */
    public boolean contains(Object obj) {
		return inner.contains(obj);     
    }
}
