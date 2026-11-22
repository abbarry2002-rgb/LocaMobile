package ca.cuniversel.locamobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Exercice 3 : la fiche ne reçoit plus que l'id et demande les détails au serveur.
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";
    public static final String CLE_ID = "id";

    private TextView etat, titre, ville, prix, voyageurs, proprietaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        etat = findViewById(R.id.etat);
        titre = findViewById(R.id.detailTitre);
        ville = findViewById(R.id.detailVille);
        prix = findViewById(R.id.detailPrix);
        voyageurs = findViewById(R.id.detailVoyageurs);
        proprietaire = findViewById(R.id.detailProprietaire);

        int id = getIntent().getIntExtra(CLE_ID, -1);

        Call<Logement> appel = ClientApi.get().trouverLogement(id);
        Log.d(TAG, "URL : " + appel.request().url());

        appel.enqueue(new Callback<Logement>() {
            @Override
            public void onResponse(Call<Logement> call, Response<Logement> reponse) {
                Log.d(TAG, "fiche : code " + reponse.code() + ", body = " + reponse.body());
                if (reponse.isSuccessful() && reponse.body() != null) {
                    afficher(reponse.body());
                } else if (reponse.code() == 404) {
                    // Un 404 n'est pas une panne : le serveur a répondu « introuvable »
                    etat.setText("Ce logement n'existe plus (404).");
                } else {
                    etat.setText("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(Call<Logement> call, Throwable t) {
                Log.e(TAG, "Échec : " + t.getMessage());
                etat.setText("Serveur injoignable : " + t.getMessage());
            }
        });
    }

    private void afficher(Logement l) {
        etat.setVisibility(View.GONE);
        titre.setText(l.titre);
        ville.setText(l.ville);
        prix.setText(String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));
        voyageurs.setText("Voyageurs : " + l.nbVoyageurs);
        proprietaire.setText("Propriétaire : " + l.proprietaireNom);
    }
}
