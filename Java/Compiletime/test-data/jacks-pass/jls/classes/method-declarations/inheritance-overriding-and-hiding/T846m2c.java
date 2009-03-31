
interface T846m2a { void m(); }
abstract class T846m2b implements T846m2a {}

abstract class T846m2c extends T846m2b {
    void foo() { m(); }
}
    
