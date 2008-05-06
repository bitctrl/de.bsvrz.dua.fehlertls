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

package de.bsvrz.dua.fehlertls.fehlertls;

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;

/**
 * Klasse zur einmaligen Publikation von Betriebsmeldungen.
 * 
 * @author BitCtrl Systems GmbH, Thierfelder
 * 
 * @version $Id$
 */
public class SingleMessageSender {

	/**
	 * letzte fuer dieses DE publizierte einmalige Betriebsmeldung.
	 */
	private String letzteEinmaligeNachricht = Constants.EMPTY_STRING;

	/**
	 * Publiziert eine Fehlermeldung einmalig.
	 * 
	 * @param grade die Meldungsklasse fuer die Betriebsmeldungen
	 * @param obj das mit der Nachricht assoziierte Systemobjekt
	 * @param text
	 *            der Text der Fehlermeldung
	 */
	public final void publiziere(final MessageGrade grade,
			final SystemObject obj, final String text) {
		if (!this.letzteEinmaligeNachricht.equals(text)) {
			this.letzteEinmaligeNachricht = text;
			MessageSender.getInstance().sendMessage(
					MessageType.APPLICATION_DOMAIN,
					DeFaApplikation.getAppName(),
					grade,
					obj,
					new MessageCauser(DeFaApplikation.getDav().getLocalUser(),
							Constants.EMPTY_STRING, DeFaApplikation
									.getAppName()), text);
			Debug.getLogger().info(text); //$NON-NLS-1$
		} else {
			Debug.getLogger().info(
					obj + ", Keine doppelte Ausgabe von: " + text); //$NON-NLS-1$
		}
	}
	
}
