package final_proj;

public class Reflector
{
  // The output of the reflector
  private final String out;

  // Actual wirings of reflectors A, B, and C
  public static final Reflector A = new Reflector("EJMZALYXVBWFCRQUONTSPIKHGD");
  public static final Reflector B = new Reflector("YRUHQSLDPXNGOKMIEBFZCWVJAT");
  public static final Reflector C = new Reflector("FVPJIAOYEDRZXWGCTKUQSBNMHL");

  public Reflector(String out)
  {
    this.out = out;
  }

  public String out()
  {
    return out;
  }

  // Output for specified input char
  public char output(char in)
  {
    return out.charAt((int)in - 65);
  }
}