package Composestar.DotNET.TYPEX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import Composestar.Core.Exception.ModuleException;
import Composestar.Utils.Debug;
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
		Debug.out(Debug.MODE_DEBUG, TYPEX.MODULE_NAME, "Expanding '" + source.getFilename() + "'...");
		
		String ext = FileUtils.getExtension(source.getFilename());
		MethodEmitter me = getMethodEmitter(ext); 

		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(source.getFilename()));
			writer = new BufferedWriter(new FileWriter(getDestFile(source)));
			int curPos = 0;
			
			for (ExpandedType et : source.sortedTypes())
			{
				int skip = et.getEndPos() - curPos;
				curPos += skip;
				
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