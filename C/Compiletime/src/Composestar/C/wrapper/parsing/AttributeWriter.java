package Composestar.C.wrapper.parsing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Utils.Debug;

public class AttributeWriter
{

	private static AttributeWriter Instance = null;

	public ArrayList annotations = new ArrayList();

	public AttributeWriter()
	{

	}

	public static AttributeWriter instance()
	{
		if (Instance == null)
		{
			Instance = new AttributeWriter();
		}
		return (Instance);
	}

	public void saveToXML(String fileName)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("<?xml version=\"1.0\"?>\n");
			bw.write(" <Attributes>\n");
			annotationsToXML(bw);
			bw.write(" </Attributes>\n");
			bw.close();
		}
		catch (IOException io)
		{
			Debug.out(Debug.MODE_DEBUG, "Wrapper", "Error while creating attributes.xml ");
		}
		catch (Exception e)
		{
			Debug.out(Debug.MODE_DEBUG, "Wrapper", "Error while creating attributes.xml ");
		}
	}

	public void addAnnotations(Annotation a)
	{
		annotations.add(a);
	}

	public void annotationsToXML(BufferedWriter bw)
	{
		Iterator i = annotations.iterator();
		Annotation anno = null;
		while (i.hasNext())
		{
			try
			{
				anno = (Annotation) i.next();
				bw.write("  <Attribute type=" + '"' + anno.getType() + '"' + " target=" + '"' + anno.getTarget() + '"'
						+ " targetName=" + '"' + anno.getTargetName() + '"' + " location=" + '"' + anno.getFileName()
						+ '"' + " >\n");
				bw.write("   <values>\n");
				Iterator values = anno.getValues().iterator();
				while (values.hasNext())
				{

					bw.write("     <value text=" + values.next() + "/>\n");
				}
				bw.write("   </values>\n");
				bw.write("  </Attribute>\n");
			}
			catch (IOException io)
			{
				Debug.out(Debug.MODE_DEBUG, "Wrapper", "Error while creating attributes.xml ");
			}
			catch (Exception e)
			{
				Debug.out(Debug.MODE_DEBUG, "Wrapper", "Error while creating attributes.xml ");
			}

		}
	}

}
