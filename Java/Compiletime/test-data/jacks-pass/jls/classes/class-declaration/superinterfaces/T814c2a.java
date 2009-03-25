
class E1 extends Exception {}
class E2 extends Exception {}
class E3 extends Exception {}
interface T814c2a { void foo() throws E1, E2; }
interface T814c2b { void foo() throws E2, E3; }
class T814c2c implements T814c2a, T814c2b {
    public void foo() throws E2 {}
}
