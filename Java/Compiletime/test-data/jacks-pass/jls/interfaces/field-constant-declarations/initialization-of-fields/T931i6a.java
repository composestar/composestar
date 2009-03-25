
interface T931i6a {
    int i = new Object() {
	int m() { return 0; }
    }.m();
}
class T931i6b {
    int j = T931i6a.i;
}
    