package PlatypusExample;

public class Main
{
	public static void main(String[] args)
	{
		Caretaker man = new Caretaker("John");
		System.out.println("We contracted an animal caretaker: " + man.getName());

		Platypus mother = new Platypus();
		mother.setName("Mary");
		System.out.println("We caught a female platypus: " + mother.getName());

		System.out.println();

		Object egg = mother.reproduce();

		man.collectEggs((Bird)(Object)mother, (Egg)egg);  // Casting required to pass type verification of the Java compiler

		System.out.println();

		Platypus young = (Platypus)(Object)man.hatchEgg();
		young.setName("Kyle");
		System.out.println("Mother platypus '" + mother.getName() + "' proudly looks at her young '" + young.getName() + "'.");

		System.out.println();
 
		System.out.println("Is young '" + young.getName() + "' hungry? " + young.isHungry() );
		
		man.feedMilk((Mammal)(Object)young);  // Casting required to pass type verification of the Java compiler 
		
		System.out.println("Is young '" + young.getName() + "' still hungry? " + young.isHungry());
	/*
		System.out.println();
		System.out.println("Platypus.getName(): "	+ ((Platypus)young).getName());			// -> Kyle
		System.out.println("Animal.getName(): "		+ ((Animal)(Object)young).getName());	// -> FIXME: ClassCastException
		System.out.println("Mammal.getName(): "		+ ((Mammal)(Object)young).getName());	// -> Kyle
		System.out.println("Bird.getName(): "		+ ((Bird)(Object)young).getName());		// -> FIXME: null
	*/
	}
}
