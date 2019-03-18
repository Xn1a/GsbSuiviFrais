package fr.cned.emdsgil.suividevosfrais.Models;

import java.io.Serializable;

/**
 * Classe métier contenant la description d'un frais hors forfait
 *
 */
public class FraisHf  implements Serializable {

	private final Float montant ;
	private final String motif ;
	private final Integer jour ;
	private boolean estSync = false; // Vrai si le frais a déjà été envoyé dans la BD distante, sinon Faux

	public FraisHf(Float montant, String motif, Integer jour) {
		this.montant = montant ;
		this.motif = motif ;
		this.jour = jour ;
	}

	public Float getMontant() {
		return montant;
	}

	public String getMotif() {
		return motif;
	}

	public Integer getJour() {
		return jour;
	}

    public boolean estSync() {
        return estSync;
    }

    public void setEstSync(boolean estSync) {
        this.estSync = estSync;
    }
}
