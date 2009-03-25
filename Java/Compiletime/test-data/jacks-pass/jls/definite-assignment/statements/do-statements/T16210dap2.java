
public class T16210dap2 {
    T16210dap2 (){}
    public static void main(String[] args) {
        
	int i;
	do
	    try {
		break; // not an exiting break;
	    } finally {
		i = 1;
		break;
	    }
        while (true);
        int j = i;
    
    }
}
