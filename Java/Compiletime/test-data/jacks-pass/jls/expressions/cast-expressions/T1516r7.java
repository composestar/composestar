
class T1516r7 {
    
	interface I { int m(); }
	class C { void m() {} }
	I i = (I) new C();
    
}
