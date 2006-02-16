//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\CTAdaption\\TYM\\AssemblyTransformer\\DummyUnlinker.java

/*
 * Created on 19-jul-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.DotNET.TYM.SignatureTransformer;

import java.io.*;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.TYM.TypeLocations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;

/**
 * @author Staijen
 * This class will modify references to the dummies dll so that the types from the 
 * 'real' dlls are used.
 */
public class DummyUnlinker implements CTCommonModule {

	private TypeLocations typeLocations;
    
    public DummyUnlinker()
    {
    	typeLocations = TypeLocations.instance();
    }
    
    /**
     * (non-Javadoc)
     * @see 
     * Composestar.core.Master.coreModule#run(Composestar.core.Master.Commo
     * nResources)
     * @param resources
     * @throws Composestar.core.Exception.ModuleException
     * @roseuid 40FD27B20117
     */
    public void run(CommonResources resources) throws ModuleException {
		ArrayList assemblies = (ArrayList) resources.getResource("BuiltAssemblies");
		HashSet assemblyNames = typeLocations.assemblies();
		MSAssembler assembler = new MSAssembler();
		String[] types = reverseBubbleSort(typeLocations.types());
		
		/*
		for( Iterator i = assemblies.iterator(); i.hasNext(); )
		{
			String assemblyFilename = (String) i.next();
			String assemblyName = assemblyFilename.substring(assemblyFilename.lastIndexOf("/")+1);
			assemblyName = assemblyName.substring(0, assemblyName.lastIndexOf(".")); 
			assemblyNames.add(assemblyName);
		}
		*/
        Iterator i = assemblies.iterator();
		while(i.hasNext())
		{
			String asmpath = ((String) i.next()).replaceAll("\"", "");
			String ilpath = asmpath + ".il";
			
			String asmname = asmpath.substring(asmpath.lastIndexOf("/")+1).replaceAll("\"", "");
			
			try
			{
				assembler.disassemble(asmpath, ilpath);
				String[] ilCode = readLines(ilpath);
				HashSet asmExterns = (HashSet) assemblyNames.clone();
				asmExterns.remove(asmname);
				String newCode = unlinkDummies(ilCode, asmname, (String[])asmExterns.toArray(new String[asmExterns.size()]), types);
				writeFile(ilpath, newCode);
				assembler.assemble(ilpath, asmpath);
			}
			catch(Exception e)
			{
				throw new ModuleException("Dummy Unlinking failure in " + asmpath + ", reason: " + e.getMessage());
			}
		}     
    }
    
    /**
     * @param names
     * @return java.lang.String[]
     * @roseuid 40FD27B20194
     */
    public String[] reverseBubbleSort(String[] names) {
		for (int i = names.length -1; i >= 0; i--)
		{
			for (int j = 0; j < i; j++)
			{
				if (names[j].compareTo(names[j+1]) < 0 )
				{
			  		String temp = names[j];
			  		names[j] = names[j + 1];
			  		names[j + 1] = temp;
				}
			}
		}
		
		return names;     
    }
    
    /**
     * @param ilCode
     * @param selfName
     * @param externSet
     * @return java.lang.String
     * @roseuid 40FD27B201A3
     */
    public String unlinkDummies(String[] ilCode, String selfName, String[] externSet, String[] types) {
		String returnCode = "";
		String line;
		for( int i = 0; i < ilCode.length; i++ )
		{
			line = ilCode[i];
			if( line.trim().startsWith( ".assembly extern dummies" ) ) {
				returnCode += createExternDeclarations(externSet);
				i += 3; // TODO: use dropSection
			}
			else {
				returnCode += line + "\r\n";
			}
		}
		
		if ( returnCode.indexOf( "[dummies]" ) >= 0 ) 
		{
			for( int i = 0; i < types.length; i++ )
			{
				String type = types[i];
				String assembly = typeLocations.getAssemblyByType(type);
				returnCode = returnCode.replaceAll("\\[dummies\\]" + type, "\\[" + assembly + "\\]" + type);
			
				if (returnCode.indexOf( "[dummies]" ) < 0 ){
                    break;
                }
				//String extern = externSet[i];
			//returnCode = returnCode.replaceAll("\\[dummies\\]([a-zA-Z\\.]*)\\." + extern, "\\[" + extern + "\\]$1\\." + extern);
			
			}
		}
		
		return returnCode;     
    }
    
    /**
     * @param externSet
     * @return java.lang.String
     * @roseuid 40FD27B201C2
     */
    public String createExternDeclarations(String[] externSet) {
		String declarations = "";
		for( int i = 0; i < externSet.length; i++ )
		{
			String name = externSet[i];
			declarations += ".assembly extern " + name + "\r\n";
			declarations += "{\r\n";
  			declarations += "\t.ver 0:0:0:0\r\n";
			declarations += "}\r\n"; 
		}
		return declarations;
    }
    
    /**
     * @param path
     * @return java.lang.String[]
     * @throws Exception
     * @roseuid 40FD27B201D2
     */
    public String[] readLines(String path) throws Exception {
		ArrayList lines = new ArrayList();
		
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		String line = reader.readLine();
		do
		{
			lines.add(line);
			line = reader.readLine();
		}
		while( line != null );
		
		reader.close();

		return (String[])lines.toArray(new String[lines.size()]);     
    }
    
    /**
     * @param path
     * @param content
     * @throws Exception
     * @roseuid 40FD27B2028D
     */
    public void writeFile(String path, String content) throws Exception {
		FileWriter w = new FileWriter(path);
		w.write(content);
		w.close();     
    }
}
