
interface T15111a4b {
    int i = 2;
}
class T15111a4c extends p1.T15111a4a implements T15111a4b {}
class T15111a4d extends T15111a4c {
    // Even though d inherits two versions of i, the protected a.i
    // is only accessible if the qualifying expression is type d or lower
    static int j = new T15111a4d().i; // both a.i and b.i are accessible
}
