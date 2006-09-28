package Composestar.DotNET.TYM.SignatureTransformer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

/**
 * Responsible for dissassembling an assembly, parsing the contents of its
 * dissassembled file and delegating the modification to it and finally
 * assemblying the output.
 * 
 * FIXME: something like AssemblyTransformer might be a more appropriate name?
 */
public class ILCodeParser extends TransformerBase
{
	private String assemblyName;
	private Set linkedAssemblies;
	private Map concerns;

	public ILCodeParser()
	{
		super(null, null);
		assemblyName = null;
		linkedAssemblies = new HashSet(); 
		concerns = new HashMap();
	}

	/**
	 * Sets the name of the assembly to transform.
	 */
	public void setAssemblyName(String an)
	{
		assemblyName = an;
	}

	/**
	 * Adds a concern to superimpose.
	 */
	public void addConcern(Concern concern)
	{
		if (concern.getPlatformRepresentation() != null)
		{
			String fullName = ((DotNETType)concern.getPlatformRepresentation()).fullName();
			concerns.put(fullName, new ConcernHolder(concern));
		}

		// check assembly dependencies.
		// we only need to check added methods and what assembly they are from
		Signature signature = concern.getSignature();
		if (signature != null)
		{
			List methods = signature.getMethods(MethodWrapper.ADDED);
			Iterator it = methods.iterator();
	
			while (it.hasNext()) 
			{
				DotNETMethodInfo methodInfo = (DotNETMethodInfo)it.next();
	
				// fetch assembly name
				DotNETType dnt = (DotNETType)methodInfo.parent();
				String assemblyName = dnt.assemblyName();
	
				linkedAssemblies.add(assemblyName);
			}
		}
	}

	/**
	 * Applies the transformations to the assembly.
	 * 
	 * FIXME: 'the transformations'?
	 */
	public void run() throws ModifierException
	{
		if (assemblyName == null || "".equals(assemblyName)) 
			return; // nothing to do
		
		Debug.out(Debug.MODE_DEBUG,"TYM_TRANS","Running TYM Transformer");
		
		// il file before transformation
		String preIlName = FileUtils.replaceExtension(assemblyName, "pre.il");

		// il file after transformation
		String postIlName = FileUtils.replaceExtension(assemblyName, "post.il");

		// disassemble
		Assembler asm = new MSAssembler();
		try {
			asm.disassemble(assemblyName, preIlName);
		}
		catch (AssemblerException e) {
			throw new ModifierException("Unable to disassemble '" + assemblyName + "' into '" + preIlName + "': " + e.getMessage());
		}

		// remove the old .post.il file
		if (! FileUtils.delete(postIlName))
			Debug.out(Debug.MODE_WARNING, "TYM_TRANS", "Unable to delete '" + postIlName + "'");

		// open dissassembled file
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(preIlName)));
			setIn(in);
			openOut(postIlName);

			// run main assembly transformation
			dissect(preIlName);
		}
		catch (FileNotFoundException e) {
			throw new ModifierException("Cannot open newly created il file " + preIlName);
		}
		finally {
			closeOut();
			FileUtils.close(in);
		}

		// remove the old assembly
		if (! FileUtils.delete(assemblyName))
			Debug.out(Debug.MODE_WARNING, "TYM_TRANS", "Unable to delete '" + assemblyName + "'");
		
		// remove the old pdb file
		String pdb = FileUtils.replaceExtension(assemblyName, "pdb");
		if (! FileUtils.delete(pdb))
			Debug.out(Debug.MODE_WARNING, "TYM_TRANS", "Unable to delete '" + pdb + "'");

		// re-assemble
		try {
			asm.assemble(postIlName, assemblyName);
		}
		catch (AssemblerException e) {
			throw new ModifierException(e.getMessage());
		}		
	}

	/**
	 * Opens an assembly and starts transforming it.
	 * @param ilName Name of assembly to transform.
	 */
	private void dissect(String ilName) throws ModifierException
	{
		write("// Generated by Compose* ILCodeParser");
		
		String namespace = "";
		String line;
		while ((line = getLine()) != null)
		{
			String tl = line.trim();
			if (tl.startsWith(".assembly extern"))
			{
				// fetch last part (name).
				String[] elems = line.split(" ");
				String name = elems[elems.length - 1];

				// remove from assembly list
				linkedAssemblies.remove(name);
				write(line);
			}
			else if (tl.startsWith(".assembly"))
			{
				// output missing assembly references
				Iterator it = linkedAssemblies.iterator();
				while (it.hasNext())
				{
					String asm = (String)it.next();
					Debug.out(Debug.MODE_DEBUG,"TYM_TRANS","Adding reference to assembly" + asm);
					
					write(" .assembly extern " + asm + '\n');
					write("{\n\t.ver 0:0:0:0\n}\n");
				}
				write(line);
			}
			else if (tl.startsWith(".namespace"))
			{
				String[] elems = tl.split(" ");
				namespace = elems[elems.length - 1];
				write(line);
			}
			else if (tl.startsWith(".class"))
			{
				// fetch last part (name).
				String[] elems = line.split(" ");
				String name = elems[elems.length - 1];
				
				ConcernHolder ch = (ConcernHolder)concerns.get(name);				
				if (ch == null) // try FQN
					ch = (ConcernHolder)concerns.get(namespace + '.' + name);
				
				if (ch == null)
				{
					// if not present output complete class.
					write(line);
					printLine();
					transformSection(false); // don't eat
				}
				else
				{
					// if not declared mark as declared else mark as current
					if (!ch.isDeclared())
					{
						ch.setDeclared(true);

						// output to }
						write(line);
						printLine();
						transformSection(false); // don't eat
					}
					else
					{
						ch.setDeclared(false);

						// print complete declaration
						write(line);
						printLine();
						
						// new handler
						ClassModifier cm = new ClassModifier(this, ch.getConcern());
						cm.run();
					}
				}
			}
			else // unkown, better echo
				write(line);
		}     
	}

	private static class ConcernHolder
	{
		private boolean declared = false;
		private Concern concern;

		public ConcernHolder(Concern conc)
		{
			setConcern(conc);
		}

		public boolean isDeclared()
		{
			return declared;
		}

		public void setDeclared(boolean d)
		{
			declared = d;
		}

		public Concern getConcern()
		{
			return concern;
		}

		public void setConcern(Concern c)
		{
			concern = c;
		}
	}
}
