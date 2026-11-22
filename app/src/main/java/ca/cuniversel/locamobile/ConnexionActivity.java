package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.ConnexionRequete;
import ca.cuniversel.locamobile.reseau.Profil;
import ca.cuniversel.locamobile.reseau.TokenReponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private EditText champCourriel, champMotDePasse;
    private TextView resultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        champCourriel = findViewById(R.id.champCourriel);
        champMotDePasse = findViewById(R.id.champMotDePasse);
        resultat = findViewById(R.id.resultat);

        findViewById(R.id.boutonConnecter).setOnClickListener(v -> connecter());
    }

    private void connecter() {
        String courriel = champCourriel.getText().toString().trim();
        String motDePasse = champMotDePasse.getText().toString();

        // Étape 5 : champs vides → setError, sans appeler l'API
        if (courriel.isEmpty()) {
            champCourriel.setError("Courriel requis");
            return;
        }
        if (motDePasse.isEmpty()) {
            champMotDePasse.setError("Mot de passe requis");
            return;
        }

        resultat.setText("Connexion…");

        // Temps 1 : on envoie le courriel et le mot de passe une seule fois
        ClientApi.get().connecter(new ConnexionRequete(courriel, motDePasse))
                .enqueue(new Callback<TokenReponse>() {
                    @Override
                    public void onResponse(Call<TokenReponse> call, Response<TokenReponse> reponse) {
                        Log.d(TAG, "connexion : code " + reponse.code());
                        if (reponse.isSuccessful() && reponse.body() != null) {
                            TokenReponse t = reponse.body();
                            // On range le jeton et le nom (jamais le mot de passe)
                            Session.enregistrer(ConnexionActivity.this, t.token, t.nom);
                            // Le jeton existe enfin : c'est ICI qu'on appelle api/moi
                            chargerProfil(t.token);
                        } else {
                            // Étape 7 : en cas d'erreur, body() est null ;
                            // le message du serveur est dans errorBody()
                            resultat.setText("Erreur " + reponse.code() + " : "
                                    + lireMessage(reponse));
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenReponse> call, Throwable t) {
                        Log.e(TAG, "Échec : " + t.getMessage());
                        resultat.setText("Serveur injoignable : " + t.getMessage());
                    }
                });
    }

    // Temps 2 : on présente le jeton dans l'en-tête Authorization
    private void chargerProfil(String jeton) {
        String entete = "Bearer " + jeton;
        ClientApi.get().monProfil(entete).enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(Call<Profil> call, Response<Profil> reponse) {
                Log.d(TAG, "api/moi : code " + reponse.code());
                if (reponse.isSuccessful() && reponse.body() != null) {
                    Profil p = reponse.body();
                    resultat.setText("Connecté !\n"
                            + "Nom : " + p.nom + "\n"
                            + "Rôle : " + p.role + "\n"
                            + "Courriel : " + p.courriel + "\n"
                            + "Code HTTP (api/moi) : " + reponse.code());
                } else {
                    resultat.setText("api/moi : erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(Call<Profil> call, Throwable t) {
                Log.e(TAG, "Échec : " + t.getMessage());
                resultat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    // Lit le champ "message" du JSON d'erreur, sans écrire de classe
    private String lireMessage(Response<?> reponse) {
        try {
            String texte = reponse.errorBody().string();
            Log.d(TAG, "errorBody : " + texte);
            JsonObject json = new Gson().fromJson(texte, JsonObject.class);
            return json.get("message").getAsString();
        } catch (Exception e) {
            return "(message illisible)";
        }
    }
}
