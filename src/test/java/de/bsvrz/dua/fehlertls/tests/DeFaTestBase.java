/*
 * Copyright 2016 by Kappich Systemberatung Aachen
 * 
 * This file is part of de.bsvrz.dua.fehlertls.tests.
 * 
 * de.bsvrz.dua.fehlertls.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * de.bsvrz.dua.fehlertls.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with de.bsvrz.dua.fehlertls.tests.  If not, see <http://www.gnu.org/licenses/>.

 * Contact Information:
 * Kappich Systemberatung
 * Martin-Luther-Straße 14
 * 52062 Aachen, Germany
 * phone: +49 241 4090 436 
 * mail: <info@kappich.de>
 */

package de.bsvrz.dua.fehlertls.tests;

import de.bsvrz.dua.fehlertls.fehlertls.DeFaApplikation;
import de.bsvrz.dua.tests.DuATestBase;
import de.bsvrz.sys.funclib.bitctrl.dua.DUAKonstanten;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;
import de.bsvrz.sys.funclib.kappich.annotations.NotNull;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessageInterface;
import de.bsvrz.sys.funclib.operatingMessage.OperatingMessageSink;
import org.junit.After;

import java.time.LocalTime;
import java.util.Date;

/**
 * TBD Dokumentation
 *
 * @author Kappich Systemberatung
 */
public abstract class DeFaTestBase extends DuATestBase{
	private DeFaApplikation _deFaApplikation;

	private static OperatingMessageSink _sink;

	static {
		_sink = new OperatingMessageSink() {
			@Override
			public void publish(final OperatingMessageInterface message) {
				System.out.println(date() + " BM: " + message);
			}
		};
	}

	@NotNull
	@Override
	protected String[] getConfigurationAreas() {
		return new String[]{"kb.fehlerTlsTest"};
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		OperatingMessageSink.register(_sink);
		_deFaApplikation = new DeFaApplikation();
		_deFaApplikation.parseArguments(new ArgumentList(getArgs()));
		_deFaApplikation.initialize(_connection);
	}

	
	private String[] getArgs() {
		return new String[]{
				"-geraet=" + getDevice()
		};
	}

	protected abstract String getDevice();

	@After
	public void tearDown() throws Exception {
		OperatingMessageSink.unregister(_sink);
		super.tearDown();
	}

	@NotNull
	protected static String date() {
		return LocalTime.now().toString();
	}
}
