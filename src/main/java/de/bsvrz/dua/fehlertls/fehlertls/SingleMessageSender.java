/*
 * Segment 4 Datenübernahme und Aufbereitung (DUA), SWE 4.DeFa DE Fehleranalyse fehlende Messdaten
 * Copyright (C) 2007-2015 BitCtrl Systems GmbH
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

package de.bsvrz.dua.fehlertls.fehlertls;

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.config.DynamicObject;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.bitctrl.daf.BetriebsmeldungDaten;
import de.bsvrz.sys.funclib.bitctrl.daf.DefaultBetriebsMeldungsIdKonverter;
import de.bsvrz.sys.funclib.debug.Debug;
import de.bsvrz.sys.funclib.operatingMessage.MessageCauser;
import de.bsvrz.sys.funclib.operatingMessage.MessageGrade;
import de.bsvrz.sys.funclib.operatingMessage.MessageSender;
import de.bsvrz.sys.funclib.operatingMessage.MessageState;
import de.bsvrz.sys.funclib.operatingMessage.MessageType;

/**
 * Klasse zur einmaligen Publikation von Betriebsmeldungen.
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 */
public class SingleMessageSender {

	private static final Debug LOGGER = Debug.getLogger();

	private static final DefaultBetriebsMeldungsIdKonverter KONVERTER = new DefaultBetriebsMeldungsIdKonverter();

	/** letzte fuer dieses DE publizierte einmalige Betriebsmeldung. */
	private String letzteEinmaligeNachricht = Constants.EMPTY_STRING;

	private final DynamicObject localUser;

	/**
	 * Konstruktor, erzeugt eine Instanz des MessageSenders für die übergebene
	 * Datenverteilerverbiondung an.
	 *
	 * @param dav
	 *            die Datenverteilerverbindung
	 */
	public SingleMessageSender(final ClientDavInterface dav) {
		localUser = dav.getLocalUser();
	}

	/**
	 * Publiziert eine Fehlermeldung einmalig.
	 *
	 * @param grade
	 *            die Meldungsklasse fuer die Betriebsmeldungen
	 * @param obj
	 *            das mit der Nachricht assoziierte Systemobjekt
	 * @param text
	 *            der Text der Fehlermeldung
	 */
	public final void publiziere(final MessageGrade grade, final SystemObject obj, final String text) {
		if (!this.letzteEinmaligeNachricht.equals(text)) {
			this.letzteEinmaligeNachricht = text;

			if (obj != null) {
				MessageSender.getInstance().sendMessage(
						SingleMessageSender.KONVERTER.konvertiere(new BetriebsmeldungDaten(obj), null, new Object[0]),
						MessageType.APPLICATION_DOMAIN, null, grade, obj, MessageState.MESSAGE,
						new MessageCauser(localUser, Constants.EMPTY_STRING, DeFaApplikation.getAppName()), text);
			} else {
				MessageSender.getInstance().sendMessage(MessageType.APPLICATION_DOMAIN, null, grade, obj,
						new MessageCauser(localUser, Constants.EMPTY_STRING, DeFaApplikation.getAppName()), text);
			}
			SingleMessageSender.LOGGER.info(text);
		} else {
			SingleMessageSender.LOGGER.info(obj + ", Keine doppelte Ausgabe von: " + text);
		}
	}

}
