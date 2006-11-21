package PlatypusExample;

public abstract class Animal
{
	protected String name;
	protected boolean hungry = true;
	
	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public boolean isHungry() 
	{
		return hungry;
	}

	public abstract Object reproduce();
	public abstract void eat(Food f);
}
