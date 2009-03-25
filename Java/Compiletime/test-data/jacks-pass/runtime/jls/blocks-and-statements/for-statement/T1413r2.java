
class T1413r2 {
    public static void main(String[] args) {
        Thread t = new Thread() {
            public void run() {
                for (int i = 0; ; i++)
                    if (i < 5) {
                        System.out.print(i);
                        continue;
                    } else
                        return;
            }
        };
        t.start();
        try {
            t.join(1000); // wait one second, to check for infinite loop
        } catch (Exception e) {
            System.out.print(" Interrupt");
        }
        if (t.isAlive())
            System.out.print(" Infinite");
        System.exit(0);
    }
}
    