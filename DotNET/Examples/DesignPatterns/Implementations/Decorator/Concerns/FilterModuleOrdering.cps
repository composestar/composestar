concern FilterModuleOrdering 
{
  superimposition
  {
    constraints
      pre(StarDecoratorConcern::StarDecoratorFM,BracketDecoratorConcern::BracketDecoratorFM);
      pre(BracketDecoratorConcern::BracketDecoratorFM,DollarDecoratorConcern::DollarDecoratorFM);
  }
}
