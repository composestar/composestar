package Composestar.RuntimeJava.Utils;

import java.lang.reflect.Field;
import java.util.*;

import Composestar.Core.CpsProgramRepository.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;
import Composestar.Core.RepositoryImplementation.*;
import Composestar.RuntimeCore.Utils.Debug;

public class RepositoryFixer
{
	public static void fixRepository(DataStore ds)
	{
		RepositoryFixer rf = new RepositoryFixer();
		try
		{
			rf.fixClones(ds);
			rf.fixReferences(ds);
			rf.fixParentLinks(ds);
		}
		catch (java.lang.Exception e)
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", "Could not fix repository.");
			e.printStackTrace();
		}
	}

	private void fixClones(DataStore ds)
	{
		Collection values = ds.map.values();

		try
		{
			Iterator it = values.iterator();
			while (it.hasNext())
			{
				Object obj = it.next();
				if (obj instanceof DeclaredRepositoryEntity)
				{
					if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG, "FLIRT", "Fixing clone "
							+ ((DeclaredRepositoryEntity) obj).repositoryKey + ".");
					fixChildren(obj, ds);
				}
			}
		}
		catch (java.lang.Exception ex)
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", "An exception occurred during fixing of clones.");
			ex.printStackTrace();
		}
	}

	private void fixChildren(Object o, DataStore ds) throws java.lang.IllegalAccessException
	{
		Field[] fields = o.getClass().getFields();
		Object child;

        for (Field field : fields) {
            child = field.get(o);
            if (child == null) {
            } else if (child instanceof RepositoryEntity) {
                Debug.out(Debug.MODE_DEBUG, "FLIRT", "Fixing '" + field.getName() + "' of type '"
                        + child.getClass().getName() + "'.");

                Object temp = fixEntity((RepositoryEntity) child, ds);
                field.set(o, temp);
            } else if (child instanceof Vector) {
                Debug.out(Debug.MODE_DEBUG, "FLIRT", "Fixing '" + field.getName() + "' of type '"
                        + child.getClass().getName() + "'.");
                field.set(o, fixVector((Vector) child, ds));
            } else if (child instanceof DataMap) {
                Debug.out(Debug.MODE_DEBUG, "FLIRT", "Fixing '" + field.getName() + "' of type '"
                        + child.getClass().getName() + "'.");
                // fields[i].set(o, fixMap((Map) child, ds) );
            } else {
                Debug.out(Debug.MODE_DEBUG, "FLIRT", "Field '" + field.getName() + "' has unknown type '"
                        + child.getClass().getName() + "'.");
            }

        }
    }

	public Object fixEntity(RepositoryEntity o, DataStore ds)
	{
		String repositoryKey = ((RepositoryEntity) o).repositoryKey;
		Object reffedObject = ds.getObjectByID(repositoryKey);

		if (reffedObject == null)
		{
			if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_ERROR, "FLIRT",
					"Fatal Error: Unable to resolve object with key '" + repositoryKey + "'.");
			System.exit(0);
		}

		return reffedObject;
	}

	public Object fixVector(Vector v, DataStore ds)
	{
		Vector returnVector = new Vector();

		for (Enumeration e = v.elements(); e.hasMoreElements();)
		{
			Object o = e.nextElement();
			if (o instanceof DeclaredRepositoryEntity)
			{
				returnVector.addElement(fixEntity((DeclaredRepositoryEntity) o, ds));
			}
			else returnVector.addElement(o);
		}
		return returnVector;
	}

	public void fixReferences(DataStore ds) throws java.lang.Exception
	{
		Iterator it = ds.getAllInstancesOf(Reference.class);
		Reference ref;
		while (it.hasNext())
		{
			ref = (Reference) it.next();
			if (ref.getResolved())
			{
				String fqn = ref.getQualifiedName();

				Object referenced;

				if (ref instanceof ConcernReference)
				{
					referenced = ds.getObjectByID(fqn);
					if (referenced == null)
					{
						if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT",
								"Unable to resolve concern '" + fqn + "' with key '" + ref.repositoryKey + "'.");
					}
					else ((ConcernReference) ref).setRef((Concern) referenced);
				}
				else if (ref instanceof FilterModuleReference)
				{
					referenced = (((FilterModuleReference) ref).getRef());

					if (referenced == null)
					{
						if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT",
								"Unable to resolve filtermodule '" + fqn + "' with key '" + ref.repositoryKey + "'.");
					}
				}
				else if (ref instanceof DeclaredObjectReference)
				{
					fqn = fqn.replaceAll("::", ".");
					fqn = fqn.replaceAll(":", ".");

					referenced = ds.getObjectByID(fqn);
					if (referenced == null)
					{
						if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Unable to resolve object '"
								+ fqn + "'.");
					}
					else ((DeclaredObjectReference) ref).setRef((TypedDeclaration) referenced);
				}
				else if (ref instanceof ConditionReference)
				{
					fqn = fqn.replaceAll("::", ".");
					fqn = fqn.replaceAll(":", ".");

					referenced = ds.getObjectByID(fqn);
					if (referenced == null)
					{
						if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT",
								"Unable to resolve condition '" + fqn + "'.");
					}
					else ((ConditionReference) ref).setRef((Condition) referenced);
				}
				else if (ref instanceof SelectorReference)
				{
					fqn = ((SelectorReference) ref).getConcern() + ".superimposition."
							+ ((SelectorReference) ref).getName();
					((SelectorReference) ref).setRef((SelectorDefinition) ds.getObjectByID(fqn));
				}
				else
				{
					if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Unable to resolve object '"
							+ fqn + "' from class '" + ref.getClass().getName() + "'.");
				}
			}
		}
	}

	public void fixParentLinks(DataStore ds) throws java.lang.Exception
	{
		Iterator it = ds.getAllInstancesOf(DeclaredRepositoryEntity.class);
		DeclaredRepositoryEntity current, parent;
		while (it.hasNext())
		{
			current = (DeclaredRepositoryEntity) it.next();
			// skipping PrimitiveConcerns
			if (current instanceof PrimitiveConcern) continue;

			String fqn = current.repositoryKey;
			// skipping entities with no dots in the fqn
			if (fqn.lastIndexOf(".") == -1) continue;

			String pfqn = fqn.substring(0, fqn.lastIndexOf("."));
			if (fqn.compareTo(pfqn) != 0)
			{
				parent = (DeclaredRepositoryEntity) ds.getObjectByID(pfqn);
				if (parent != null)
				{
					current.setParent(parent);
					if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG, "FLIRT", "Parent of '" + fqn + "' set to '"
							+ pfqn + "'.");
				}
			}
		}
	}
}
