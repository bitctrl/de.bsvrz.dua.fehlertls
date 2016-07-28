/*
 * Segment Datenübernahme und Aufbereitung (DUA), Fehleranalyse fehlende Messdaten TLS
 * Copyright (C) 2007 BitCtrl Systems GmbH 
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.fehlertls.
 * 
 * de.bsvrz.dua.fehlertls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.fehlertls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.fehlertls.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.fehlertls.de;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dav.daf.main.config.SystemObjectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Dient zum dynamischen Laden der einzelnen DE-Typ-Beschreibungen. Diese
 * Beschreibungen implementieren das Interface <code>IDeTyp</code>, müssen so
 * heißen, wie die DE-Typ-PID in der Konfiguration (ohne Punkte) und müssen
 * weiterhin im Package <code>de.bsvrz.dua.DeFa</code> definiert sein.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public final class DeTypLader {

	/**
	 * Der Name dieses Packages. Hier müssen auch alle anderen Klassen liegen,
	 * die einen DE-Typ bzgl. der SWE "DE Fehleranalyse fehlende Messdaten"
	 * beschreiben. Also alle Klassen die das Interface <code>IDeTyp</code>
	 * unterstützen.
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
	 * der SWE "DE Fehleranalyse fehlende Messdaten" benötigt wird, um die
	 * Datenidentifikationen eines bestimmten DE-Typs zu ermitteln, die
	 * Messwerte enthalten.
	 * 
	 * @param deTypObj
	 *            Systemobjekttyp des DE-Typs wie er in der Konfiguration steht
	 * @return eine Instanz einer DE-Typ-Beschreibung des DE-Typs, dessen PID
	 *         übergeben wurde
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
	 * entfernt und alle Buchstaben hinter den Punkten in Großbuchstaben
	 * umgewandelt. Weiterhin wird der erste Buchstabe in einen Großbuchstaben
	 * umgewandelt. Alle Umlaute werden wie folgt verändert:
	 * 
	 * <ul>
	 * <li>ä -&gt; ae, Ä -&gt; Ae</li>
	 * <li>ü -&gt; ue, Ü -&gt; Ue</li>
	 * <li>ö -&gt; oe, Ö -&gt; Oe</li>
	 * <li>ß -&gt; ss</li>
	 * </ul>
	 * 
	 * @param pid
	 *            eine PID eines DE-Typs
	 * @return den über die PID referenzierten Klassennamen
	 */
	private static String getKlassenNameVonPid(final String pid) {
		String dummy = pid;

		dummy = dummy.replaceAll("ü", "ue"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("Ü", "Ue"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("ä", "ae"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("Ä", "Ae"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("ö", "oe"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("Ö", "Oe"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy = dummy.replaceAll("ß", "ss"); //$NON-NLS-1$ //$NON-NLS-2$
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
