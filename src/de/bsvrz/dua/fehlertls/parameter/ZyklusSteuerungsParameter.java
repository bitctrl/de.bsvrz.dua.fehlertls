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

package de.bsvrz.dua.fehlertls.parameter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dua.fehlertls.de.DeFaException;
import de.bsvrz.dua.fehlertls.de.DeTypLader;
import de.bsvrz.dua.fehlertls.de.IDeTyp;

/**
 * Klasse zum Auslesen und Anmelden auf die Betriebsparameter zur Zyklussteuerung
 * eines allgemeinen Systemobjektes vom Typ <code>typ.de</code>
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class ZyklusSteuerungsParameter
implements ClientReceiverInterface{
	
	/**
	 * statische Instanzen dieser Klasse
	 */
	private static Map<SystemObject, ZyklusSteuerungsParameter> INSTANZEN =
								Collections.synchronizedMap(new TreeMap<SystemObject, ZyklusSteuerungsParameter>());
	
	/**
	 * Menge aller Beobachterobjekte
	 */
	private Set<IZyklusSteuerungsParameterListener> listenerMenge = Collections.synchronizedSet(
			new HashSet<IZyklusSteuerungsParameterListener>());
	
	/**
	 * Schnittstelle zum De-Typ
	 */
	private IDeTyp deTyp = null;
	
	/**
	 * die aktuelle Erfassungsintervalldauer 
	 */
	private Long erfassungsIntervallDauer = null;
	
	
	/**
	 * Erfragt eine statische Instanz dieser Klasse
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @param objekt ein Objekt vom Typ <code>typ.de</code>
	 * @return eine statische Instanz dieser Klasse oder <code>null</code>
	 * @throws DeFaException wird geworfen, wenn es Probleme beim Laden oder
	 * Instanziieren der Klasse gibt, die den erfragten DE-Typ beschreibt
	 */
	public static final ZyklusSteuerungsParameter getInstanz(ClientDavInterface dav,
															 SystemObject objekt)
	throws DeFaException{
		ZyklusSteuerungsParameter instanz = null;
		
		synchronized (INSTANZEN) {
			instanz = INSTANZEN.get(objekt);	
		}		
		
		if(instanz == null){
			instanz = new ZyklusSteuerungsParameter(dav, objekt);
			synchronized (INSTANZEN) {
				INSTANZEN.put(objekt, instanz);	
			}			
		}
		
		return instanz;
	}
	
	
	/**
	 * Standardkonstruktor
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @param objekt ein Objekt vom Typ <code>typ.de</code>
	 * @throws DeFaException wird geworfen, wenn es Probleme beim Laden oder
	 * Instanziieren der Klasse gibt, die den erfragten DE-Typ beschreibt
	 */
	private ZyklusSteuerungsParameter(ClientDavInterface dav,
								 	  SystemObject objekt)
	throws DeFaException{
		this.deTyp = DeTypLader.getDeTyp(objekt.getType());
				
		dav.subscribeReceiver(this, 
							  objekt,
							  this.deTyp.getDeFaIntervallParameterDataDescription(dav),
							  ReceiveOptions.normal(),
							  ReceiverRole.receiver());
	}

	
	/**
	 * Fuegt diesem Objekt einen Listener hinzu
	 * 
	 * @param listener eine neuer Listener
	 */
	public final synchronized void addListener(final IZyklusSteuerungsParameterListener listener){
		if(listenerMenge.add(listener) && this.erfassungsIntervallDauer != null){
			listener.aktualisiereZyklusSteuerungsParameter(this.erfassungsIntervallDauer);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void update(ResultData[] resultate) {
		if(resultate != null){
			for(ResultData resultat:resultate){
				if(resultat != null && resultat.getData() != null){
					synchronized (this) {
						this.erfassungsIntervallDauer = this.deTyp.getErfassungsIntervall(resultat.getData());
						for(IZyklusSteuerungsParameterListener listener:this.listenerMenge){
							listener.aktualisiereZyklusSteuerungsParameter(this.erfassungsIntervallDauer);
						}
					}					
				}
			}
		}		
	}
	
}
