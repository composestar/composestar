package Composestar.DotNET2.TYPEX;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Utils.StringUtils;

import composestar.dotNET2.tym.entities.MethodElement;
import composestar.dotNET2.tym.entities.ParameterElement;

class JSharpMethodEmitter implements MethodEmitter
{
	private Map<String, String> primitives;

	public JSharpMethodEmitter()
	{
		primitives = new HashMap<String, String>();
		primitives.put("System.Void", "void");
		primitives.put("System.Boolean", "boolean");
		primitives.put("System.SByte", "byte");
		primitives.put("System.Byte", "ubyte");
		primitives.put("System.Char", "char");
		primitives.put("System.Int16", "short");
		primitives.put("System.Int32", "int");
		primitives.put("System.Int64", "long");
		primitives.put("System.Single", "float");
		primitives.put("System.Double", "double");
	}

	public void emit(MethodElement me, BufferedWriter bw) throws IOException
	{
		bw.newLine();
		bw.append("\t");
		bw.append("public ");
		bw.append(deconvert(me.getReturnType()));
		bw.append(" ");
		bw.append(me.getName());
		bw.append("(");

		List<ParameterElement> paramList = me.getParameters().getParameterList();
		bw.append(StringUtils.join(getParameters(paramList), ", "));

		bw.append(")");
		bw.append(" { throw new System.NotImplementedException(); }");
		bw.newLine();
	}

	private List<String> getParameters(List<ParameterElement> params)
	{
		List<String> result = new ArrayList<String>();
		for (ParameterElement pe : params)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(deconvert(pe.getType()));
			sb.append(" ");
			sb.append(pe.getName());
			result.add(sb.toString());
		}
		return result;
	}

	/**
	 * Converts some .NET types back to their J# counterpart, specificly
	 * primitives.
	 */
	private String deconvert(String type)
	{
		String prim = primitives.get(type);
		if (prim != null)
		{
			return prim;
		}

		// TODO: System.String <-> java.lang.String?

		return type;
	}
}
