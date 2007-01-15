package Composestar.C.wrapper.parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.C.LAMA.CAnnotation;
import Composestar.C.LAMA.CFile;
import Composestar.C.LAMA.CParameterInfo;
import Composestar.C.LAMA.CVariable;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

public class Annotation
{
	private ArrayList values = new ArrayList();

	private String type = "";

	private String target = ""; // type, method, field -> the original program
								// element names

	private String filename = "";

	private String targetName = "";

	/** when we want to retrieve a parameter also the parent function is needed* */
	private String functionName = "";

	public Annotation()
	{}

	public void addValue(TNode valueNode)
	{
		if (valueNode.getType() == GnuCTokenTypes.StringLiteral)
		{
			this.values.add(valueNode.getText());
		}
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void retrieveTarget(TNode annotationNode)
	{
		TNode idname = null;
		if (annotationNode.firstSiblingOfType(GnuCTokenTypes.NParameterDeclaration) != null)
		{
			target = "parameter";
			idname = annotationNode.firstSiblingOfType(GnuCTokenTypes.NParameterDeclaration);
			idname = idname.firstChildOfType(GnuCTokenTypes.NDeclarator);
			targetName = (idname.firstChildOfType(GnuCTokenTypes.ID)).getText();
		}
		else if (annotationNode.firstSiblingOfType(GnuCTokenTypes.NFunctionDef) != null)
		{
			target = "method";
			idname = annotationNode.firstSiblingOfType(GnuCTokenTypes.NFunctionDef);
			idname = idname.firstChildOfType(GnuCTokenTypes.NDeclarator);
			targetName = (idname.firstChildOfType(GnuCTokenTypes.ID)).getText();
		}
		else if (annotationNode.firstSiblingOfType(GnuCTokenTypes.NInitDecl) != null)
		{
			target = "field";
			idname = annotationNode.firstSiblingOfType(GnuCTokenTypes.NInitDecl);
			idname = idname.firstChildOfType(GnuCTokenTypes.NDeclarator);
			targetName = (idname.firstChildOfType(GnuCTokenTypes.ID)).getText();
		}
		// System.out.println("Target of annotation is:"+target
		// );//.getParent().getText());
	}

	public void printAnnotation()
	{
		Debug.out(Debug.MODE_INFORMATION, "ANNOTATION", "Annotation:" + type + "\n" + "Value first element:"
				+ values.get(0) + "\n" + "Target:" + target + "\n");
	}

	public String getType()
	{
		return type;
	}

	public String getTarget()
	{
		return target;
	}

	public String getTargetName()
	{
		return targetName;
	}

	public ArrayList getValues()
	{
		return values;
	}

	public void setFileName(String filename)
	{
		this.filename = filename;
	}

	public String getFileName()
	{
		return filename;
	}

	public void setFunctionName(String functionName)
	{
		this.functionName = functionName;
	}

	public void addAnnotationtoLAMA()
	{
		Composestar.Core.LAMA.Annotation annot = new CAnnotation();
		Concern c = null;
		if (type.equals("file"))
		{
			// Debug.out(Debug.MODE_ERROR, "Annotation Extractor", "Annotations
			// type "+ filename +" is not declared");
			c = (Concern) DataStore.instance().getObjectByID(filename);
		}
		else
		{
			c = (Concern) DataStore.instance().getObjectByID(type);
		}
		if (c == null)
		{
			Debug.out(Debug.MODE_ERROR, "Annotation Extractor", "Annotations type " + type + " is not declared");
		}
		else
		{
			Type annotType = (Type) c.getPlatformRepresentation();
			if (type.equals("file"))
			{
				annot.register(annotType, getTypeLocation(targetName));
			}
			else if ("parameter".equals(target))
			{
				annot.register(annotType, getParameterLocation(targetName));
			}
			else if ("method".equals(target))
			{
				annot.register(annotType, getMethodLocation(targetName));
			}
			else if ("field".equals(target))
			{
				annot.register(annotType, getFieldLocation(targetName));
			}
			annot.setValue((String) values.get(0));
		}
		printAnnotation();
	}

	public Type getTypeLocation(String location)
	{
		Concern c = (Concern) DataStore.instance().getObjectByID(location);
		if (c != null && c.getPlatformRepresentation() instanceof Type)
		{
			return (Type) c.getPlatformRepresentation();
		}
		return null;
	}

	public Type getFileLocation(String location)
	{
		Concern c = (Concern) DataStore.instance().getObjectByID(location);
		if (c != null && c.getPlatformRepresentation() instanceof CFile)
		{
			return (CFile) c.getPlatformRepresentation();
		}
		return null;
	}

	public MethodInfo getMethodLocation(String location)
	{
		String methodName = location;
		String typeName = getFileName();

		Type type = getTypeLocation(typeName);

		if (type != null)
		{
			List methods = type.getMethods();
			Iterator i = methods.iterator();
            for (Object method1 : methods) {
                MethodInfo method = (MethodInfo) method1;
                if (method.Name.equals(methodName)) {
                    return method;
                }
            }
        }
		return null;
	}

	public FieldInfo getFieldLocation(String location)
	{

		String fieldName = location;
		String typeName = getFileName();
		Type type = getTypeLocation(typeName);

		if (type != null)
		{
			List fields = type.getFields();
			Iterator i = fields.iterator();
            for (Object field1 : fields) {
                CVariable field = (CVariable) field1;
                if (field.Name.equals(fieldName)) {
                    return field;
                }
            }
        }
		return null;
	}

	public ParameterInfo getParameterLocation(String paramName)
	{
		MethodInfo method = getMethodLocation(functionName);
		Iterator paramIter = method.getParameters().iterator();
        for (Object o : method.getParameters()) {
            CParameterInfo param = (CParameterInfo) o;
            if (param.Name.equals(paramName)) {
                return param;
            }
        }
        return null;
	}

}
