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

package de.bsvrz.dua.fehlertls.de;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dav.daf.main.config.SystemObjectType;

/**
 * Dient zum dynamischen Laden der einzelnen DE-Typ-Beschreibungen. Diese
 * Beschreibungen implementieren das Interface <code>IDeTyp</code>, m�ssen so
 * hei�en, wie die DE-Typ-PID in der Konfiguration (ohne Punkte) und m�ssen
 * weiterhin im Package <code>de.bsvrz.dua.DeFa</code> definiert sein.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public final class DeTypLader {

	/**
	 * Der Name dieses Packages. Hier m�ssen auch alle anderen Klassen liegen,
	 * die einen DE-Typ bzgl. der SWE "DE Fehleranalyse fehlende Messdaten"
	 * beschreiben. Also alle Klassen die das Interface <code>IDeTyp</code>
	 * unterst�tzen.
	 */
	private static final String PACKAGE = DeTypLader.class.getPackage()
			.getName()
			+ ".typen"; //$NON-NLS-1$

	/**
	 * speichert alle statischen Instanzen von DE-Typen.
	 */
	private static Map<SystemObject, IDeTyp> typen = Collections
			.synchronizedMap(new HashMap<SystemObject, IDeTyp>());

	
	/**
	 * Default-Konstruktor.
	 */
	private DeTypLader() {
		
	}
	
	
	/**
	 * Erfragt eine (statische) Instanz einer DE-Typ-Beschreibung, wie sie in
	 * der SWE "DE Fehleranalyse fehlende Messdaten" ben�tigt wird, um die
	 * Datenidentifikationen eines bestimmten DE-Typs zu ermitteln, die
	 * Messwerte enthalten.
	 * 
	 * @param deTypObj
	 *            Systemobjekttyp des DE-Typs wie er in der Konfiguration steht
	 * @return eine Instanz einer DE-Typ-Beschreibung des DE-Typs, dessen PID
	 *         �bergeben wurde
	 * @throws DeFaException
	 *             wird geworfen, wenn es Probleme beim Laden oder Instanziieren
	 *             der Klasse gibt, die den erfragten DE-Typ beschreibt
	 */
	public static IDeTyp getDeTyp(final SystemObjectType deTypObj)
			throws DeFaException {
		IDeTyp deTyp = null;

		synchronized (typen) {
			deTyp = typen.get(deTypObj);

			if (deTyp == null) {
				Class<?> klasse = null;

				try {
					klasse = ClassLoader.getSystemClassLoader().loadClass(
							PACKAGE + "." + //$NON-NLS-1$
									getKlassenNameVonPid(deTypObj.getPid()));
					deTyp = (IDeTyp) klasse.newInstance();
					typen.put(deTypObj, deTyp);
				} catch (Throwable e) {
					throw new DeFaException(e);
				}
			}
		}

		return deTyp;
	}

	/**
	 * Wandelt die DE-Typ-PID in einen Klassennamen um. Dabei werden alle Punkte
	 * entfernt und alle Buchstaben hinter den Punkten in Gro�buchstaben
	 * umgewandelt. Weiterhin wird der erste Buchstabe in einen Gro�buchstaben
	 * umgewandelt. Alle Umlaute werden wie folgt ver�ndert:<br>
	 * � -> ae, � -> Ae<br>
	 * � -> ue, � -> Ue<br>
	 * � -> oe, � -> Oe<br>
	 * � -> ss
	 * 
	 * @param pid
	 *            eine PID eines DE-Typs
	 * @return den �ber die PID referenzierten Klassennamen
	 */
	private static String getKlassenNameVonPid(final String pid) {
		String dummy = pid;

		dummy = dummy.replaceAll("�", "ue"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("�", "Ue"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("�", "ae"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("�", "Ae"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("�", "oe"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("�", "Oe"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("�", "ss"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.substring(0, 1).toUpperCase()
				+ dummy.substring(1, dummy.length());

		String klassenName = ""; //$NON-NLS-1$
		for (int i = 0; i < dummy.length();) {
			String zeichen = dummy.substring(i, i + 1);
			if (zeichen.equals(".")) { //$NON-NLS-1$
				i++;
				klassenName += dummy.substring(i, i + 1).toUpperCase();
			} else {
				klassenName += zeichen;
			}
			i++;
		}

		return klassenName;
	}

}
