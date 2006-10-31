package PlatypusExample;

public class Mammal extends Animal
{
	public Mammal()
	{	
	}
	
	public Object reproduce()
	{
		System.out.print("Mammal '" + name + "' is giving birth...");
		return new Mammal();	
	}
	
	public void eat(Food f)
	{
		if (f == Food.MILK)
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
