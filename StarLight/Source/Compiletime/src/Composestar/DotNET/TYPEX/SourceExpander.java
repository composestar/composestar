/**
 * 
 */
package Composestar.DotNET.TYPEX;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import Composestar.Core.Exception.ModuleException;
import Composestar.Utils.FileUtils;

import composestar.dotNET.tym.entities.ExpandedType;
import composestar.dotNET.tym.entities.MethodElement;

class SourceExpander
{
	public SourceExpander()
	{
	}

	public void expand(Iterable<ExpandedSource> sources) throws ModuleException
	{
		try
		{
			for (ExpandedSource source : sources)
				expand(source);			
		}
		catch (IOException e)
		{
			throw new ModuleException("IOException: " + e.getMessage(), TYPEX.MODULE_NAME);
		}
	}
	
	private void expand(ExpandedSource source) throws IOException
	{
		String ext = FileUtils.getExtension(source.toString());
		MethodEmitter me = getMethodEmitter(ext); 

		FileReader reader = null;
		FileWriter writer = null;
		try {
			reader = new FileReader(source.getFilename());
			writer = new FileWriter(getDestFile(source));
			int curPos = 0;
			
			for (ExpandedType et : source.sortedTypes())
			{
				int endPos = 12345;
				int skip = endPos - curPos;
				
				FileUtils.copy(reader, writer, skip);
				
				for (MethodElement method : et.getExtraMethods().getMethodList())
					me.emit(method, writer);
			}
			
			FileUtils.copy(reader, writer);
		}
		finally {
			FileUtils.close(reader);
			FileUtils.close(writer);
		}
	}
	
	private File getDestFile(ExpandedSource source)
	{
	//	String baseName = source.getFile().getName();
	//	File dir = new File(starlightFolder, "Expanded");
	//	return new File(dir, baseName);
		
		String oldName = source.getFilename();
		String newName = oldName + ".txt";
		return new File(newName);
	}

	private MethodEmitter getMethodEmitter(String ext)
	{
		if ("java".equals(ext) || "jsl".equals(ext))
			return new JSharpMethodEmitter();
		
		if ("cs".equals(ext))
			return new CSharpMethodEmitter();

		throw new IllegalArgumentException(
				"Unknown extension '" + ext + "'");
	}		
}