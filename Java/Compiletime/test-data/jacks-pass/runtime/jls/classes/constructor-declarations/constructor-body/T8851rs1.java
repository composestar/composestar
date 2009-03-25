
class T8851rs1 {
  public static int value = 0;
  class Inner {
    Inner(int i) {}
  }
  static class Der extends Inner {
    Der(T8851rs1 outer, int i) {
      outer.super(value = ++i);
    }
  }
  public static void main(String[] args) {
    try {
      new Der(null, 5);
    } catch (NullPointerException npe) {
      if (value == 0)
	System.out.print("OK");
      else
	System.out.print("should throw NullPointerException before argument evaluation");
      return;
    }
    System.out.print("should throw NullPointerException");
  }
}
    