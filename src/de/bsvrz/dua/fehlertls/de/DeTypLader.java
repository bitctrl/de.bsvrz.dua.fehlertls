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

package de.bsvrz.dua.fehlertls.de;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dav.daf.main.config.SystemObjectType;


/**
 * Dient zum dynamischen Laden der einzelnen DE-Typ-Beschreibungen. Diese 
 * Beschreibungen implementieren das Interface <code>IDeTyp</code>, müssen
 * so heißen, wie die DE-Typ-PID in der Konfiguration (ohne Punkte) und
 * müssen weiterhin im Package <code>de.bsvrz.dua.DeFa</code> definiert
 * sein.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 */
public class DeTypLader {
	
	/**
	 * Der Name dieses Packages. Hier müssen auch alle anderen Klassen
	 * liegen, die einen DE-Typ bzgl. der SWE "DE Fehleranalyse fehlende
	 * Messdaten" beschreiben. Also alle Klassen die das Interface
	 * <code>IDeTyp</code> unterstützen.
	 */
	private static final String PACKAGE = DeTypLader.class.getPackage().getName() + ".typen"; //$NON-NLS-1$
	
	/**
	 * speichert alle statischen Instanzen von DE-Typen
	 */
	private static Map<SystemObject, IDeTyp> TYPEN = Collections.synchronizedMap(new HashMap<SystemObject, IDeTyp>()); 
	
	
	/**
	 * Erfragt eine (statische) Instanz einer DE-Typ-Beschreibung, wie sie in der SWE 
	 * "DE Fehleranalyse fehlende Messdaten" benötigt wird, um die Datenidentifikationen
	 * eines bestimmten DE-Typs zu ermitteln, die Messwerte enthalten
	 * 
	 * @param deTypObj Objekt des DE-Typs wie er in der Konfiguration steht
	 * @return eine Instanz einer DE-Typ-Beschreibung des DE-Typs,
	 * dessen PID übergeben wurde 
	 * @throws DeFaException wird geworfen, wenn es Probleme beim Laden oder
	 * Instanziieren der Klasse gibt, die den erfragten DE-Typ beschreibt
	 */
	public static final IDeTyp getDeTyp(final SystemObjectType deTypObj)
	throws DeFaException{
		IDeTyp deTyp = null;
		
		synchronized (TYPEN) {
			deTyp = TYPEN.get(deTypObj);
			
			if(deTyp == null){
				Class<?> klasse = null;
				
				try {
					klasse = ClassLoader.getSystemClassLoader().loadClass(PACKAGE + "." + //$NON-NLS-1$
								getKlassenNameVonPid(deTypObj.getPid()));
					deTyp = (IDeTyp)klasse.newInstance();
					TYPEN.put(deTypObj, deTyp);
				} catch (Throwable e) {
					throw new DeFaException(e);
				}
			}			
		}
		
		return deTyp;
	}
	
	
	/**
	 * Wandelt die DE-Typ-PID in einen Klassennamen um. Dabei werden alle Punkte entfernt
	 * und alle Buchstaben hinter den Punkten in Großbuchstaben umgewandelt. Weiterhin
	 * wird der erste Buchstabe in einen Großbuchstaben umgewandelt. Alle Umlaute
	 * werden wie folgt verändert:<br>
	 * ä -> ae, Ä -> Ae<br>
	 * ü -> ue, Ü -> Ue<br>
	 * ö -> oe, Ö -> Oe<br>
	 * ß -> ss
	 * 
	 * @param pid eine PID eines DE-Typs
	 * @return den über die PID referenzierten Klassennamen
	 */
	private static final String getKlassenNameVonPid(final String pid){
		String dummy = pid;

		dummy = dummy.replaceAll("ü", "ue"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("Ü", "Ue"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("ä", "ae"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("Ä", "Ae"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("ö", "oe"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("Ö", "Oe"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("ß", "ss"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.substring(0, 1).toUpperCase() +
					dummy.substring(1, dummy.length());

		String klassenName = ""; //$NON-NLS-1$
		for(int i = 0; i < dummy.length(); ){
			String zeichen = dummy.substring(i, i + 1);
			if(zeichen.equals(".")){ //$NON-NLS-1$
				i++;
				klassenName += dummy.substring(i, i + 1).toUpperCase();
			}else{
				klassenName += zeichen;
			}
			i++;
		}		
		
		return klassenName;
	}
	
}
