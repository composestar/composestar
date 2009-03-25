
class T852nsmu10 {
    
        class C {}
        static class T852nsmu10_Test {
            {
                // C is qualified, doesn't count as usage
                new T852nsmu10().new C();
            }
        }
    
}
