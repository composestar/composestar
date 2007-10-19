package Composestar.DotNET2.TYPEX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import composestar.dotNET2.tym.entities.ExpandedType;

class ExpandedSource
{
	private String filename;

	private List<ExpandedType> types;

	public ExpandedSource(String filename)
	{
		this.filename = filename;
		types = new ArrayList<ExpandedType>();
	}

	public String getFilename()
	{
		return filename;
	}

	public ExpandedType addNewType()
	{
		ExpandedType et = ExpandedType.Factory.newInstance();
		types.add(et);
		return et;
	}

	public Iterable<ExpandedType> sortedTypes()
	{
		Comparator<ExpandedType> c = new Comparator<ExpandedType>()
		{
			public int compare(ExpandedType a, ExpandedType b)
			{
				int endPosA = a.getEndPos();
				int endPosB = b.getEndPos();

				if (endPosA == endPosB)
				{
					throw new IllegalArgumentException();
				}

				return (endPosA < endPosB ? -1 : 1);
			}
		};
		Collections.sort(types, c);
		return types;
	}
}
