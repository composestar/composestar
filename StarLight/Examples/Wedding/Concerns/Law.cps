concern Law in Wedding
{
  filtermodule TaxPaying
  {
	 externals
		taxOffice : Wedding.ConcernImplementations.TaxOffice = Wedding.ConcernImplementations.TaxOffice.GetInstance();
     inputfilters
		incomeTax : Before = {[*.RecieveCash] taxOffice.PayIncomeTax}
  }

  filtermodule Licences
  {
	 externals
		townHall : Wedding.ConcernImplementations.TownHall = Wedding.ConcernImplementations.TownHall.GetInstance();
     outputfilters
		weddingLicence : Before = {[*.PreformWedding] townHall.NeedWeddingLicence}
  }

  superimposition
  {
    selectors
		money = { C | isClassWithName(C, 'Wedding.ConcernImplementations.Pocket') };
		about = { C | isClassWithName(C, 'Wedding.Person') };
	filtermodules
        money <- TaxPaying;
        about <- Licences;
   }
}