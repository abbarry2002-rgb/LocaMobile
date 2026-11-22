package ca.cuniversel.locamobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

import ca.cuniversel.locamobile.reseau.ClientApi;
import ca.cuniversel.locamobile.reseau.Logement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListeActivity extends AppCompatActivity {

    private static final String TAG = "LocaMobile";

    private LinearLayout conteneurLogements;
    private TextView messageErreur;
    private EditText champVille;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        conteneurLogements = findViewById(R.id.conteneurLogements);
        messageErreur = findViewById(R.id.messageErreur);
        champVille = findViewById(R.id.champVille);

        // Exercice 3 : bouton Chercher
        findViewById(R.id.boutonChercher).setOnClickListener(v -> chargerLogements());

        // Exercice 4 : ouvrir l'écran de connexion
        findViewById(R.id.boutonConnexion).setOnClickListener(v ->
                startActivity(new Intent(this, ConnexionActivity.class)));
    }

    // Exercice 2, étape 7 : recharger chaque fois qu'on revient sur cet écran
    @Override
    protected void onResume() {
        super.onResume();
        chargerLogements();
    }

    // Exercice 3, étape 8 : champ vide → liste complète, sinon → recherche par ville
    private void chargerLogements() {
        final String ville = champVille.getText().toString().trim();

        Call<List<Logement>> appel = ville.isEmpty()
                ? ClientApi.get().listerLogements()
                : ClientApi.get().chercherParVille(ville);

        // L'URL réellement envoyée (utile pour le tableau du rapport)
        Log.d(TAG, "URL : " + appel.request().url());

        Log.d(TAG, "1");
        appel.enqueue(new Callback<List<Logement>>() {
            @Override
            public void onResponse(Call<List<Logement>> call, Response<List<Logement>> reponse) {
                Log.d(TAG, "3");
                // onResponse ne veut pas dire « succès » : un 404 ou un 500 arrivent aussi ici
                if (reponse.isSuccessful() && reponse.body() != null) {
                    List<Logement> logements = reponse.body();
                    Log.d(TAG, "code " + reponse.code() + ", " + logements.size() + " logements");
                    if (logements.isEmpty()) {
                        afficherErreur("Aucun logement à " + ville + ".");
                    } else {
                        afficher(logements);
                    }
                } else {
                    Log.d(TAG, "code " + reponse.code());
                    afficherErreur("Erreur HTTP " + reponse.code());
                }
            }

            @Override
            public void onFailure(Call<List<Logement>> call, Throwable t) {
                Log.e(TAG, "Échec : " + t.getMessage());
                afficherErreur("Serveur injoignable : " + t.getMessage());
            }
        });
        Log.d(TAG, "2");
    }

    // Exercice 2, étape 3 : une carte par logement reçu
    private void afficher(List<Logement> logements) {
        messageErreur.setVisibility(View.GONE);
        conteneurLogements.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Logement l : logements) {
            // « Gonfler » le modèle item_logement pour obtenir une vraie vue
            View carte = inflater.inflate(R.layout.item_logement, conteneurLogements, false);

            ((TextView) carte.findViewById(R.id.carteTitre)).setText(l.titre);
            ((TextView) carte.findViewById(R.id.carteVille)).setText(l.ville);
            ((TextView) carte.findViewById(R.id.cartePrix)).setText(
                    String.format(Locale.CANADA_FRENCH, "%.2f $ / nuit", l.prixParNuit));

            // Exercice 3, étape 3 : un seul extra, l'id
            carte.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.CLE_ID, l.id);
                startActivity(intent);
            });

            conteneurLogements.addView(carte);
        }
    }

    // Exercice 2, étape 5
    private void afficherErreur(String message) {
        conteneurLogements.removeAllViews();
        messageErreur.setText(message);
        messageErreur.setVisibility(View.VISIBLE);
    }
}
