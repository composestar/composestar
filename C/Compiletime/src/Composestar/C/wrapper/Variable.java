package Composestar.C.wrapper;

public class Variable
{
	public String name = "";

	public boolean IsStatic = false;

	private int pointerLevel = 0;

	private int arrayLevel = 0;

	private boolean isGlobal = false;

	private boolean isExtern = false;

	private boolean isInline = false;

	public String FieldTypeString = "";

	public String filename = "";

	public String FieldTypeString()
	{
		return FieldTypeString;
	}

	public void setFieldTypeString(String FieldTypeString)
	{
		this.FieldTypeString = FieldTypeString;
	}

	public boolean isStatic()
	{
		return IsStatic;
	}

	public void setIsInline(boolean isInline)
	{
		this.isInline = isInline;
	}

	public boolean isInline()
	{
		return isInline;
	}

	public void setIsExtern(boolean isExtern)
	{
		this.isExtern = isExtern;
	}

	public boolean isExtern()
	{
		return isExtern;
	}

	public void setIsStatic(boolean isStatic)
	{
		IsStatic = isStatic;
	}

	public boolean isPointer()
	{
		return pointerLevel > 0;
	}

	public int getPointerLevel()
	{
		return pointerLevel;
	}

	public void setPointerLevel(int pointerLevel)
	{
		this.pointerLevel = pointerLevel;
	}

	public boolean isArray()
	{
		return arrayLevel > 0;
	}

	public void setArrayLevel(int arrayLevel)
	{
		this.arrayLevel = arrayLevel;
	}

	public int getArrayLevel()
	{
		return arrayLevel;
	}

	public boolean isGlobal()
	{
		return isGlobal;
	}

	public void setIsGlobal(boolean isGlobal)
	{
		this.isGlobal = isGlobal;
	}

	public String name()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String fileName()
	{
		return filename;
	}

	public void setFileName(String name)
	{
		filename = name;
	}

}
