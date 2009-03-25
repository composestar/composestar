
class T1516a13 {
    
	interface I { int m(); }
	class C { void m() {} }
	I[] i = (I[]) new C[0];
    
}
