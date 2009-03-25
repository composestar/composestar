
class T1528rc3 extends Thread {
    private static int i = 0;
    public static void main(String[] args) {
       synchronized ("foo" + "bar") {
           // did we sync on the same object? If the compiler correctly
           // inlined the String expression, and the JVM correctly
           // does mutual exclusion, then we have already locked
           // "foobar", stopping the second thread in its tracks.
           System.out.print("1 ");
           new T1528rc3().start(); // build new thread
           try {
               sleep(500); // give second thread a chance
           } catch (InterruptedException e) {
           }
           if (i == 2)
           System.out.println(" Constant not inlined; wrong object locked ");
           else if (i == 0)
           System.out.println(" Indeterminate - thread never ran ");
           else if (i == 1)
           System.out.print("3 ");
           else
           System.out.println(" Unexpected result ");
       }
    }
    public void run() {
       System.out.print("2 ");
       i = 1;
       synchronized ("foobar") {
           i = 2;
       }
       System.out.print("4");
    }
}
    