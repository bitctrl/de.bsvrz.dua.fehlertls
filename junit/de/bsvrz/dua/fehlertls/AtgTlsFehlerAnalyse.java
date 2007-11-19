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
package de.bsvrz.dua.fehlertls;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.enums.TlsFehlerAnalyse;

/**
 * Assoziiert mit DE-Daten von <code>atg.tlsFehlerAnalyse</code>, <code>asp.analyse</code>
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 */
public class AtgTlsFehlerAnalyse
implements ClientReceiverInterface{
	
	/**
	 * statische Instanzen dieser Klasse
	 */
	private static Map<SystemObject, AtgTlsFehlerAnalyse> INSTANZEN = new HashMap<SystemObject, AtgTlsFehlerAnalyse>();
	
	/**
	 * Listenermenge
	 */
	private Set<IAtgTlsFehlerAnalyseListener> listenerMenge = 
		Collections.synchronizedSet(new HashSet<IAtgTlsFehlerAnalyseListener>());
	
	/**
	 * aktueller Fehler
	 */
	private TlsFehlerAnalyse aktuellerFehler = null;
	
	
	/**
	 * Erfragt eine statische Instanz dieser Klasse
	 * 
	 * @param obj ein DE-Objekt
	 * @return eine statische Instanz dieser Klasse
	 * @throws Exception wird weitergereicht
	 */
	public static final AtgTlsFehlerAnalyse getInstanz(SystemObject obj)
	throws Exception{
		AtgTlsFehlerAnalyse instanz = INSTANZEN.get(obj);
		
		if(instanz == null){
			instanz = new AtgTlsFehlerAnalyse(obj);
			INSTANZEN.put(obj, instanz);
		}
		
		return instanz;
	}
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param obj ein DE-Objekt
	 * @throws Exception wird weitergereicht
	 */
	private AtgTlsFehlerAnalyse(SystemObject obj)
	throws Exception{
		DataDescription datenBeschreibung = new DataDescription(
					DAVTest.getDav().getDataModel().getAttributeGroup("atg.tlsFehlerAnalyse"), //$NON-NLS-1$
					DAVTest.getDav().getDataModel().getAspect("asp.analyse")); //$NON-NLS-1$
		DAVTest.getDav().subscribeReceiver(this, obj, datenBeschreibung, ReceiveOptions.normal(), ReceiverRole.receiver());
	}


	/**
	 * Fuegt Listener hinzu
	 * 
	 * @param listener neuer Listener
	 */
	public final synchronized void addListener(IAtgTlsFehlerAnalyseListener listener){
		if(this.listenerMenge.add(listener) && this.aktuellerFehler != null){
			listener.aktualisiereTlsFehlerAnalyse(this.aktuellerFehler);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] results) {
		if(results != null){
			for(ResultData result:results){
				if(result != null && result.getData() != null){
					synchronized (this) {
						this.aktuellerFehler = TlsFehlerAnalyse.getZustand(
								result.getData().getUnscaledValue("TlsFehlerAnalyse").intValue()); //$NON-NLS-1$
						for(IAtgTlsFehlerAnalyseListener listener:this.listenerMenge){
							listener.aktualisiereTlsFehlerAnalyse(this.aktuellerFehler);
						}
					}
				}
			}
		}
	}

}
