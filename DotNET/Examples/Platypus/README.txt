	THE PLATYPUS EFFECT 

	A number of programmers have described their class hierarchies as being "brittle". 
	Class hierarchies are often used to represent taxonomies. In the "real world", the term 
	"taxonomy" refers to the system of biological classification into phyla, genus, species and so forth. 
	In the software domain, the term is sometimes used to refer to a hierarchical 
	categorization of a diverse set of objects. An example would be the various flavors 
	of widgets in a moden GUI environment. 

	However, real object collections aren't always hierarchical. In the biological world, 
	for example, we see that scientists are constantly re-arranging the overall category structure 
	for unicellular organisms. And as it turns out, the unicellular organisms can't be arranged 
	into a strict tree-like hierarchy, because apparently there has been a lot of "cross-branching" 
	as organisms have borrowed and incorporated genetic material from one another. 

	Similarly, in the software world, collections of objects which appear to be hierarchically arranged 
	are often only superficially so. As one programmer put it: "You have your 'isa' hierarchy all 
	thought out - let's say you have a "mammals" class and a "reptiles" class and so on - and you 
	start to implement it, and along comes a platypus, a fur-bearing, egg-laying, duck-billed creature, 
	which doesn't appear to fit in any of the classifications you've created. So what you often end up 
	having to do is rethink your entire hierarchy, refactoring into a different set of basic categories, 
	or maintaining several categorizations along different axes. A lot of your thinking ends up getting 
	thrown out, as well as any implementation you've done up to that point." 

	In other words, this refactoring has caused exactly the kind of massive code ripple that proper 
	object design was supposed to save us from. 
									
									[from http://www.advogato.org/article/83.html]

Known issue: At this time it is not possible to restrict object behaviour once casted to a superclass.
E.g.: It's possible to feed the platypus young just like a bird, i.e. with an insect. 
									
In this example, we try to address this problem using composition filters approach. Assume you modelled
a simple inheritance hierarchy to group the animals into different subclasses and enhanced reuse of 
functionality and data as follows:

***** THE FOLLOWING INFO IS OUT OF DATE *****

Abstract root class Animal has two methods: 
	
	public abstract Animal giveBirth();
	public abstract void feed(Animal child);

Mammal class inherits from Animal class and provides mamal specific birth giving (i.e. child is created inside mother)
and feeding (i.e. feeding the infant with milk) functionality.

Bird class inherits from Animal class and provides bird specific birth giving (i.e. infant grows inside egg)
and feeding (i.e. feeding the infant with half digested food) functionality.

Now you come to the model of Platypus, which lays eggs but feeds the child with milk. Obviously, this animal
doesn't fit under any of the leaves of the hierarchy so far. We can create a new concern for solving this problem
and use a simple dispatch filter to provide multiple inheritance as follows:

// Concern definition for Platypus
concern Platypus
{
	//Filter module which provides multiple 
	//inheritance functionality
	filtermodule MultitpleInheritance{
		
		//Platypus concern has two internal fields
		internals
			
			//Mammal object is going to be used to provide feeding dunctionality.
			m : PlatypusExample.Mammal;
			
			//Bird object is going to be used to provide birth giving functionality.
			b : PlatypusExample.Bird;
		
		//These filters intercept incoming messages to the object
		inputfilters
			
			// A call to Platypus.eat or Platypus.isHungry,
			// is always dispatched to the mammel.
			// All other calls are dispatched to the inner object,
			// in this case the animal.
			eat : Dispatch = { [*.eat] m.eat };
			hungry : Dispatch = { [*.isHungry] m.isHungry }
	}
	
	//This part determins which objects, what filters are going to be superimposed
	superimposition{
		
		//Select the Platypus class for superimposition
		selectors
		
			files = { *=PlatypusExample.Platypus };
		
		filtermodules
			
			//Superimpose the multipleinheritance filtermodule 
			// on the specified selector
			files <- MultitpleInheritance;
	}
	
	implementation by PlatypusExample.Platypus;
}