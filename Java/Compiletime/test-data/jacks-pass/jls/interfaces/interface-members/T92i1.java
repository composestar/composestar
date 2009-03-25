
class T92i1 {
    
        interface I {}
        void foo(I i) {
            i.getClass();
            i.toString();
            i.equals(null);
            i.hashCode();
            try {
                i.wait();
                i.wait(1);
                i.wait(1, 0);
                i.notifyAll();
                i.notify();
            } catch (Throwable t) {
            }
        }
    
}
