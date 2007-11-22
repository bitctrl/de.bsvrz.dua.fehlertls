/**
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * Contact Information:<br>
 * BitCtrl Systems GmbH<br>
 * Weißenfelser Straße 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.tls;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.enums.TlsDeFehlerStatus;
import de.bsvrz.dua.fehlertls.online.ITlsGloDeFehlerListener;
import de.bsvrz.dua.fehlertls.online.TlsGloDeFehler;
import de.bsvrz.dua.fehlertls.parameter.IZyklusSteuerungsParameterListener;
import de.bsvrz.dua.fehlertls.parameter.ZyklusSteuerungsParameter;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.bitctrl.konstante.Konstante;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Ueberwacht den Erfassungszustand eines DE bezueglich der DeFa. Dieser
 * Zustand kann die Werte <code>erfasst</code> und <code>nicht erfasst</code> annehmen
 *   
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class DeErfassungsZustand 
implements ITlsGloDeFehlerListener,
		   IZyklusSteuerungsParameterListener{
	
	/**
	 * Debug-Logger
	 */
	private static final Debug LOGGER = Debug.getLogger();
	
	protected static final String GRUND_PRAEFIX = "Keine TLS-Fehleranalyse moeglich. "; //$NON-NLS-1$
	
	/**
	 * indiziert, dass der TLS-Kanalstatus auf <code>aktiv</code> steht
	 */
	protected Boolean aktiv = null;
	
	
	/**
	 * TLS-DE-Fehler-Status
	 */
	protected TlsDeFehlerStatus deFehlerStatus = null;
	
	/**
	 * die entsprechende Erassungsintervalldauer (in ms), wenn das DE auf zyklischen
	 * Abruf parametriert ist und -1 sonst
	 */
	protected Long erfassungsIntervallDauer = null;
	
	/**
	 * aktueller Erfassungszustand bzgl. der DeFa des mit dieser Instanz
	 * assoziierten DE 
	 */
	protected Zustand aktuellerZustand = null;
	
	/**
	 * Menge aller Listener dieses Objektes
	 */
	private Set<IDeErfassungsZustandListener> listenerMenge = new HashSet<IDeErfassungsZustandListener>();
	
	/**
	 * das erfasste DE
	 */
	private SystemObject obj =  null;


	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Datenverteiler-Verbindung
	 * @param objekt ein durch diese Instanz zu ueberwachendes DE
	 * @throws DeFaException wird geworfen, wenn es Probleme beim Laden oder
	 * Instanziieren der Klasse gibt, die den erfragten DE-Typ beschreibt
	 */
	public DeErfassungsZustand(ClientDavInterface dav, SystemObject objekt)
	throws DeFaException{
		this.obj = objekt;
		this.aktuellerZustand = new Zustand();
		TlsGloDeFehler.getInstanz(dav, objekt).addListener(this);
		ZyklusSteuerungsParameter.getInstanz(dav, objekt).addListener(this);
		LOGGER.info("DeFa-Zustand von " + objekt + " wird ab sofort ueberwacht");  //$NON-NLS-1$//$NON-NLS-2$
	}


	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereTlsGloDeFehler(boolean aktiv,
			TlsDeFehlerStatus deFehlerStatus){
		synchronized (this) {
			this.aktiv = aktiv;
			this.deFehlerStatus = deFehlerStatus;
			informiereListener();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void aktualisiereZyklusSteuerungsParameter(long erfassungsIntervallDauer) {
		synchronized (this) {
			this.erfassungsIntervallDauer = erfassungsIntervallDauer;
			informiereListener();
		}		
	}
	
	
	/**
	 * Informiert alle Listener ueber eine Veraenderung des Erfassungszustandes
	 * dieses Objektes
	 */
	private final void informiereListener(){
		synchronized (this) {
			Zustand neuerZustand = new Zustand();
			if( !neuerZustand.equals(this.aktuellerZustand) ){
				this.aktuellerZustand = neuerZustand;
				for(IDeErfassungsZustandListener listener:this.listenerMenge){
					listener.aktualisiereErfassungsZustand(this.aktuellerZustand);
				}				
			}
		}				
	}
	
	
	/**
	 * Erfragt den Erfassungszustand des durch diese Instanz ueberwachten DE
	 * in Bezug auf die DeFa
	 * 
	 * @return der Erfassungszustand des durch diese Instanz ueberwachten DE
	 * in Bezug auf die DeFa
	 */
	public final DeErfassungsZustand.Zustand getZustand(){
		return new Zustand();
	}
	
	
	/**
	 * Fuegt diesem Objekt einen neuen Listener hinzu und informiert diesen sofort
	 * ueber den aktuellen Zustand dieses Objektes 
	 * 
	 * @param listener ein neuer Listener
	 */
	public final synchronized void addListener(IDeErfassungsZustandListener listener){
		if(this.listenerMenge.add(listener)){
			listener.aktualisiereErfassungsZustand(this.aktuellerZustand);
		}
	}
	
	
	/**
	 * Repraesentiert den Erfassungszustand dieses DE bezueglich der DeFa. Dieser
	 * Zustand kann die Werte <code>erfasst</code> und <code>nicht erfasst</code>
	 * annehmen
	 * 
	 * @author BitCtrl Systems GmbH, Thierfelder
	 *
	 */
	public class Zustand{
		
		/**
		 * indiziert, das die Parameter dieses DE initialisiert wurden
		 */
		private boolean initialisiert = true;
		
		/**
		 * die entsprechende Erassungsintervalldauer (in ms), wenn das DE auf zyklischen
		 * Abruf parametriert ist und -1 sonst
		 */
		private long erfassungsIntervallDauer = -1;
		
		/**
		 * Grund fuer die Tatsache, dass dieser Zustand den Wert nicht
		 * <code>nicht erfasst</code> hat
		 */
		private String grund = null;
		
		
		/**
		 * Standardkonstruktor
		 */
		protected Zustand(){
			synchronized (DeErfassungsZustand.this) {
				if(DeErfassungsZustand.this.deFehlerStatus != null){
					if(DeErfassungsZustand.this.deFehlerStatus == TlsDeFehlerStatus.OK){
						if(DeErfassungsZustand.this.aktiv != null){
							if(DeErfassungsZustand.this.aktiv){
								if(DeErfassungsZustand.this.erfassungsIntervallDauer != null){
									if(DeErfassungsZustand.this.erfassungsIntervallDauer >= 0){
										this.erfassungsIntervallDauer = DeErfassungsZustand.this.erfassungsIntervallDauer;
									}else{
										this.grund = "TLS-Fehlerueberwachung nicht moeglich, da keine " + //$NON-NLS-1$
												"zyklische Abgabe von Meldungen eingestellt"; //$NON-NLS-1$
									}
								}else{
									this.initialisiert = false;
								}								 
							}else{
								this.grund = GRUND_PRAEFIX + "DE-Kanal ist passiviert"; //$NON-NLS-1$
							}
						}else{
							this.initialisiert = false;
						}
					}else{
						this.grund = GRUND_PRAEFIX + "DE-Fehler(" +  //$NON-NLS-1$
							DeErfassungsZustand.this.deFehlerStatus.toString() + "): " //$NON-NLS-1$
							+ DeErfassungsZustand.this.deFehlerStatus.getText();
					}
				}else{
					this.initialisiert = false;
				}
			}
			
			LOGGER.info("Neuer Erfassungszusstand (" + DeErfassungsZustand.this.obj.getPid() + "):\n" + this); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		
		/**
		 * Erfragt den Grund fuer die Tatsache, dass dieser Zustand den Wert nicht
		 * <code>nicht erfasst</code> hat<br>
		 * 
		 * @return Grund fuer die Tatsache, dass dieser Zustand den Wert nicht
		 * <code>nicht erfasst</code> hat oder <code>null</code>, wenn dieser Zustand
		 * auf <code>erfasst</code> steht bzw. die Parameter noch nicht initialisiert
		 * wurden (<code>)
		 */
		public final String getGrund(){
			return this.grund;
		}
		
		
		/**
		 * Erfragt, ob die Parameter dieses DE initialisiert bereits wurden
		 * (der Zustand kann auch auf <code>nicht erfasst</code> stehen, wenn
		 * noch keine Initialisierung stattgefunden hat)
		 * 
		 * @return ob die Parameter dieses DE initialisiert bereits wurden
		 */
		public final boolean isInitialisiert(){
			return this.initialisiert;
		}
		
		
		/**
		 * Erfragt die aktuelle Erfassungsintervalldauer
		 * 
		 * @return die aktuelle Erfassungsintervalldauer
		 */
		public final long getErfassungsIntervallDauer(){
			return this.erfassungsIntervallDauer;
		}
		
		
		/**
		 * Erfragt den Erfassungszustand dieses DE bezueglich der DeFa. Dieser
		 * Zustand kann die Werte <code>erfasst</code> und <code>nicht erfasst</code> annehmen.
		 * Der Zustand <code>erfasst</code> wird angenommen wenn für dieses DE gilt:<br>
		 * 1.) es liegt aktuell kein DE-Fehler vor,<br>
		 * 2.) der DE-Kanalstatus hat den Wert <code>aktiv</code> und<br>
		 * 3.) die Erfassungsart ist auf <code>Zyklische Abgabe von Meldungen</code> gesetzt.<br>
		 * Sonst wird der Wert <code>nicht erfasst</code> angenommen
		 * 
		 * @return ob dieses DE im Sinne der DeFa als <code>erfasst</code> gilt
		 */
		public final boolean isErfasst(){
			return this.erfassungsIntervallDauer >= 0;
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			boolean gleich = false;
			
			if(obj != null && obj instanceof Zustand){
				Zustand that = (Zustand)obj;
				gleich = this.initialisiert == that.initialisiert &&
						 this.erfassungsIntervallDauer == that.erfassungsIntervallDauer;
				if(gleich){
					if(this.grund != null && that.grund != null){
						gleich &= this.grund.equals(that.grund);
					}else{
						gleich &= this.grund == null && that.grund == null;
					}
				}				
			}
			
			return gleich;
		}


		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			String s = Konstante.LEERSTRING;
			
			if(initialisiert){
				if(this.erfassungsIntervallDauer >= 0){
					s += "erfasst (Intervalldauer: " +  //$NON-NLS-1$
					DUAKonstanten.ZEIT_FORMAT_GENAU.format(new Date(this.erfassungsIntervallDauer)) +  ")"; //$NON-NLS-1$
				}else{
					s += this.grund;
				}
			}else{
				s += "nicht initialisiert"; //$NON-NLS-1$
			}
			
			return s;
		}
	
	}
	
}
