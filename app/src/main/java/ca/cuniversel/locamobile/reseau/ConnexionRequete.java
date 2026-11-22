package ca.cuniversel.locamobile.reseau;

// Corps du POST api/auth/connexion : Gson le transforme en JSON (@Body)
public class ConnexionRequete {
    public String courriel;
    public String motDePasse;

    public ConnexionRequete(String courriel, String motDePasse) {
        this.courriel = courriel;
        this.motDePasse = motDePasse;
    }
}
