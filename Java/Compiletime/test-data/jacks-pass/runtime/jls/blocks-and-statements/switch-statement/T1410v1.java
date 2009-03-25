
class T1410v1 {
  public static void main(String [] args) {
    // prevent compiler optimization of switch by using args.length
    switch (args.length + 0x7ffffff2) {
    case 0x7ffffff0:
    case 0x7ffffff1:
    case 0x7ffffff2:
    case 0x7ffffff3:
      System.out.print("OK");
      break;
    default:
      System.out.print("NOT_OK");
    }
  }
}
    