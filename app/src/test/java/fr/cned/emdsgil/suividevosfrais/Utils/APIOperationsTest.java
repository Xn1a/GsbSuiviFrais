package fr.cned.emdsgil.suividevosfrais.Utils;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.cned.emdsgil.suividevosfrais.Activities.AuthActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.junit.Assert.*;

public class APIOperationsTest {

    private static final int LOGIN = 0;
    private static final int MDP = 1;
    private static final int ERREUR = 2;
    private static final int MESSAGE = 3;
    private static final int EST_CONNECTE = 4;

    @Test
    public void testerConnexion() {
        // Paramètres à tester
        ArrayList<Object> idCorrectes = new ArrayList<>();
        idCorrectes.add("dandre");
        idCorrectes.add("oppg5");
        idCorrectes.add(false);
        idCorrectes.add(null);
        idCorrectes.add(true);
        ArrayList<Object> idIncorrectes = new ArrayList<>();
        idIncorrectes.add("dandre");
        idIncorrectes.add("mauvaismdp");
        idIncorrectes.add(true);
        idIncorrectes.add("Indentifiants incorrectes");
        idIncorrectes.add(false);
        ArrayList<Object> majusculeLogin = new ArrayList<>();
        majusculeLogin.add("Dandre");
        majusculeLogin.add("oppg5");
        majusculeLogin.add(false);
        majusculeLogin.add(null);
        majusculeLogin.add(true);
        ArrayList<Object> champsManquants = new ArrayList<>();
        champsManquants.add("");
        champsManquants.add("");
        champsManquants.add(true);
        champsManquants.add("Certain champs sont manquants");
        champsManquants.add(false);
        ArrayList<Object> loginManquant = new ArrayList<>();
        loginManquant.add("");
        loginManquant.add("oppg5");
        loginManquant.add(true);
        loginManquant.add("Certain champs sont manquants");
        loginManquant.add(false);
        ArrayList<Object> mdpManquant = new ArrayList<>();
        mdpManquant.add("dandre");
        mdpManquant.add("");
        mdpManquant.add(true);
        mdpManquant.add("Certain champs sont manquants");
        mdpManquant.add(false);

        ArrayList<ArrayList<Object>> lesParametres = new ArrayList<>();
        lesParametres.add(idCorrectes);
        lesParametres.add(idIncorrectes);
        lesParametres.add(majusculeLogin);
        lesParametres.add(champsManquants);
        lesParametres.add(loginManquant);
        lesParametres.add(mdpManquant);

        for (ArrayList params : lesParametres) {
            System.out.println("Test n°" + String.valueOf(lesParametres.indexOf(params)));

            // On récupère les différents paramètres
            final String mdp = (String) params.get(MDP);
            final String login = (String) params.get(LOGIN);
            final Boolean erreurAttendue = (Boolean) params.get(ERREUR);
            final String messageAttendu = (String) params.get(MESSAGE);
            final Boolean estConnecteAttendu = (Boolean) params.get(EST_CONNECTE);

            // Envoie d'une requete à l'API GSB pour tester les identifiants
            APIOperations api = API.getAPI();
            Call<JsonElement> testerConnexion = api.testerConnexion(login, mdp);

            // Réception de la réponse
            testerConnexion.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement>call, Response<JsonElement> response) {
                    assert response.body() != null;
                    Boolean erreur = Boolean.valueOf(response.body().getAsJsonObject().get("erreur").toString());
                    assertEquals(erreurAttendue, erreur);

                    String message = String.valueOf(response.body().getAsJsonObject().get("message"));
                    assertEquals(messageAttendu, message);

                    Boolean estConnecte = Boolean.valueOf(response.body().getAsJsonObject().get("estConnecte").toString());
                    assertEquals(estConnecteAttendu, estConnecte);
                }
                @Override
                public void onFailure(Call<JsonElement>call, Throwable t) {
                    System.out.println(t.toString());
                    assertNotEquals("Failed to connect to /192.168.1.23:80", t.getMessage());
                    fail("Une erreur est survenue lors de l'envoie de la requete au serveur");
                }
            });
        }
    }
}