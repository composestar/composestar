// Use filter modules from a different concern
concern correct09_externalfms
{
	superimposition
	{
		selectors
			s1 = { C | isClassWithName(C, 'C1') };
			s2 = { C | isClassWithName(C, 'C2') };
		filtermodules
			s1 <- correct06_si::FM1;
			s1 <- correct06_si.FM1;
			s2 <- Concern.Examples.correct03_basic.FM1, correct06_si.FM2;
	}
}
