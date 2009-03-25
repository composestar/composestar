
class T1410o2 {
    public static void main(final String[] args) {
        Thread t = new Thread() {
            public void run() {
                switch (args.length) {
                default:
                    System.out.print(1);
                    break;
                }
                switch (args.length) {
                case 0:
                    System.out.print(2);
                    break;
                }
                switch (args.length + 1) {
                case 1:
                    System.out.print(3);
                    break;
                }
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
    