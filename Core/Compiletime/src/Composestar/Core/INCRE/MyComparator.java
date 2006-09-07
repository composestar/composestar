package Composestar.Core.INCRE;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Stack;

import Composestar.Utils.*;
import Composestar.Core.Exception.ModuleException;

public class MyComparator {

    public static HashMap myFields = new HashMap();
    public static HashMap comparisons = new HashMap();
    private String module;

    public static int compare = 0;

    public MyComparator(String module) {
        this.module = module;
    }

    public int getCompare() {
        return compare;
    }

    public void clearComparisons() {
        comparisons.clear();
    }

    public int duplicates = 0;

    /*
     * Compares two objects
     * returns true if equals and false otherwise
     */
    public boolean compare(Object a, Object b) throws ModuleException {
        // Keep track of the number of comparisons made
        compare++;
        
        // special cases: one or both objects are null
        if (a == null && b == null) {          
        	return true;
        }
        else if (a == null || b == null){
           	return false;
        }
        
        // first check the types of both objects
        if (a.getClass().getName().equals(b.getClass().getName())) {
            if (a.getClass().equals(String.class)) {
                // easy case
                return a.equals(b);
            } else if (a.getClass().equals(Integer.class)) {
                // easy case
                return a.equals(b);
            } else if (a.getClass().equals(Boolean.class)) {
                // easy case
                return a.equals(b);
            } else if (a instanceof AbstractList) {
                // compare abstract list
                return compareAbstractLists((AbstractList) a, (AbstractList) b);
            } else if (a instanceof HashSet) {
                // compare HashSets
                return compareAbstractSets((HashSet) a, (HashSet) b);
            } else {
                if (hasComparableObjects(a)) {
                    // compare all INCRE fields
                    if (!compareINCREfields(a, b))
                        return false;
                } else {
                    //iterate over all public fields
                    Enumeration enumFields = getFields(a.getClass()).elements();
                    while (enumFields.hasMoreElements()) {
                        Field field = (Field) enumFields.nextElement();

                        try {
                            // only public fields are compared
                            if (!compare(field.get(a), field.get(b)))
                                return false;
                        } catch (Exception excep) {
                            throw new ModuleException("INCRE::MyComparator error: " + excep.getMessage());
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    /**
     * @param AbstractList a1
     * @param AbstractList a2
     * @return
     */
    public boolean compareAbstractLists(AbstractList a1, AbstractList a2) throws ModuleException {
        if (a1.size() != a2.size()) // compare sizes first
            return false;
        else {// compare all objects in the ArrayList
            for (int i = 0; i < a1.size(); i++) {
                Object obj1 = (Object) a1.get(i);
                Object obj2 = (Object) a2.get(i);
                if (!compare(obj1, obj2)) {
                    // no match, return false
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @param HashSet s1
     * @param HashSet s2
     * @return
     */
    public boolean compareAbstractSets(AbstractSet s1, AbstractSet s2) throws ModuleException {
        if (s1.size() != s2.size()) // compare sizes first
            return false;
        else {// compare all objects in the HashSet

            Iterator iter1 = s1.iterator();
            Iterator iter2 = s2.iterator();
            while (iter1.hasNext()) {
                Object obj1 = (Object) iter1.next();
                Object obj2 = (Object) iter2.next();
                if (!compare(obj1, obj2)) {
                    // no match, return false
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * @param Class
     * @return
     */
    public Vector getFields(Class c) {

        if (myFields.containsKey(c))
            return (Vector) myFields.get(c);

        Vector fields = new Vector();
        Stack stack = new Stack();
        Class myClass = c;
        //while( !myClass.equals(Object.class) )
        //{
        stack.push(myClass);
        //	myClass = myClass.getSuperclass();
        //}

        while (!stack.empty()) {
            myClass = (Class) stack.pop();
            Field[] declaredFields = myClass.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                // can only check public fields
                if (Modifier.isPublic(declaredFields[i].getModifiers())) {
                    Field f = (Field) declaredFields[i];
                    // IMPORTANT: skip repositoryKey due to different hashcodes
                    if (!f.getName().equals("repositoryKey"))
                        fields.add(f);
                }
            }
        }

        myFields.put(c, fields);
        return fields;
    }


    /**
     * Adds the result of a comparison to the map
     *
     * @param id     The id of the object compared
     * @param result The result of the comparison
     * @return
     */
    public void addComparison(String id, boolean result) {
    	if (id != null) {
    		Boolean b = Boolean.valueOf(result);
            comparisons.put(id, b);
        } else {
            Debug.out(Debug.MODE_DEBUG, "INCRE::Comparator", "Key of comparison is null!");
        }
    }

    /**
     * Check whether an object has been compared before
     *
     * @param id The id of the object
     * @return true if object is in map, false if not
     */
    public boolean comparisonMade(String id) {
    	return (comparisons.containsKey(id));
    }

    /*
     * Get the result of an earlier comparison
     * @param id The id of the object compared
     * @return boolean The result of the comparison
     */
    public boolean getComparison(String id) {
        return ((Boolean) comparisons.get(id)).booleanValue();
    }

    public boolean hasComparableObjects(Object obj) {
        String fullname = obj.getClass().getName();
        INCRE incre = INCRE.instance();
        Module m = incre.getConfigManager().getModuleByID(this.module);

        return m.hasComparableObjects(fullname);
    }

    /**
     * Compares all 'INCRE fields' of two objects
     * The fields are acquired from the Module and were extracted from the incre configuration file
     */
    public boolean compareINCREfields(Object a, Object b) throws ModuleException {
        try {
        	INCRE incre = INCRE.instance();
            String fullname = a.getClass().getName();
            Module m = incre.getConfigManager().getModuleByID(this.module);
            ArrayList compObjects = m.getComparableObjects(fullname);
            boolean equal = false;
            String key = null;

            Iterator itrObjects = compObjects.iterator();
            while (itrObjects.hasNext()) {
                Object obj = (Object) itrObjects.next();
                Object fielda = null;
                Object fieldb = null;

                if (obj instanceof FieldNode) {
                    FieldNode fieldnode = (FieldNode) obj;
                    key = fieldnode.getUniqueID(a)+b.hashCode();
                    fielda = fieldnode.visit(a);
                    fieldb = fieldnode.visit(b);
                } else if (obj instanceof MethodNode) {
                    MethodNode methodnode = (MethodNode) obj;
                    key = methodnode.getUniqueID(a)+b.hashCode();
                    fielda = methodnode.visit(a);
                    fieldb = methodnode.visit(b);
                } else if (obj instanceof Path) {
                    Path path = (Path) obj;
                    fielda = path.follow(a);
                    fieldb = path.follow(b);
                }

                if (key != null && comparisonMade(key)) {
                    // already made comparison before
                	duplicates++;
                    equal = getComparison(key);
                } else {
                    if (key != null)// temporarily true to avoid infinite loops
                        addComparison(key, true);

                    equal = compare(fielda, fieldb);

                    if (key != null)// store result of comparison
                        addComparison(key, equal);
                }

                if (!equal) // stop comparison by returning false
                    return false;
            }
        } catch (Exception e) {
        	throw new ModuleException("INCRE::MyComparator error: " + e.toString());
        }

        return true;
    }
}