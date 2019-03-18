package fr.cned.emdsgil.suividevosfrais.Utils;

import com.google.gson.JsonElement;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface Retrofit contenant les différentes requetes pour l'API de GSB
 * @author xn1a
 */
public interface APIOperations {

    /**
     * Teste la connexion à GSB avec les identifiants donnés
     * @param login Le nom d'utilisateur
     * @param mdp Le mot de passe
     * @return Une réponse en JSon
     */
    @GET("?operation="+API.OP_TESTER_CONNEXION)
    Call<JsonElement> testerConnexion(@Query("login") String login, @Query("mdp") String mdp);

    /**
     * Crée un frais hors forfait
     * @param login Le nom d'utilisateur du visiteur connecté
     * @param mdp Le mot de passe de l'utilisateur connecté
     * @param mois Le mois pour lequel on ajoute le frais
     * @param libelle Le libelle du frais (la description)
     * @param date La date du frais
     * @param montant Le montant du frais
     * @return Une réponse en Json
     */
    @FormUrlEncoded
    @POST("?operation="+API.OP_CREER_FRAIS_HF)
    Call<JsonElement> creerFraisHf(@Query("login") String login, @Query("mdp") String mdp
            , @Field("mois") String mois
            , @Field("libelle") String libelle
            , @Field("date") String date
            , @Field("montant") Float montant
    );

    /**
     * Crée ou modifie les frais forfait du mois
     * @param login Le nom d'utilisateur du visiteur connecté
     * @param mdp Le mot de passe du visiteur connecté
     * @param mois Le mois auquel appartiennent les frais
     * @param qteEtape La quantité de frais de type Etape
     * @param qteRepas La quantité de frais de type Repas
     * @param qteNuitee La quantité de frais de type Nuiutée
     * @param qteKm La quantité de frais de type kilométrage
     * @return Une réponse en JSon
     */
    @FormUrlEncoded
    @POST("?operation="+API.OP_CREER_FRAIS_FORFAIT)
    Call<JsonElement> creerFraisForfait(@Query("login") String login, @Query("mdp") String mdp
            , @Field("mois") String mois
            , @Field("qteEtape") int qteEtape
            , @Field("qteRepas") int qteRepas
            , @Field("qteNuitee") int qteNuitee
            , @Field("qteKm") int qteKm
    );
}
