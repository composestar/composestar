package PlatypusExample;

public class Bird extends Animal
{
	public Bird()
	{
	}

	public Object reproduce()
	{
		System.out.println("Bird '" + name + "' is laying an egg...");
		return new Egg(new Bird());
	}
	
	public void feed(Animal child)
	{
		System.out.print("Bird is feeding it's child: " + child.getName());
	}

	public void eat(Food f)
	{
		if (f == Food.INSECT)
		{
			System.out.println("'" + name + "' is eating the food '" + f.getName() + "' it was given.");
			this.hungry = false;
		}
		else 
		{
			System.out.println("'" + name + "' is not eating the given food '" + f.getName() + "'.");
		}
	}
}
