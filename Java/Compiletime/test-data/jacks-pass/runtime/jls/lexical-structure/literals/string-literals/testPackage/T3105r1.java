
package testPackage;
  class T3105r1 {
      public static void main(String[] args) {   
        String hello = "Hello", lo = "lo";
        System.out.print((hello == "Hello") + " ");
        System.out.print((Other1.hello == hello) + " ");
        System.out.print((other.Other.hello == hello) + " ");
        System.out.print((hello == ("Hel"+"lo")) + " ");
        System.out.print((hello == ("Hel"+lo)) + " ");
        System.out.print(hello == ("Hel"+lo).intern());
      }
}
class Other1 { static String hello = "Hello"; }
    