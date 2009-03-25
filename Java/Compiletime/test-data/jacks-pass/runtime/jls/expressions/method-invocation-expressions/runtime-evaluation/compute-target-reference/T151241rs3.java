
class T151241rs3 {
    public static void main(String[] args) {
        try {
            Class c = T151241rs3.class.forName("T151241rs3");
            System.out.print((c == T151241rs3.class ? "OK" : "oops"));
        } catch (Exception e) {
            System.out.print("Unexpected: " + e);
        }
    }
}
    