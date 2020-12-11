
public interface Virus {

    /*
    il metodo avanzamento viene applicato ogni giorno.
    Se l'utente non applica nessuna strategia il virus avanza senza ostacoli
    */

    void avanzamento(MyGoverno.Popolazione p);

}