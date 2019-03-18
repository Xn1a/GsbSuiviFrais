package fr.cned.emdsgil.suividevosfrais.Utils;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
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

    @Test
    public void testerConnexion() {

        final int LOGIN = 0;
        final int MDP = 1;
        final int ERREUR = 2;
        final int MESSAGE = 3;
        final int EST_CONNECTE = 4;

        // Paramètres à tester
        ArrayList<Object> idCorrectes = new ArrayList<>();
        idCorrectes.add("dandre");
        idCorrectes.add("oppg5");
        idCorrectes.add(false);
        idCorrectes.add("null");
        idCorrectes.add(true);
        ArrayList<Object> idIncorrectes = new ArrayList<>();
        idIncorrectes.add("dandre");
        idIncorrectes.add("mauvaismdp");
        idIncorrectes.add(true);
        idIncorrectes.add("\"Identifiants incorrectes\"");
        idIncorrectes.add(false);
        ArrayList<Object> majusculeLogin = new ArrayList<>();
        majusculeLogin.add("Dandre");
        majusculeLogin.add("oppg5");
        majusculeLogin.add(false);
        majusculeLogin.add("null");
        majusculeLogin.add(true);
        ArrayList<Object> champsManquants = new ArrayList<>();
        champsManquants.add("");
        champsManquants.add("");
        champsManquants.add(true);
        champsManquants.add("\"Vous n'etes pas autorisé à accéder à ce serveur\"");
        champsManquants.add(false);
        ArrayList<Object> loginManquant = new ArrayList<>();
        loginManquant.add("");
        loginManquant.add("oppg5");
        loginManquant.add(true);
        loginManquant.add("\"Vous n'etes pas autorisé à accéder à ce serveur\"");
        loginManquant.add(false);
        ArrayList<Object> mdpManquant = new ArrayList<>();
        mdpManquant.add("dandre");
        mdpManquant.add("");
        mdpManquant.add(true);
        mdpManquant.add("\"Vous n'etes pas autorisé à accéder à ce serveur\"");
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

            try {
                JsonObject body = testerConnexion.execute().body().getAsJsonObject();

                System.out.println("ASSERT ERREUR");
                Boolean erreur = Boolean.valueOf(body.get("erreur").toString());
                assertEquals(erreurAttendue, erreur);

                System.out.println("ASSERT MESSAGE");
                String message = String.valueOf(body.getAsJsonObject().get("message"));
                assertEquals(messageAttendu, message);

                System.out.println("ASSERT EST_CONNECTE");
                Boolean estConnecte = Boolean.valueOf(body.getAsJsonObject().get("estConnecte").toString());
                assertEquals(estConnecteAttendu, estConnecte);
            } catch (IOException e) {
                System.out.println(e.toString());
                assertNotEquals("Failed to connect to /192.168.1.23:80", e.getMessage());
                fail("Une erreur est survenue lors de l'envoie de la requete au serveur");
            }
        }
    }

    @Test
    public void creerFraisHf() {

        final int ERREUR = 0;
        final int MESSAGE = 1;
        final int MOIS = 2;
        final int LIBELLE = 3;
        final int DATE = 4;
        final int MONTANT = 5;

        // Paramètres à tester
        ArrayList<Object> fraisCorrecte = new ArrayList<>();
        fraisCorrecte.add(false);
        fraisCorrecte.add("null");
        fraisCorrecte.add("201805");
        fraisCorrecte.add("Un frais hors forfait à ajouter");
        fraisCorrecte.add("11/05/2018");
        fraisCorrecte.add(Float.valueOf("13.56"));

        ArrayList<Object> champsManquants = new ArrayList<>();
        champsManquants.add(true);
        champsManquants.add("\"Certains champs sont manquants\"");
        champsManquants.add("");
        champsManquants.add("Un frais hors forfait à ajouter mais avec un champs manquant");
        champsManquants.add("11/10/2018");
        champsManquants.add(Float.valueOf("130.70"));

        ArrayList<ArrayList<Object>> lesParametres = new ArrayList<>();
        lesParametres.add(fraisCorrecte);
        lesParametres.add(champsManquants);

        for (ArrayList params : lesParametres) {
            System.out.println("Test n°" + String.valueOf(lesParametres.indexOf(params)));

            // On récupère les différents paramètres
            final String mois = (String) params.get(MOIS);
            final String libelle = (String) params.get(LIBELLE);
            final Boolean erreurAttendue = (Boolean) params.get(ERREUR);
            final String messageAttendu = (String) params.get(MESSAGE);
            final String date = (String) params.get(DATE);
            final Float montant = (Float) params.get(MONTANT);

            APIOperations api = API.getAPI();
            Call<JsonElement> creerFraisHf = api.creerFraisHf("dandre", "oppg5", mois, libelle, date, montant);

            try {
                JsonObject body = creerFraisHf.execute().body().getAsJsonObject();

                System.out.println("ASSERT ERREUR");
                Boolean erreur = Boolean.valueOf(body.getAsJsonObject().get("erreur").toString());
                assertEquals(erreurAttendue, erreur);

                System.out.println("ASSERT MESSAGE");
                String message = String.valueOf(body.getAsJsonObject().get("message"));
                assertEquals(messageAttendu, message);

            } catch (IOException e) {
                System.out.println(e.toString());
                assertNotEquals("Failed to connect to /192.168.1.23:80", e.getMessage());
                fail("Une erreur est survenue lors de l'envoie de la requete au serveur");
            }
        }

    }
}