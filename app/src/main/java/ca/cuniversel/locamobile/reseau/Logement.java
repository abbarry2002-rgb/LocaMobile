package ca.cuniversel.locamobile.reseau;

// Le « moule » du JSON : chaque attribut porte exactement le nom d'une clé
// renvoyée par GET api/logements/{id}. Gson remplit les attributs par leur nom.
public class Logement {
    public int id;
    public String titre;
    public String ville;
    public double prixParNuit;
    public int nbVoyageurs;
    public boolean disponible;
    public String proprietaireNom;
}
