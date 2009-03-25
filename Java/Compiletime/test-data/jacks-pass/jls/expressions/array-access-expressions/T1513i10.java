
public class T1513i10 {
    T1513i10 (){}
    public static void main(String[] args) {
        
	int[] i = { 1 };
	short s = 0;
	byte b = 0;
	i[s] = i[b] = i['\0'] = i[0];
    
    }
}
