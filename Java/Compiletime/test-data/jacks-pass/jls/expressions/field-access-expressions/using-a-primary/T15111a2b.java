
interface T15111a2b {
    int i = 2;
}
class T15111a2c extends p1.T15111a2a implements T15111a2b {
    static int j = new T15111a2c().i; // only b.i is accessible
}
