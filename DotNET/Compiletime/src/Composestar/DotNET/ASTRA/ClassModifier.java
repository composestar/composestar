package Composestar.DotNET.ASTRA;

import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.Utils.Debug;

/**
 * This class does the transformation of an actual class, physically present in
 * an IL file within .class definitions.
 */
public class ClassModifier extends TransformerBase
{
	private Concern concern;

	public ClassModifier(TransformerBase parent, Concern c)
	{
		super(parent);
		concern = c;
	}

	public void run() throws ModifierException
	{
		String line = getLine();
		do
		{
			if (line.trim().startsWith(".method"))
			{
				// split into string until "cil managed"
				String plines = fetchMethodInfo(line);
				write(plines);

				transformSection(false); // don't eat
				/*
				 * // disabled method-removal code. see bug #1441793. String
				 * name = fetchMethodName(plines); if (keepMethod(name)) {
				 * write(plines); transformSection(false); // don't eat } else
				 * transformSection(true); // do eat
				 */
			}
			else if (line.matches("\\s*\\}.*")) // end of class
			{
				// foreach methods marked with new --> print
				Signature sig = concern.getSignature();
				if (sig != null)
				{
					List added = sig.getMethods(MethodWrapper.ADDED);
					Iterator it = added.iterator();
					while (it.hasNext())
					{
						MethodInfo m = (MethodInfo) it.next();
						printMethod(m);
					}
				}
				write(line); // output final bracket
				return;
			}
			else
			{
				write(line);
			}

		} while ((line = getLine()) != null);
	}

	// disabled method-removal code. see bug #1441793.
	// /**
	// * Checks if a method should be kept or if it should be removed.
	// * @return true keep, false remove
	// */
	// private boolean keepMethod(String name) throws ModifierException
	// {
	// if (concern.getSignature().hasMethod(name))
	// {
	// int status = concern.getSignature().getMethodStatus(name);
	// return status != MethodWrapper.REMOVED;
	// }
	// else
	// return true;
	// }

	/**
	 * Parses input lines and transforms this into concrete method information.
	 */
	private String fetchMethodInfo(String line) throws ModifierException
	{
		String plines = line.trim();
		while (!line.endsWith("cil managed"))
		{
			line = getLine().trim();
			plines += " ";
			plines += line;
		}
		return plines;
	}

	private String fetchMethodName(String plines) throws ModifierException
	{
		plines = plines.replaceAll("  ", " ");

		String[] elems = plines.split(" ");
		int pos = 0;
		while (pos < elems.length && !"instance".equals(elems[pos]))
		{
			++pos;
		}

		if (pos >= elems.length - 1)
		{
			return "main";
		}

		if ("class".equals(elems[pos + 1]))
		{
			++pos; // ignore
		}
		// next is returntype
		++pos;
		// next is name
		++pos;
		if (elems[pos].endsWith("()"))
		{ // void
			return elems[pos].replaceFirst("\\(\\)", "");
		}
		else
		{ // params
			return elems[pos].replaceFirst("\\(", "");
		}
	}

	// /**
	// * Matches method info agains the ???
	// */
	// private boolean matchMethod(MethodWrapper methodWrapper, String name,
	// String returnType, String[] params)
	// {
	// // fetch .NET version
	// MethodInfo minfo = methodWrapper.getMethodInfo();
	// if (!minfo.name().equals(name)) return false;
	// if (!minfo.returnType().name().equals(returnType)) return false;
	//
	// // check params
	// List paramList = minfo.getParameters();
	// if (paramList.size() != params.length) return false;
	//		
	// for (int i = 0; i < params.length; ++i)
	// {
	// if (!params[i].equals(((ParameterInfo)
	// paramList.get(i)).parameterType().name()))
	// return false;
	// }
	//		
	// return true; // amazing.. we got through
	// }

	private void printMethod(MethodInfo mi) throws ModifierException
	{
		DotNETMethodInfo dnmi = (DotNETMethodInfo) mi;
		write(".method public hidebysig strict virtual");
		writenn("instance ");

		writenn(((DotNETType) dnmi.returnType()).ilType());
		writenn(" " + mi.name() + "(");

		Iterator it = mi.getParameters().iterator();
		while (it.hasNext())
		{
			ParameterInfo param = (ParameterInfo) it.next();
			Type paramType = param.parameterType();
			if (paramType != null)
			{
				String iltype = ((DotNETType) paramType).ilType();
				writenn(iltype + " " + param.name());

				if (it.hasNext())
				{
					writenn(", ");
				}
			}
			else
			{
				Debug.out(Debug.MODE_WARNING, "ASTRA", "Unresolvable parameter type: " + param.ParameterTypeString);
			}
		}

		write(") cil managed\n"); // TODO: params
		write("{");
		printMethodBody(mi);
		write("}");
	}

	private void printMethodBody(MethodInfo mi) throws ModifierException
	{
	/*
	 * if (mi.returnType().name().equals("Void")) { write(".maxstack 0");
	 * write("nop"); write("ret"); } else if
	 * (mi.returnType().name().equals("Int8")) { write(".maxstack 1");
	 * write(".locals init ([0] int8 CS$00000003$00000000)"); write("ldc.i4.0");
	 * write("stloc.0"); write("br.s"); write("ldloc.0"); write("ret"); } else
	 * if (mi.returnType().name().equals("Int16")) { write(".maxstack 1");
	 * write(".locals init ([0] int16 CS$00000003$00000000)");
	 * write("ldc.i4.0"); write("stloc.0"); write("br.s"); write("ldloc.0");
	 * write("ret"); } else if (mi.returnType().name().equals("Int32")) {
	 * write(".maxstack 1"); write(".locals init ([0] int32
	 * CS$00000003$00000000)"); write("ldc.i4.0"); write("stloc.0");
	 * write("br.s"); write("ldloc.0"); write("ret"); } else if
	 * (mi.returnType().name().equals("Int64")) { write(".maxstack 1");
	 * write(".locals init ([0] int64 CS$00000003$00000000)");
	 * write("ldc.i4.0"); write("conv.i8"); write("stloc.0"); write("br.s");
	 * write("ret"); } else if (mi.returnType().name().equals("Char")) {
	 * write(".maxstack 1"); write(".locals init ([0] char
	 * CS$00000003$00000000)"); write("ldc.i4.0"); write("stloc.0");
	 * write("br.s"); write("ldloc.0"); write("ret"); } else if
	 * (mi.returnType().name().equals("Boolean")) { write(".maxstack 1");
	 * write(".locals init ([0] bool CS$00000003$00000000)"); write("ldc.i4.0");
	 * write("stloc.0"); write("br.s"); write("ldloc.0"); write("ret"); } else //
	 * object { write(".maxstack 1"); write(".locals init ([0] " +
	 * mi.returnType().fullName() + " CS$00000003$00000000)"); write("ldnull");
	 * write("stloc.0"); write("br.s"); write("ldloc.0"); write("ret"); }
	 */
	}
}
