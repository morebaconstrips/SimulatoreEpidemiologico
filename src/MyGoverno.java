import java.util.Scanner;

public class MyGoverno implements Governo {


    // Dati sulle risorse italiane da poter investire per l'emergenza coronavirus
    // https://www.agi.it/economia/news/2020-06-03/quanti-soldi-italia-per-emergenza-coronavirus-8800736/
    static long risorse;
    // Le risorse vengono detratte in caso di:
    // 1. Mancata produzione da parte di individui fermi;
    // 2. Utilizzo dei tamponi;
    // 3. Ricovero dei malati;

    static int costo;  // prezzo di un tampone

    static long isolatiVerdi;
    static long isolatiGialli;
    static boolean inizioIsolamento = false;


    public MyGoverno(long risorse, int costo) {

        // Lo scopo del governo è quello di debellare il virus, quindi cercare di abbassare
        // l'R0 al di sotto del valore 1.
        // I suoi strumenti sono l'isolamento e il tampone; attraverso questi è possibile costruire delle strategie
        // combinando i due strumenti nel corso del tempo.

        this.risorse = risorse;
        this.costo = costo;

    }

    public static class Popolazione {

        static long verdi, gialli, rossi, blu, neri;
        static int velocita;
        static long numeroIndividui;
        static long personeInMovimento;


        public Popolazione(long numeroIndividui, int velocita) {

            this.velocita = velocita;
            this.numeroIndividui = numeroIndividui;

            // All'inizio la situazione è la seguente:
            verdi = numeroIndividui - 1; // Sono tutti negativi al virus tranne una persona
            gialli = 1;                  // Paziente 0 che fa partire l'epidemia
            rossi = 0;
            blu = 0;
            neri = 0;

            personeInMovimento = verdi + gialli - isolatiVerdi - isolatiGialli;

        }
    }


    @Override
    public void isolamento(boolean isolati, long numeroIsolatiVerdi, long numeroIsolatiGialli) {

        // Se "isolati" è true, allora il numero di isolati (sani o asintomatici) viene tolto dal numero di persone in movimento;
        // se invece "isolati" è false, il numero di isolati preso in input viene tolto dall'isolamento
        // e riaggiunto al numero di persone in movimento

        try {

            if (inizioIsolamento) {

                if (isolati) {

                    if (numeroIsolatiVerdi <= Popolazione.verdi - isolatiVerdi) {
                        Popolazione.personeInMovimento -= numeroIsolatiVerdi;
                        isolatiVerdi += numeroIsolatiVerdi;
                    } else {
                        throw new TooManyLockedDownException();
                    }
                    Popolazione.personeInMovimento -= numeroIsolatiGialli;
                    isolatiGialli += numeroIsolatiGialli;
                } else {
                    if (numeroIsolatiVerdi <= isolatiVerdi) {
                        Popolazione.personeInMovimento += numeroIsolatiVerdi;
                        isolatiVerdi -= numeroIsolatiVerdi;
                    } else {
                        throw new TooManyOutOfIsolationException();
                    }

                    Popolazione.personeInMovimento += numeroIsolatiGialli;
                    isolatiGialli -= numeroIsolatiGialli;
                }
            } else throw new TooEarlyException();
        } catch (TooEarlyException e) {
            System.err.println("\nNon si può far nulla finché il primo sintomatico non compaia sulla scena a dare l’allarme\n");
        } catch (TooManyLockedDownException e) {
            System.err.println("\nNon è possibile isolare più individui sani di quanti non ce ne siano in questo momento. Al momento ci sono " + isolatiVerdi + " individui sani in isolamento.\n");
        } catch (TooManyOutOfIsolationException e) {
            System.err.println("\nNon è possibile far uscire dall'isolamento più individui sani di quanti non ce ne siano stati messi. Al momento ci sono " + isolatiVerdi + " individui sani in isolamento.\n");
        }
    }


    @Override
    public void tampone(long tamponi) {

        try {
            if (inizioIsolamento) {
                Scanner sc = new Scanner(System.in);
                long gialliTrovati = 0;
                long t = tamponi;

                if (tamponi <= Popolazione.personeInMovimento) {

                    // proporzione randomica tra verdi e gialli il cui totale deve essere = numero tamponi

                    while (t-- > 0) {
                        long r = (long) (Math.random() * Popolazione.personeInMovimento);
                        if (r >= Popolazione.verdi - isolatiVerdi && gialliTrovati < Popolazione.gialli) gialliTrovati += 1;
                    }
                    System.out.println("Dei " + tamponi + " tamponi fatti sono stati trovati " + gialliTrovati + " individui asintomatici");

                    // L'utente sceglie se isolare o meno gli individui asintomatici trovati
                    System.out.println("Desideri isolare gli asintomatici trovati? [y][n]");
                    if (sc.next().equals("y")) isolamento(true, 0, gialliTrovati);
                } else {
                    System.err.println("\nIl numero di tamponi richiesto è superiore al numero di persone a cui è possibile farlo\n"); // TODO scriverlo in italiano
                }

                risorse -= (tamponi * costo);


            } else throw new TooEarlyException();
        } catch (TooEarlyException e) {
            System.err.println("\nNon si può far nulla finché il primo sintomatico non compaia sulla scena a dare l’allarme\n");
        }
    }
}
