package fr.cned.emdsgil.suividevosfrais;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setTitle("GSB Suivi de vos frais :  Connexion");
        cmdConnexion_clic();
    }

    /**
     * Quand on clique sur le bouton de connexion : on tante de connecter le visiteur
     * à la base de donnée distante de GSB
     */
    private void cmdConnexion_clic() {
        String nomUtilisateur = String.valueOf(((EditText)findViewById(R.id.txtUtilisateur)).getText());
        String mdp = String.valueOf(((EditText)findViewById(R.id.txtMdp)).getText());

        findViewById(R.id.cmdConnexion).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // TODO: try connection
                //Serializer.serialize(Global.listFraisMois, KmActivity.this, Global.filename) ;
               // retourActivityPrincipale();
            }
        });
    }

    /**
     * Retour à l'activité principale (le menu)
     */
    private void retourActivityPrincipale() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class) ;
        startActivity(intent) ;
    }
}
