package Composestar.DotNET.PACMAN;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.Utils.FileUtils;

/**
 * @author Marcus Klimstra
 */
class PartialClassEmitter
{
	private final File m_target;

	private final Map m_namespaces; // <String,Namespace>

	private final Map m_types; // <DotNETType,ExpandedType>

	private PrintWriter m_writer;

	public PartialClassEmitter(File target)
	{
		m_target = target;
		m_namespaces = new HashMap();
		m_types = new HashMap();
	}

	private Namespace getNamespace(String name)
	{
		Namespace ns = (Namespace) m_namespaces.get(name);
		if (ns == null)
		{
			m_namespaces.put(name, ns = new Namespace(name));
		}

		return ns;
	}

	private ExpandedType getExpandedType(String tn)
	{
		ExpandedType et = (ExpandedType) m_types.get(tn);
		if (et == null)
		{
			m_types.put(tn, et = new ExpandedType(tn));
		}

		return et;
	}

	public void addMethod(DotNETType dnt, DotNETMethodInfo mi)
	{
		String ns = dnt.namespace();
		String tn = dnt.name();

		ExpandedType et = getExpandedType(tn);
		getNamespace(ns).addType(et);

		et.addMethod(mi);
	}

	public void emit() throws IOException
	{
		if (m_types.isEmpty())
		{
			return;
		}

		try
		{
			m_writer = new PrintWriter(new BufferedWriter(new FileWriter(m_target)));

			emitHeader();

			Iterator nsIt = m_namespaces.values().iterator();
			while (nsIt.hasNext())
			{
				Namespace ns = (Namespace) nsIt.next();
				emitNamespace(ns);
			}
		}
		finally
		{
			FileUtils.close(m_writer);
			m_writer = null;
		}
	}

	private void emitHeader()
	{
		m_writer.println("using System;");
		m_writer.println();
	}

	private void emitNamespace(Namespace ns)
	{
		openNamespace(ns.getName());
		emitTypes(ns.types());
		closeNamespace();
	}

	private void openNamespace(String name)
	{
		m_writer.println("namespace " + name);
		m_writer.println("{");
	}

	private void closeNamespace()
	{
		m_writer.println("}");
	}

	private void emitTypes(Iterator typeIt)
	{
		while (typeIt.hasNext())
		{
			ExpandedType et = (ExpandedType) typeIt.next();
			emitType(et);
		}
	}

	private void emitType(ExpandedType et)
	{
		openType(et.getName());
		emitMethods(et.methods());
		closeType();
	}

	private void openType(String name)
	{
		m_writer.println("\tpublic partial class " + name);
		m_writer.println("\t{");
	}

	private void closeType()
	{
		m_writer.println("\t}");
	}

	private void emitMethods(Iterator methIt)
	{
		while (methIt.hasNext())
		{
			DotNETMethodInfo mi = (DotNETMethodInfo) methIt.next();
			emitMethod(mi);
		}
	}

	private void emitMethod(DotNETMethodInfo mi)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\t\t");
		sb.append(mi.toString());
		sb.append(" { throw new InvalidOperationException(); }");

		m_writer.println(sb.toString());
	}
}
