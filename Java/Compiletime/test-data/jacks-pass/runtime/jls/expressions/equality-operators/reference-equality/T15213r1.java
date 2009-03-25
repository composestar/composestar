
class T15213r1 {
    public boolean equals(Object o) {
	return this == o;
    }
    boolean not(Object o) {
	return this != o;
    }
    public static void main(String[] args) {
	T15213r1 t = new T15213r1();
	System.out.print(t.equals("") + " " + t.not("") + " ");
	System.out.print(t.equals(t) + " " + t.not(t));
    }
}
    