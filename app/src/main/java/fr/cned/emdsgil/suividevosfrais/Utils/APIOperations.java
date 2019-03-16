package fr.cned.emdsgil.suividevosfrais.Utils;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIOperations {
    @GET("?operation="+API.OP_TESTER_CONNEXION)
    Call<JsonElement> testerConnexion(@Query("login") String login, @Query("mdp") String mdp);

    @FormUrlEncoded
    @POST("?operation="+API.OP_CREER_FRAIS_HF)
    Call<JsonElement> creerFraisHf(@Query("login") String login, @Query("mdp") String mdp
            , @Field("mois") String mois
            , @Field("libelle") String libelle
            , @Field("date") String date
            , @Field("montant") Float montant
    );
}
