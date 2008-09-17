// super imposition constraints
concern correct06_si
{
	filtermodule FM1
	{}
	
	filtermodule FM2
	{}
	
	filtermodule FM3
	{}
	
	superimposition
	{
		selectors
			s1 = { C | isClassWithName(C, 'C1') };
			s2 = { C | isClassWithName(C, 'C2') };
		filtermodules
			s1 <- FM1;
			s2 <- FM1, FM2;
			s1 <- FM3;
		constraints
			pre(FM3, FM1);
			pre(FM2, FM3);
	}
}