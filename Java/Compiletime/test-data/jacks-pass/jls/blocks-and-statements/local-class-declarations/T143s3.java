
public class T143s3 {
    T143s3 (){}
    public static void main(String[] args) {
        
	class Local {
	    { new Local() {}; }
	}
	new Local();
	{
	    new Local();
	}
    
    }
}
