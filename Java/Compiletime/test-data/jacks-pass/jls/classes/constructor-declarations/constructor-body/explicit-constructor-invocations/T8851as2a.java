
class T8851as2a {
    T8851as2a(String s) {}
    private T8851as2a(Integer i) {}
}
class T8851as2b extends T8851as2a {
    T8851as2b() {
	super(null); // calls T8851as2a(String)
    }
}
    