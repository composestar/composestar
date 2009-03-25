
class T131rm4c {
    // no longer extend b, now a's code won't link
    public static void main(String[] args) {
        try {
            T131rm4a.m();
        } catch (LinkageError e) {
            System.out.print("3");
        }
    }
    public String toString() {
        return "2";
    }
}
    