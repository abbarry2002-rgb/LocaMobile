package ca.cuniversel.locamobile.reseau;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Singleton : Retrofit n'est construit qu'une seule fois.
public class ClientApi {

    // 10.0.2.2 = l'ordinateur hôte, vu depuis l'émulateur
    private static final String URL_BASE = "http://10.0.2.2:8080/";

    private static LocaMobileApi api;

    public static LocaMobileApi get() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(LocaMobileApi.class);
        }
        return api;
    }
}
