
class T1617cdap7 {
    
	String s;
	void foo() {
            T1617cdap7 x;
	    (true ? x = new T1617cdap7() : null).s += x;
	}
    
}
