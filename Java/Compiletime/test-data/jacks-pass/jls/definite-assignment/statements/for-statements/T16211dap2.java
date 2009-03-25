
public class T16211dap2 {
    T16211dap2 (){}
    public static void main(String[] args) {
        
	int i;
	for ( ; ; )
	    try {
		break; // not an exiting break;
	    } finally {
		i = 1;
		break;
	    }
        int j = i;
    
    }
}
