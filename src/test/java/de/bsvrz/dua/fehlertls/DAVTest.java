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

package de.bsvrz.dua.fehlertls;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikation;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.bitctrl.dua.ufd.typen.UmfeldDatenArt;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;

/**
 * Stellt eine Datenverteiler-Verbindung zur Verfügung.
 *
 * @author BitCtrl Systems GmbH, Thomas Thierfelder
 * @version $Id$
 */
public final class DAVTest {
	/**
	 * Verbindungsdaten.
	 */
	private static final String[] CON_DATA = new String[] {
		"-datenverteiler=localhost:8083", "-benutzer=Tester",
			"-authentifizierung=passwd", "-debugLevelStdErrText=OFF",
			"-debugLevelFileText=OFF" };

	// /**
	// * Verbindungsdaten.
	// */
	// private static final String[] CON_DATA = new String[] {
	// "-datenverteiler=localhost:8083",
	// "-benutzer=Tester",
	// "-authentifizierung=c:\\passwd",
	// "-debugLevelStdErrText=ERROR",
	// "-debugLevelFileText=OFF" };

	/**
	 * Verbindung zum Datenverteiler.
	 */
	private static String geraet;

	/**
	 * Verbindung zum Datenverteiler.
	 */
	private static ClientDavInterface dav;

	/**
	 * Erste Datenzeit der Testdaten.
	 */
	public static final long START_ZEIT = 0;

	/**
	 * Konstruktor.
	 */
	private DAVTest() {

	}

	/**
	 * Setzt das zu ueberwachende Geraet.
	 *
	 * @param geraet1
	 *            eine PID eines Geraetes.
	 */
	public static void setTestParameter(final String geraet1) {
		DAVTest.geraet = geraet1;
	}

	/**
	 * Schließt die aktuelle Datenverteiler-Verbindung.
	 *
	 * @param nachricht
	 *            die Nachricht
	 * @throws Exception
	 *             wird weitergereicht
	 */
	public static void disconnect(final String nachricht) throws Exception {
		if (DAVTest.dav != null) {
			DAVTest.dav.disconnect(true, nachricht);
			DAVTest.dav = null;
		}
	}

	/**
	 * Erfragt bzw. initialisiert eine Datenverteiler-Verbindung fuer den
	 * Extra-Test.
	 *
	 * @return die Datenverteiler-Verbindung
	 */
	public static ClientDavInterface getDav() {

		if (DAVTest.dav == null) {

			final String[] conDataApp = new String[DAVTest.CON_DATA.length + 1];
			int i = 0;
			for (final String str : DAVTest.CON_DATA.clone()) {
				conDataApp[i++] = new String(str.getBytes());
			}
			conDataApp[i] = "-geraet=" + DAVTest.geraet;

			StandardApplicationRunner.run(new StandardApplication() {

				@Override
				public void initialize(final ClientDavInterface connection)
						throws Exception {
					DAVTest.dav = connection;
					UmfeldDatenArt.initialisiere(DAVTest.dav);
					// TestKEx.getInstanz(verbindung);
				}

				@Override
				public void parseArguments(final ArgumentList argumentList)
						throws Exception {
					//
				}

			}, DAVTest.CON_DATA.clone());
			StandardApplicationRunner.run(new DeFaApplikation(), conDataApp);
		}

		return DAVTest.dav;
	}

}
