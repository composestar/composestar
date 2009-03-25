
class T131rm5a {
    String m() {
        return "1";
    }
}
class T131rm5b extends T131rm5a {}
class T131rm5c extends T131rm5b {
    T131rm5c() {
        // should be invokespecial T131rm5b.toString; even though a declares
        System.out.print(super.m());
    }
    String m() {
        return "3";
    }
}
    