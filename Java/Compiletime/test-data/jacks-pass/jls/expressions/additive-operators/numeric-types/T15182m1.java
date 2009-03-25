
class T15182m1 {
    
	void m(int i, long l, float f, double d) {
	    l = 0L + i;
	    l = i + 0L;
	    l = 0L - i;
	    l = i - 0L;

	    f = 0f + i;
	    f = i + 0f;
	    f = 0f - i;
	    f = i - 0f;
	    f = -0f + i;
	    f = i + -0f;
	    f = -0f - i;
	    f = i - -0f;

	    f = 0f + l;
	    f = l + 0f;
	    f = 0f - l;
	    f = l - 0f;
	    f = -0f + l;
	    f = l + -0f;
	    f = -0f - l;
	    f = l - -0f;

	    d = 0. + i;
	    d = i + 0.;
	    d = 0. - i;
	    d = i - 0.;
	    d = -0. + i;
	    d = i + -0.;
	    d = -0. - i;
	    d = i - -0.;

	    d = 0. + l;
	    d = l + 0.;
	    d = 0. - l;
	    d = l - 0.;
	    d = -0. + l;
	    d = l + -0.;
	    d = -0. - l;
	    d = l - -0.;

	    d = 0. + f;
	    d = f + 0.;
	    d = 0. - f;
	    d = f - 0.;
	    d = -0. + f;
	    d = f + -0.;
	    d = -0. - f;
	    d = f - -0.;
	}
    
}
