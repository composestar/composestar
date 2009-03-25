
class T131rm6a {
    // VM should call a.toString, even though linked to Object.toString
    public static void main(String[] args) {
        new T131rm6b();
    }
    public String toString() {
        return "1";
    }
}
    