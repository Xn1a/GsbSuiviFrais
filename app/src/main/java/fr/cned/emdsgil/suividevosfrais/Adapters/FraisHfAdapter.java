package fr.cned.emdsgil.suividevosfrais.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;

import fr.cned.emdsgil.suividevosfrais.Models.FraisHf;
import fr.cned.emdsgil.suividevosfrais.Models.FraisMois;
import fr.cned.emdsgil.suividevosfrais.R;
import fr.cned.emdsgil.suividevosfrais.Utils.Global;
import fr.cned.emdsgil.suividevosfrais.Utils.Serializer;

public class FraisHfAdapter extends BaseAdapter {

	private final ArrayList<FraisHf> lesFrais ; // liste des frais du mois
	private final LayoutInflater inflater ;
	private final FraisMois fraisMois;
	private final Context context;
	private Hashtable<Integer, FraisMois> listFraisMois;

    /**
	 * Constructeur de l'adapter pour valoriser les propriétés
     * @param context Accès au contexte de l'application
     * @param lesFrais Liste des frais hors forfait
     */
	public FraisHfAdapter(Context context, ArrayList<FraisHf> lesFrais, FraisMois fraisMois, Hashtable listFraisMois) {
		this.context = context;
		inflater = LayoutInflater.from(context) ;
		this.lesFrais = lesFrais ;
		this.fraisMois = fraisMois;
		this.listFraisMois = listFraisMois;
    }
	
	/**
	 * retourne le nombre d'éléments de la listview
	 */
	@Override
	public int getCount() {
		return lesFrais.size() ;
	}

	/**
	 * retourne l'item de la listview à un index précis
	 */
	@Override
	public Object getItem(int index) {
		return lesFrais.get(index) ;
	}

	/**
	 * retourne l'index de l'élément actuel
	 */
	@Override
	public long getItemId(int index) {
		return index;
	}

	/**
	 * structure contenant les éléments d'une ligne
	 */
	private class ViewHolder {
		TextView txtListJour ;
		TextView txtListMontant ;
		TextView txtListMotif ;
		ImageButton btnListSupp;
	}
	
	/**
	 * Affichage dans la liste
	 */
	@Override
	public View getView(final int index, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder() ;
			convertView = inflater.inflate(R.layout.layout_liste, parent, false) ;
			holder.txtListJour = convertView.findViewById(R.id.txtListJour);
			holder.txtListMontant = convertView.findViewById(R.id.txtListMontant);
			holder.txtListMotif = convertView.findViewById(R.id.txtListMotif);
			holder.btnListSupp = convertView.findViewById(R.id.cmdSuppHf);
			convertView.setTag(holder) ;
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.txtListJour.setText(String.format(Locale.FRANCE, "%d", lesFrais.get(index).getJour()));
		holder.txtListMontant.setText(String.format(Locale.FRANCE, "%.2f", lesFrais.get(index).getMontant())) ;
		holder.txtListMotif.setText(lesFrais.get(index).getMotif()) ;
		holder.btnListSupp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                supprimerFrais(index);
			}
		});
		return convertView ;
	}

    /**
     * Supprime un frais de la ListView et du fichier
     *
     * @param index L'index du frais à supprimer frais de la liste
     */
    private void supprimerFrais(int index) {
        // Suppression du frais de la ListView
        lesFrais.remove(index);
        notifyDataSetChanged();
        // Suppression du frais dans le fichier
        listFraisMois.get(fraisMois.getAnnee()*100 + fraisMois.getMois()).supprFraisHf(index);
        Serializer.serialize(listFraisMois, context, Global.filename);
    }
	
}
