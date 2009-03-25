
class T143s8 {
    
	class Cyclic {}
	void foo() {
	    new Cyclic(); // create a T143s8.Cyclic
	    {
		class Local{};
		{
		    class AnotherLocal {
			void bar() {
			    class Local {}; // ok
			}
		    }
		}
	    }
	    class Local{}; // ok, not in scope of prior Local
	}
    
}
