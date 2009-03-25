
class T14192rt1 {
    public static void main(String [] args) {
        Object o = new Object();
        try {
            synchronized (o) {
                System.out.print("O");
                return;
            }
        }
        finally {
            try {
                raise();
            }
            catch (Exception e) {
                System.out.print("K");
            }
        }
    }
    public static void raise() throws Exception {
        throw new Exception();
    }
}
    