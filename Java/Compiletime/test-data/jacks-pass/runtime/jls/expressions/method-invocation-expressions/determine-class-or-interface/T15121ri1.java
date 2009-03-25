
class T15121ri1_Base
{
  public void whoami( )
  {
    System.out.print( getClass( ).getName( ));
  }
}

public class T15121ri1 extends T15121ri1_Base
{
  class Foo extends T15121ri1_Base
  {
    Foo( )
    {
      whoami( );
    }
  }

  void bar( )
  {
    new Foo( );
  }

  public static void main( String[] args)
  {
    new T15121ri1( ).bar( );
  }
}
    