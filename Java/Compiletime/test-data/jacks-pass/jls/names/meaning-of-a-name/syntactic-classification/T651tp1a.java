
class T651tp1a {
    int C;
    int C() { return 0; }
    static class C {
	static class Inner {}
    }
}
// T651tp1a.C is a typeorpackage, which gets resolved to a type; and not an int
class T651tp1b extends T651tp1a.C.Inner {}
    