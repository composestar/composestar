
    class T652s3b {
        static class p1 {
            static int T652s3a;
        }
        void foo() {
            // Even though p1.T652s3a is also a type, this is resolved to
            // the int T652s3b.p1.T652s3a.
            p1.T652s3a++;
        }
    }
