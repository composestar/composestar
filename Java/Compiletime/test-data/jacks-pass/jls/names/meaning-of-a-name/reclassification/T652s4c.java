
    class T652s4c extends p2.T652s4b {
        void foo() {
            // Even though p1.T652s4a is also a type, this is resolved to
            // the inherited int T652s4b.p1.T652s4a.
            p1.T652s4a++;
        }
    }
