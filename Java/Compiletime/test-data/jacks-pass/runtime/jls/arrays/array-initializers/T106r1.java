
class T106r1 {
    public static void main(String[] args) {
        Object[] o = { new int[] {0, 1}, new double[] {0, -0., 1}, null };
        System.out.print(((int[]) o[0])[0] + " " + ((int[]) o[0])[1] + " "
                         + ((double[]) o[1])[0] + " " + ((double[]) o[1])[1]
                         + " " + ((double[]) o[1])[2] + " " + o[2]);
    }
}
    