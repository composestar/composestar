
// T62e1 is identifier
class T62e1 {
    // main, args are identifier; String is name
    public static void main(String[] args) {
        // c is identifier; Class, System.out.getClass are names
        Class c = System.out.getClass();
        // length is identifier; System.out.println, c are names
        System.out.println(c.toString().length() + 
        // first length is identifier; args, args.length are names
            args[0].length() + args.length);
    }
}
