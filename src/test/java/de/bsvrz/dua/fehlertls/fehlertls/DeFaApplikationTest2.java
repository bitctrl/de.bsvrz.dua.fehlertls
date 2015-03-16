/**
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

import java.util.Date;

import org.junit.Test;

import com.bitctrl.Constants;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dua.fehlertls.DAVTest;
import de.bsvrz.dua.fehlertls.DeStatus;
import de.bsvrz.dua.fehlertls.TypDeTestWrapper;
import de.bsvrz.dua.fehlertls.TypTlsFehlerAnalyse;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;

/**
 * Testet die Applikation nach PruefSpez (erster Teil - UZ).
 *
 * @author BitCtrl Systems GmbH, Thierfelder
 *
 * @version $Id$
 */
public class DeFaApplikationTest2 {

	/**
	 * Debug?
	 */
	public static final boolean DEBUG = false;

	/**
	 * Debug2?
	 */
	public static final boolean DEBUG2 = true;

	/**
	 * Zeigt an, ob der Test an ist, oder ob nur die Ausgaben mitgeloggt werden
	 * sollen.
	 */
	public static final boolean ASSERTION_AN = true;

	/**
	 * Zeigt an, ob der der Text der DE-Fehleranalyse an allen DE ausgegeben
	 * werden soll.
	 */
	public static final boolean SHOW_DE = true;

	/**
	 * Zeigt an, ob die Betriebsmeldungen an allen DE ausgegeben werden soll.
	 */
	public static final boolean SHOW_BM = true;

	/**
	 * Zeigt an, ob die Nummer des Zeitpunktes mitgeloggt werden soll.
	 */
	public static final boolean SHOW_ZEITPUNKT = true;

	/**
	 * Testet analog PrüfSpez. (1.Teil).
	 *
	 * @throws Exception
	 *             wird weitergereicht
	 */
	@Test
	public void test() throws Exception {
		DAVTest.setTestParameter("UZ");
		final ClientDavInterface dav = DAVTest.getDav();

		TypDeTestWrapper.init(dav);
		final TypTlsFehlerAnalyse parameter = TypTlsFehlerAnalyse
				.getInstanz(dav);

		parameter.setParameter(15L * Constants.MILLIS_PER_SECOND,
				60L * Constants.MILLIS_PER_SECOND);
		for (int i = 1; i < 17; i++) {
			final TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE" + i);

			if (i == 6) {
				de.setBetriebsParameter(-1);
			} else {
				de.setBetriebsParameter(15L * 1000L);
			}
			de.setDe(DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AUS);
		}
		TypDeTestWrapper.getInstanz("DE2").setDe(
				DeStatus.KANAL_AKTIVIERT_DE_FEHLER_AN);
		TypDeTestWrapper.getInstanz("DE4").setDe(
				DeStatus.KANAL_PASSIVIERT_DE_FEHLER_AUS);

		if (DeFaApplikationTest2.DEBUG2) {
			System.out
			.println("\n"
					+ DUAKonstanten.NUR_ZEIT_FORMAT_GENAU
					.format(new Date(System.currentTimeMillis()))
					+ "\nDatengenerator fuer alle ausser DE2, DE4 und DE6 starten und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 3; r++) {
			final long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				final TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE"
						+ i);
				if ((i != 6) && (i != 4) && (i != 2)) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (final InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator für DE1 stoppen und auf Ergebnisse warten
		 */
		if (DeFaApplikationTest2.DEBUG2) {
			System.out
			.println("\n"
					+ DUAKonstanten.NUR_ZEIT_FORMAT_GENAU
					.format(new Date(System.currentTimeMillis()))
					+ "\nDatengenerator fuer DE1 stoppen und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 6; r++) {
			final long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				final TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE"
						+ i);
				if ((i != 6) && (i != 2) && (i != 4) && (i != 1)) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (final InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator für DE3 stoppen und 2 Minuten auf Ergebnisse warten
		 */
		if (DeFaApplikationTest2.DEBUG2) {
			System.out
			.println("\n"
					+ DUAKonstanten.NUR_ZEIT_FORMAT_GENAU
					.format(new Date(System.currentTimeMillis()))
					+ "\nDatengenerator fuer DE3 stoppen und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 6; r++) {
			final long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				final TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE"
						+ i);
				if ((i != 6) && (i != 1) && (i != 3) && (i != 4) && (i != 2)) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (final InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator für DE5, DE7 und DE8 stoppen und 2 Minuten auf
		 * Ergebnisse warten
		 */
		if (DeFaApplikationTest2.DEBUG2) {
			System.out
			.println("\n"
					+ DUAKonstanten.NUR_ZEIT_FORMAT_GENAU
					.format(new Date(System.currentTimeMillis()))
					+ "\nDatengenerator fuer DE5, DE7 und DE8 stoppen und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 6; r++) {
			final long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				final TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE"
						+ i);
				if ((i != 1) && (i != 2) && (i != 3) && (i != 4) && (i != 5)
						&& (i != 6) && (i != 7) && (i != 8)) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (final InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator für DE9,..., DE16 stoppen und 2 Minuten auf Ergebnisse
		 * warten
		 */
		if (DeFaApplikationTest2.DEBUG2) {
			System.out
			.println("\n"
					+ DUAKonstanten.NUR_ZEIT_FORMAT_GENAU
					.format(new Date(System.currentTimeMillis()))
					+ "\nDatengenerator fuer DE9,..., DE16 stoppen und auf Ergebnisse warten\n");
		}
		for (int r = 0; r < 6; r++) {
			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (final InterruptedException ex) {
				//
			}
		}

		/**
		 * Datengenerator alle außer DE2, DE4, DE6, DE13,..., DE16 wieder
		 * anschalten und 2 Minuten auf Ergebnisse warten
		 */
		if (DeFaApplikationTest2.DEBUG2) {
			System.out
			.println("\n"
					+ DUAKonstanten.NUR_ZEIT_FORMAT_GENAU
					.format(new Date(System.currentTimeMillis()))
					+ "\nDatengenerator fuer alle ausser DE2, DE4, DE6, DE13,..., DE16 wieder anschalten und auf Ergebnisse werden\n");
		}
		for (int r = 0; r < 20; r++) {
			final long jetzt = System.currentTimeMillis();
			for (int i = 1; i < 17; i++) {
				final TypDeTestWrapper de = TypDeTestWrapper.getInstanz("DE"
						+ i);
				if ((i != 2) && (i != 4) && (i != 6) && (i != 13) && (i != 14)
						&& (i != 15) && (i != 16)) {
					de.sendDeData(true, jetzt);
				}
			}

			try {
				Thread.sleep(15L * Constants.MILLIS_PER_SECOND);
			} catch (final InterruptedException ex) {
				//
			}
		}

	}

}
