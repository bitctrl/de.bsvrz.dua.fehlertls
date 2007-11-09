/**
 * Segment 4 Daten�bernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
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
 * Wei�enfelser Stra�e 67<br>
 * 04229 Leipzig<br>
 * Phone: +49 341-490670<br>
 * mailto: info@bitctrl.de
 */

package de.bsvrz.dua.fehlertls.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.daf.DaVKonstanten;

/**
 * Korrespondiert mit der Attributgruppe <code>atg.parameterTlsFehlerAnalyse</code>
 * (Parameter f�r die TLS Fehleranalyse)
 *  
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class ParameterTlsFehlerAnalyse
implements ClientReceiverInterface{

	/**
	 * statische Instanzen dieser Klasse
	 */
	private static Map<SystemObject, ParameterTlsFehlerAnalyse> INSTANZEN =
								Collections.synchronizedMap(new HashMap<SystemObject, ParameterTlsFehlerAnalyse>());
	
	/**
	 * Menge aller Beobachterobjekte
	 */
	private Set<IParameterTlsFehlerAnalyseListener> listenerMenge = Collections.synchronizedSet(
			new HashSet<IParameterTlsFehlerAnalyseListener>());
	
	/**
	 * Der zus�tzliche Zeitverzug, der nach dem erwarteten 
	 * Empfangszeitpunkt noch bis zur Erkennung eines nicht gelieferten Messwertes abgewartet 
	 * werden muss
	 */
	 private long zeitverzugFehlerErkennung = Long.MIN_VALUE;
	 
	 /**
	  * Der zus�tzliche Zeitverzug, der nach der Fehlererkennung
	  * bis zur Fehlerermittlung abgewartet werden muss
	  **/
	 private long zeitverzugFehlerErmittlung = Long.MIN_VALUE;;

	
	
	/**
	 * Erfragt eine statische Instanz dieser Klasse
	 * 
	 * @param dav Verbindung zum Datenverteiler
	 * @param objekt ein Objekt vom Typ <code>typ.tlsFehlerAnalyse</code>
	 * @return eine statische Instanz dieser Klasse oder <code>null</code>
	 */
	public static final ParameterTlsFehlerAnalyse getInstanz(ClientDavInterface dav,
															 SystemObject objekt){
		ParameterTlsFehlerAnalyse instanz = null;
		
		synchronized (INSTANZEN) {
			instanz= INSTANZEN.get(objekt);	
		}		
		
		if(instanz == null){
			instanz = new ParameterTlsFehlerAnalyse(dav, objekt);
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
	 * @param objekt ein Objekt vom Typ <code>typ.tlsFehlerAnalyse</code>
	 */
	private ParameterTlsFehlerAnalyse(ClientDavInterface dav,
										SystemObject objekt){
		dav.subscribeReceiver(this, 
							  objekt,
							  new DataDescription(
									  dav.getDataModel().getAttributeGroup("atg.parameterTlsFehlerAnalyse"), //$NON-NLS-1$
									  dav.getDataModel().getAspect(DaVKonstanten.ASP_PARAMETER_SOLL),
									  (short)0),
							  ReceiveOptions.normal(),
							  ReceiverRole.receiver());
	}

	
	/**
	 * Fuegt diesem Objekt einen Listener hinzu
	 * 
	 * @param listener eine neuer Listener
	 */
	public final synchronized void addListener(final IParameterTlsFehlerAnalyseListener listener){
		if(listenerMenge.add(listener) && this.zeitverzugFehlerErkennung != Long.MIN_VALUE){
			listener.aktualisiereParameterTlsFehlerAnalyse(zeitverzugFehlerErkennung, zeitverzugFehlerErmittlung);
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
						this.zeitverzugFehlerErkennung = resultat.getData().
									getTimeValue("ZeitverzugFehlerErkennung").getMillis(); //$NON-NLS-1$
						this.zeitverzugFehlerErmittlung = resultat.getData().
									getTimeValue("ZeitverzugFehlerErmittlung").getMillis(); //$NON-NLS-1$
						for(IParameterTlsFehlerAnalyseListener listener:this.listenerMenge){
							listener.aktualisiereParameterTlsFehlerAnalyse(this.zeitverzugFehlerErkennung,
																		   this.zeitverzugFehlerErmittlung);
						}
					}					
				}
			}
		}		
	}
	
}
