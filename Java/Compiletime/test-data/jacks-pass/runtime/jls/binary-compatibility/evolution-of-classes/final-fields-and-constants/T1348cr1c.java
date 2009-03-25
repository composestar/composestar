
class T1348cr1c {
    public static void main(String[] args) {
        try {
            Class c = Class.forName("T1348cr1b");
            c.getMethod("doit", null).invoke(null, null);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
    