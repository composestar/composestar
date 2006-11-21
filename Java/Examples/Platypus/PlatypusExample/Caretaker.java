package PlatypusExample;

import java.util.ArrayList;

public class Caretaker
{
	private String name;
	private ArrayList bag;
	
	public Caretaker(String name)
	{
		this.name = name;
		this.bag = new ArrayList();
	}

	public String getName()
	{
		return name;
	}

	public void collectEggs(Bird mother, Egg egg)
	{
		bag.add(egg);
		System.out.println("Caretaker '" + getName() + "' has collected the egg '" + egg.getName() +"'");
	}

	public Animal hatchEgg() 
	{
		if (bag.size() == 0) 
		{
			return null;
		}

		Egg hatchingEgg = (Egg)bag.get(0);
		bag.remove(0);

		System.out.println("Caretaker '" + name + "' is waiting for '" + hatchingEgg.getName() + "' to hatch...");		
		Animal hatchling = hatchingEgg.hatch();
		
		System.out.println("Egg '" + hatchingEgg.getName() + "' has hatched.");
		return hatchling;
	}

	public void feedInsect(Bird youngster)
	{
        System.out.println("Caretaker '" + name + "' is feeding '" + youngster.getName() + "' an insect...");
		youngster.eat(Food.INSECT);
	}

	public void feedMilk(Mammal youngster)
	{
		System.out.println("Caretaker '" + name + "' is feeding '" + youngster.getName() + "' with a bottle of milk...");
		youngster.eat(Food.MILK);
	}
}
