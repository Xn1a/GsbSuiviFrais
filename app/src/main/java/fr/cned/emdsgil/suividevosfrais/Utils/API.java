package fr.cned.emdsgil.suividevosfrais.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    public static final String API_URL = "http://192.168.1.23/GSB_API/API.php/";
    public static final String OP_TESTER_CONNEXION = "testerConnexion";
    public static final String OP_CREER_FRAIS_HF = "creerFraisHf";
    public static final String OP_CREER_FRAIS_FORFAIT = "creerFraisForfait";

    private static APIOperations instance = null;

    private API() {
    }

    public static APIOperations getAPI() {
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(APIOperations.class);
        }
        return instance;
    }
}
