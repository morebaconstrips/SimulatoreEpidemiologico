
public class MyVirus implements Virus {

    static int giornoCorrente = 1;
    final int durata;
    final double infettivita;
    final double sintomaticita;
    final double letalita;


    public MyVirus(double infettivita, double sintomaticita, double letalita, int durata) {

        this.durata = durata;
        this.infettivita = infettivita;
        this.letalita = letalita;
        this.sintomaticita = sintomaticita;
    }

    @Override
    public void avanzamento(MyGoverno.Popolazione p) {

        // Questo metodo mostra l'avanzamento del virus giorno dopo giorno


        if (giornoCorrente > durata/6) {

            MyGoverno.Popolazione.personeInMovimento = MyGoverno.Popolazione.verdi + MyGoverno.Popolazione.gialli - MyGoverno.isolatiVerdi - MyGoverno.isolatiGialli;

            // Velocità dinamica = velocità : popolazione = x : persone in movimento
            double Vd = (double) (MyGoverno.Popolazione.velocita * MyGoverno.Popolazione.personeInMovimento) / MyGoverno.Popolazione.numeroIndividui;

            // Fattore di contagiosità
            double R0 = Vd * (infettivita / 100);               // Non moltiplico per la durata per avere l'indice di contagi giornaliero

            // contagiabili = popolazione : 100 = p.verdi : x
            double contagiabili = (double) (MyGoverno.Popolazione.verdi - MyGoverno.isolatiVerdi) / MyGoverno.Popolazione.numeroIndividui;   // indice che va da 1 (quando i verdi sono uguali
                                                                                                                         // all'intera popolazione) a 0 ( quando i verdi sono = 0)

            long nuoviContagi = Math.min((long) Math.ceil(R0 * (MyGoverno.Popolazione.gialli - MyGoverno.isolatiGialli) * contagiabili), MyGoverno.Popolazione.verdi - MyGoverno.isolatiVerdi);
            long nuoviSintomatici = (long) ((sintomaticita / 100) * MyGoverno.Popolazione.gialli);
            long nuoviMorti = (long) ((letalita / 100) * MyGoverno.Popolazione.rossi);
            long nuoviGuaritiGialli = (giornoCorrente>durata)? MyGoverno.Popolazione.gialli - nuoviSintomatici : 0;
            long nuoviGuaritiRossi = (giornoCorrente>durata)? MyGoverno.Popolazione.rossi - nuoviMorti : 0;

            // Individui sani
            MyGoverno.Popolazione.verdi = MyGoverno.Popolazione.verdi - nuoviContagi;

            // Individui asintomatici
            MyGoverno.Popolazione.gialli = MyGoverno.Popolazione.gialli + nuoviContagi - nuoviSintomatici - nuoviGuaritiGialli;

            // Individui sintomatici
            MyGoverno.Popolazione.rossi = MyGoverno.Popolazione.rossi + nuoviSintomatici - nuoviMorti - nuoviGuaritiRossi;

            // Morti
            MyGoverno.Popolazione.neri = MyGoverno.Popolazione.neri + nuoviMorti;

            // Guariti
            MyGoverno.Popolazione.blu = MyGoverno.Popolazione.blu + nuoviGuaritiRossi + nuoviGuaritiGialli;

            MyGoverno.isolatiGialli = Math.min(MyGoverno.Popolazione.gialli, MyGoverno.isolatiGialli);      // Dato che i gialli diminuiscono diventando rossi o blu nel corso del tempo,
                                                                                                            // allora anche gli isolati gialli devono diminuire (non possono essere > gialli)

            MyGoverno.Popolazione.numeroIndividui -= nuoviMorti;

            if (MyGoverno.Popolazione.rossi > 0) MyGoverno.inizioIsolamento = true;

            if (giornoCorrente > durata && MyGoverno.Popolazione.gialli == 1) {
                MyGoverno.Popolazione.gialli--;
                MyGoverno.Popolazione.blu++;
            }

            giornoCorrente += 3;
        }

        else giornoCorrente += 3;

        MyGoverno.risorse -= (MyGoverno.isolatiVerdi + MyGoverno.isolatiGialli + MyGoverno.Popolazione.rossi * 3 * MyGoverno.costo);

    }
}