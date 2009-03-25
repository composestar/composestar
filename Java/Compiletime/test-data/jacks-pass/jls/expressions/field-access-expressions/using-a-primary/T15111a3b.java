
interface T15111a3b {
    int i = 2;
}
class T15111a3c extends p1.T15111a3a implements T15111a3b {}
class T15111a3d extends T15111a3c {
    // Even though d inherits two versions of i, the protected a.i
    // is only accessible if the qualifying expression is type d or lower
    static int j = new T15111a3c().i; // only b.i is accessible
}
