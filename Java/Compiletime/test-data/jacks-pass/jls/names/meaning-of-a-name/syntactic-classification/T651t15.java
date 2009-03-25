
class T651t15 {
    
        class Super {
            int i;
        }
        class Sub extends Super {
            int Sub;
            int j = Sub.super.i;
        }
    
}
