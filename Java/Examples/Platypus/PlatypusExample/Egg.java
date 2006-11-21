package PlatypusExample;

public class Egg
{
	private Animal hatchling;
	private String name;

	public Egg(Platypus h)
	{
		this((Bird)(Object)h);
	}
	
	public Egg(Animal hatchling)
	{
		this.hatchling = hatchling;
        this.name = "Egg#1";
	}

	public String getName()
	{
		return name;
	}

	public Animal hatch() 
	{
        return hatchling;
	}
}
