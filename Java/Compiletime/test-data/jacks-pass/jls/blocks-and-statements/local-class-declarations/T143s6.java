
public class T143s6 {
    T143s6 (){}
    public static void main(String[] args) {
        
	class Local {}
	{
	    new Object() {
		class Local {}
	    };
	}
	new Object() {
	    class Local {}
	};
    
    }
}
