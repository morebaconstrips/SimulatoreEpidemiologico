
public interface Governo { // Vengono combinati gli strumenti per creare le strategie

    void isolamento(boolean b, long numeroIsolatiVerdi, long numeroIsolatiGialli);               // STRUMENTO N. 1
    // riduce il parametro velocità
    // riduce le persone in movimento (conseguenze di mancata produzione)

    void tampone(long tamponi);                                                                  // STRUMENTO N. 2
    // ritorna il numero di persone positive
    // riduce le risorse
    // scopre nuovi contagiati

}