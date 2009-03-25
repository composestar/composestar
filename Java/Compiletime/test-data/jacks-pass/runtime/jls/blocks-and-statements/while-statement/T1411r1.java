
class T1411r1 {
    public static void main(String[] args) {
	int i = 0;
        while (i < 10) {
            try {
                i++;
		if (i == 5)
                    throw new Exception();
            } catch (Exception ex) {
		System.out.print("a ");
                continue;
            }
        }
	System.out.print(i);
    }
}
    