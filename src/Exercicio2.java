import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class Exercicio2 {

    public static Semaphore canGenerate = new Semaphore(1);

    public static Semaphore canFilter = new Semaphore(0);

    public static Semaphore canAnalyse = new Semaphore(0);

    public static Semaphore canFilterByAnalyser = new Semaphore(1);

    public static String generated;

    public static String filtered;

    private static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static void main(String[] args)
    {
        Thread generator = new Thread( new Generator());

        Thread filter = new Thread( new Filter());

        Thread analyse = new Thread( new Analyser());

        generator.start();

        filter.start();

        analyse.start();

    }
    static class Generator implements Runnable{

        private int length = 8;

        @Override
        public void run() {

            while (true)
            {
                try
                {
                    canGenerate.acquire();

                    generated = this.random();

                    canFilter.release();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }

        }

        private String random()
        {
            return new Random().ints( this.length,0,chars.length() )
                                .mapToObj( i -> "" + chars.charAt(i) )
                                .collect(Collectors.joining());
        }

    }

    static class Filter implements Runnable{

        @Override
        public void run() {
           while (true)
           {
               try
               {
                   canFilter.acquire();
                   
                   canFilterByAnalyser.acquire();

                   filtered = generated.toUpperCase();

                   canGenerate.release();

                   canAnalyse.release();

               }
               catch (InterruptedException e)
               {
                   e.printStackTrace();
               }
           }
        }

    }

    static class Analyser implements Runnable{

        @Override
        public void run() {
            while (true)
            {
                try
                {
                    canAnalyse.acquire();

                    this.findOccurences();

                    canFilterByAnalyser.release();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private void findOccurences()
        {
            chars.chars().forEach(c -> {
                System.out.println("String: " + filtered
                        +  " | Char: " + Character.toString((char) c)
                        +  " | Count: " + filtered.chars().filter(ch -> ch == c).count()
                );
            });

            System.out.println("=======================================================");
        }

    }
}
