package fr.cned.emdsgil.suividevosfrais.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.util.Hashtable;

import fr.cned.emdsgil.suividevosfrais.R;
import fr.cned.emdsgil.suividevosfrais.Utils.API;
import fr.cned.emdsgil.suividevosfrais.Utils.APIOperations;
import fr.cned.emdsgil.suividevosfrais.Utils.Global;
import fr.cned.emdsgil.suividevosfrais.Utils.Serializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité de connexion à GSB (uniquement pour les visiteurs médicaux)
 * @author xn1a
 */
public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setTitle("GSB :  Connexion");
        recupIdentifiants();
        cmdConnexion_clic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        if (Global.identifiants != null && Global.identifiants.size() > 0) {
            inflater.inflate(R.menu.menu_actions, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.retour_accueil))) {
            retourActivityPrincipale() ;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Quand on clique sur le bouton de connexion : on tante de connecter le visiteur
     * à la base de données distante de GSB
     */
    private void cmdConnexion_clic() {
        findViewById(R.id.cmdConnexion).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final String login = ((TextInputEditText)findViewById(R.id.txtLogin)).getText().toString();
                final String mdp = ((TextInputEditText)findViewById(R.id.txtMdp)).getText().toString();

                if (login.isEmpty() || mdp.isEmpty()) {
                    Toast.makeText(AuthActivity.this, "Merci de renseigner tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }
                testerConnexion(login, mdp);
            }
        });
    }

    /**
     * Vérifie si il y a déjà des identifiants enregistrés
     */
    private void recupIdentifiants() {
        Hashtable<?, ?> monHash = (Hashtable<?, ?>) Serializer.deSerialize(AuthActivity.this, Global.idFileName);
        if (monHash != null) {
            Hashtable<String, String> monHashCast = new Hashtable<>();
            for (Hashtable.Entry<?, ?> entry : monHash.entrySet()) {
                monHashCast.put((String) entry.getKey(), (String) entry.getValue());
            }
            Global.identifiants = monHashCast;
        }
        // Si on a récupéré quelque chose on valorise le champ login
        if (Global.identifiants != null && Global.identifiants.size() > 0) {
            valoriseLogin(Global.identifiants.get("login"));
        }
    }

    /**
     * Retour à l'activité principale (le menu)
     */
    private void retourActivityPrincipale() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class) ;
        startActivity(intent) ;
    }

    /**
     * On affiche le login récupéré (si il y en a) dans le champ login
     * @param login Le nom d'utilisateur du visiteur actuellement connecté à GSB
     */
    private void valoriseLogin(String login) {
        ((TextInputEditText) findViewById(R.id.txtLogin)).setText(login);
    }

    /**
     * Teste la connexion d'un utilisateur à GSB avec les identifiants renseignés
     * @param login
     * @param mdp
     */
    private void testerConnexion(final String login, final String mdp) {
        // Envoie de la requete à l'API GSB
        APIOperations api = API.getAPI();
        Call<JsonElement> testerConnexion = api.testerConnexion(login, mdp);

        // Réception de la réponse
        testerConnexion.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement>call, Response<JsonElement> response) {
                Boolean estConnecte = Boolean.valueOf(response.body().getAsJsonObject().get("estConnecte").toString());
                if (estConnecte) {
                    // Serialization des identifiants
                    Global.identifiants.put("login", login);
                    Global.identifiants.put("mdp", mdp);
                    Serializer.serialize(Global.identifiants, AuthActivity.this, Global.idFileName) ;
                    retourActivityPrincipale();
                }
                else {
                    // Affichage du potentiel message d'erreur
                    String message = String.valueOf(response.body().getAsJsonObject().get("message"));
                    Toast.makeText(AuthActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<JsonElement>call, Throwable t) {
                System.out.println(t.toString());
                Toast.makeText(AuthActivity.this, "Le serveur est actuellement indisponible", Toast.LENGTH_LONG).show();
            }
        });
    }
}
