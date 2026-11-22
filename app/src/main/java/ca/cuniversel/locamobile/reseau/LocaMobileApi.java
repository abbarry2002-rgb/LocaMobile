package ca.cuniversel.locamobile.reseau;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

// La liste des endpoints de l'API.
public interface LocaMobileApi {

    @GET("api/logements")
    Call<List<Logement>> listerLogements();

    // Exercice 3 : @Path remplace {id} dans le chemin → api/logements/3
    @GET("api/logements/{id}")
    Call<Logement> trouverLogement(@Path("id") int id);

    // Exercice 3 : @Query ajoute ?ville=... après le chemin → api/logements?ville=Gatineau
    @GET("api/logements")
    Call<List<Logement>> chercherParVille(@Query("ville") String ville);

    // Exercice 4, temps 1 : courriel + mot de passe envoyés une seule fois, dans le corps
    @POST("api/auth/connexion")
    Call<TokenReponse> connecter(@Body ConnexionRequete requete);

    // Exercice 4, temps 2 : le jeton est présenté dans l'en-tête Authorization
    @GET("api/moi")
    Call<Profil> monProfil(@Header("Authorization") String entete);
}
