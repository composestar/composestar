
abstract class T8111da11b extends p1.T8111da11a {
    // b.m() does not implement a.m(), since it is not accessible; no concrete
    // subclass of b can exist (either it would be out of p1, and still not
    // implement a.m(), or it would be in p1, and cannot override a.m and b.m
    // simultaneously)
    protected int m() { return 1; }
}
    