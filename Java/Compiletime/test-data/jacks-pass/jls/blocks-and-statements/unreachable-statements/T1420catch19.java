
class T1420catch19 {
    
        class MyException extends ClassNotFoundException {}
        void foo() throws Exception {
            ClassNotFoundException c = new MyException();
            try {
                throw c;
            } catch (MyException e) {
                // reachable, as variable reference can contain subclass
            }
        }
    
}
