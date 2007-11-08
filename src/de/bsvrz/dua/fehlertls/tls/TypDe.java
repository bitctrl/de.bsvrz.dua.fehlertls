package de.bsvrz.dua.fehlertls.tls;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.de.DeTypLader;
import de.bsvrz.dua.fehlertls.enums.TlsDeFehlerStatus;
import de.bsvrz.dua.fehlertls.online.ITlsGloDeFehlerListener;
import de.bsvrz.dua.fehlertls.online.TlsGloDeFehler;
import de.bsvrz.dua.fehlertls.parameter.IZyklusSteuerungsParameterListener;
import de.bsvrz.dua.fehlertls.parameter.ZyklusSteuerungsParameter;

/**
 * 
 * 
 * 
 * @author Thierfelder
 *
 */
public class TypDe
implements ClientReceiverInterface, 
		   ITlsGloDeFehlerListener,
		   IZyklusSteuerungsParameterListener{
	
	//private long letztes
	
	/**
	 * zeigt an, ob dieses DE erfasst ist
	 */
	private boolean erfasst = false; 
	
	
	public TypDe(ClientDavInterface dav,
				 SystemObject obj)
	throws DeFaException{
		TlsGloDeFehler.getInstanz(dav, obj).addListener(this);
		ZyklusSteuerungsParameter.getInstanz(dav, obj).addListener(this);
		
		for(DataDescription messWertBeschreibung:DeTypLader.getDeTyp(obj.getType()).getDeFaMesswertDataDescriptions(dav)){
			dav.subscribeReceiver(this, obj, messWertBeschreibung,
					ReceiveOptions.normal(), ReceiverRole.receiver());
		}		
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] erwarteteResultate) {
		if(erwarteteResultate != null){
			for(ResultData erwartetesResultat:erwarteteResultate){
				if(erwartetesResultat != null){
					
				}
			}
		}
	}

	
	/**
	 * {@inheritDoc}Nfyjlp Mexik
	 */
	public void aktualisiereTlsGloDeFehler(boolean aktiv,
										   TlsDeFehlerStatus deFehlerStatus) {
		
	}


	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereZyklusSteuerungsParameter(
			long erfassungsIntervallDauer) {
	}
	
	
	/**
	 * Erfragt den Erfassungszustand dieses DE bezueglich der DeFa. Dieser
	 * Zustand kann die Werte Erfasst und Nicht erfasst annehmen. Der Zustand
	 * Erfasst wird angenommen wenn für dieses DE gilt:<br>
	 * 1.) es liegt aktuell kein DE-Fehler vor,<br>
	 * 2.) der DE-Kanalstatus hat den Wert <code>aktiv</code> und<br>
	 * 3.) die Erfassungsart ist auf <code>Zyklische Abgabe von Meldungen</code> gesetzt.<br>
	 * Sonst wird der Wert Nicht erfasst angenommen
	 * 
	 * @return der Erfassungszustand dieses DE bezueglich der DeFa
	 */
	public final boolean isErfasst(){
		return this.erfasst;
	}
	
}
