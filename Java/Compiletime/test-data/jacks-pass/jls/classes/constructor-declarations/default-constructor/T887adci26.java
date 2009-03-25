
class T887adci26 {
    
        T887adci26(Object o) {}
        {
            class A {
                private A() {}
            }
            class B extends T887adci26 {
                B() {
                    super(new A() {});
                }
            }
        }
    
}
