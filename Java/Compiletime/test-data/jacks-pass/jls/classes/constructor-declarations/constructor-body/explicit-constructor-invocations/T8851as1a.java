
class T8851as1a {
    T8851as1a(String s) {}
    T8851as1a(Integer i) {}
}
class T8851as1b extends T8851as1a {
    T8851as1b() {
	super(null); // ambiguous
    }
}
    