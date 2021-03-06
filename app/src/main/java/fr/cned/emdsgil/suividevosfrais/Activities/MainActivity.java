package fr.cned.emdsgil.suividevosfrais.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import fr.cned.emdsgil.suividevosfrais.Models.FraisHf;
import fr.cned.emdsgil.suividevosfrais.Models.FraisMois;
import fr.cned.emdsgil.suividevosfrais.Utils.API;
import fr.cned.emdsgil.suividevosfrais.Utils.APIOperations;
import fr.cned.emdsgil.suividevosfrais.Utils.Global;
import fr.cned.emdsgil.suividevosfrais.Utils.Serializer;
import fr.cned.emdsgil.suividevosfrais.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String login; // Le nom d'utilisateur du visiteur connecté
    private String mdp; // Le mot de passe du visiteur connecté

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("GSB : Suivi des frais");
        // récupération des identifiants du visiteur connecté (si il y en a)
        recupIdentifiants();
        // récupération des informations sérialisées
        recupSerialize();
        // chargement des méthodes événementielles
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdKm)), KmActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdEtape)), ForfaitEtapeActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdNuitee)), NuiteeHotelActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdRepas)), RepasRestaurantActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdHf)), HfActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdHfRecap)), HfRecapActivity.class);
        cmdTransfert_clic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_compte) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Récupère la sérialisation des identifiants du visiteur si elle existe
     */
    private void recupIdentifiants() {
        Hashtable<?, ?> monHash = (Hashtable<?, ?>) Serializer.deSerialize(MainActivity.this, Global.idFileName);
        if (monHash != null) {
            Hashtable<String, String> monHashCast = new Hashtable<>();
            for (Hashtable.Entry<?, ?> entry : monHash.entrySet()) {
                monHashCast.put((String) entry.getKey(), (String) entry.getValue());
            }
            Global.identifiants = monHashCast;
        }
        // Si rien n'a été récupéré, on renvoie vers l'activité d'authentification
        if (Global.identifiants == null || Global.identifiants.size() == 0) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            finish();
            startActivity(intent);
        } else {
          this.login = Global.identifiants.get("login");
          this.mdp = Global.identifiants.get("mdp");
        }

    }

    /**
     * Récupère la sérialisation si elle existe
     */
    private void recupSerialize() {
        /* Pour éviter le warning "Unchecked cast from Object to Hash" produit par un casting direct :
         * Global.listFraisMois = (Hashtable<Integer, FraisMois>) Serializer.deSerialize(Global.filename, MainActivity.this);
         * On créé un Hashtable générique <?,?> dans lequel on récupère l'Object retourné par la méthode deSerialize, puis
         * on cast chaque valeur dans le type attendu.
         * Seulement ensuite on affecte cet Hastable à Global.listFraisMois.
        */
        Hashtable<?, ?> monHash = (Hashtable<?, ?>) Serializer.deSerialize(MainActivity.this, Global.filename);
        if (monHash != null) {
            Hashtable<Integer, FraisMois> monHashCast = new Hashtable<>();
            for (Hashtable.Entry<?, ?> entry : monHash.entrySet()) {
                monHashCast.put((Integer) entry.getKey(), (FraisMois) entry.getValue());
            }
            Global.listFraisMois = monHashCast;
        }
        // si rien n'a été récupéré, il faut créer la liste
        if (Global.listFraisMois == null) {
            Global.listFraisMois = new Hashtable<>();
            /* Retrait du type de l'HashTable (Optimisation Android Studio)
			 * Original : Typage explicit =
			 * Global.listFraisMois = new Hashtable<Integer, FraisMois>();
			*/
        }
    }

    /**
     * Sur la sélection d'un bouton dans l'activité principale ouverture de l'activité correspondante
     */
    private void cmdMenu_clic(ImageButton button, final Class classe) {
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // ouvre l'activité
                Intent intent = new Intent(MainActivity.this, classe);
                startActivity(intent);
            }
        });
    }

    /**
     * Cas particulier du bouton pour le transfert d'informations vers le serveur
     */
    private void cmdTransfert_clic() {
        findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                afficherDialogConfirmation();
            }
        });
    }

    /**
     * Affiche la boite de dialogue demandant confirmation avant de synchroniser les données
     */
    private void afficherDialogConfirmation() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.sync_dialog_titre));
        alertDialog.setMessage(getString(R.string.sync_dialog_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.sync_dialog_positive),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        synchroniseFrais();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.sync_dialog_negative),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Envoie les frais dans la base de donnée distante
     */
    private void synchroniseFrais() {
        if (Global.listFraisMois != null && Global.listFraisMois.size() > 0) {
            Set<Integer> lesMois = Global.listFraisMois.keySet();
            int nbFraisSync = 0; // Le nombre de frais qui ont été synchronisés durant cette procédure
            for(int leMois : lesMois) {
                final FraisMois fraisMois = Global.listFraisMois.get(leMois);

                // Pour chaque frais hors forfait du fraisMois
                for(final FraisHf fraisHf : fraisMois.getLesFraisHf()) {
                    // Si le frais n'a jamais été synchronisé
                    if (!fraisHf.estSync()) {
                        nbFraisSync++;

                        // Envoie du frais à la base de données distante
                        APIOperations api = API.getAPI();
                        String date = String.valueOf(fraisHf.getJour()) + '/' + fraisMois.getMois()
                                + '/' + fraisMois.getAnnee();
                        Call<JsonElement> creerFraisHf = api.creerFraisHf(this.login, this.mdp, String.valueOf(leMois), fraisHf.getMotif()
                                , date, fraisHf.getMontant());

                        // Réception de la reponse
                        creerFraisHf.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                                assert response.body() != null;
                                JsonObject reponse = response.body().getAsJsonObject();
                                // Affiche les erreurs si il y en a
                                Boolean erreur = Boolean.valueOf(reponse.get("erreur").toString());
                                if (erreur) {
                                    String message = String.valueOf(reponse.get("message"));
                                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Toast.makeText(MainActivity.this, "Les frais ont bien été synchronisés", Toast.LENGTH_LONG).show();
                                marquerFraisHfSync(fraisMois, fraisHf);
                            }
                            @Override
                            public void onFailure(Call<JsonElement> call, Throwable t) {
                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                // Si il y des modifications des frais forfait du mois qui n'ont pas encore été sync
                if (fraisMois.getLesFraisForfaitModifies().size() > 0) {
                    nbFraisSync+=fraisMois.getLesFraisForfaitModifies().size();

                    // Les frais forfait qui n'ont pas été modifiés sont à -1. Ainsi l'API sera que
                    // pour ces frais elle devra récupérer les valeur déjà présentes dans la BD
                    Integer qteEtape = -1;
                    Integer qteRepas = -1;
                    Integer qteNuitee = -1;
                    Integer qteKm = -1;

                    if (fraisMois.getLesFraisForfaitModifies().contains("ETP")) {
                        qteEtape = fraisMois.getEtape();
                    }
                    if (fraisMois.getLesFraisForfaitModifies().contains("REP")) {
                        qteRepas = fraisMois.getRepas();
                    }
                    if (fraisMois.getLesFraisForfaitModifies().contains("NUI")) {
                        qteNuitee = fraisMois.getNuitee();
                    }
                    if (fraisMois.getLesFraisForfaitModifies().contains("KM")) {
                        qteKm = fraisMois.getKm();
                    }

                    // Envoie des frais forfait à la BD distante
                    APIOperations api = API.getAPI();
                    Call<JsonElement> creerFraisForfait = api.creerFraisForfait(this.login, this.mdp
                            , String.valueOf(leMois), qteEtape, qteRepas, qteNuitee, qteKm);

                    // Réception de la réponse
                    creerFraisForfait.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            JsonObject reponse = response.body().getAsJsonObject();
                            // Affiche les erreurs si il y en a
                            Boolean erreur = Boolean.valueOf(reponse.get("erreur").toString());
                            if (erreur) {
                                String message = String.valueOf(reponse.get("message"));
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                                return;
                            }
                            Toast.makeText(MainActivity.this, "Les frais ont bien été synchronisés", Toast.LENGTH_LONG).show();
                            marquerFraisForfaitSync(fraisMois);
                        }
                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            // Si aucun frais n'a été synchronisé
            if (nbFraisSync == 0) {
                Toast.makeText(MainActivity.this, "Il n'y a pas de frais à synchroniser", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Il n'y a pas de frais à synchroniser", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Marque un frais hors forfait comme ayant été synchronisé
     * @param leMois Le mois / la fiche auquel(le) appartient le frais
     * @param fraisHf Le frais hors forfait qui a été synchronisé
     */
    private void marquerFraisHfSync(FraisMois leMois, FraisHf fraisHf) {
        int indexFrais = leMois.getLesFraisHf().indexOf(fraisHf);
        ArrayList<FraisHf> lesFraisHf = Global.listFraisMois.get(leMois.getAnnee()*100 + leMois.getMois()).getLesFraisHf();
        lesFraisHf.get(indexFrais).setEstSync(true);
        Serializer.serialize(Global.listFraisMois, MainActivity.this, Global.filename);
    }

    /**
     * Marque les frais forfait du mois comme ayant été synchronisés
     * @param leMois Le mois / la fiche auquel(le) appartiennent les frais forfait
     */
    private void marquerFraisForfaitSync(FraisMois leMois) {
        FraisMois fraisMois = Global.listFraisMois.get(leMois.getAnnee()*100 + leMois.getMois());
        fraisMois.getLesFraisForfaitModifies().clear();
        Serializer.serialize(Global.listFraisMois, MainActivity.this, Global.filename);
    }
}
