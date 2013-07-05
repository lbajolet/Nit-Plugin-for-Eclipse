package org.nitlanguage.ndt.core.debug.network;

import java.io.IOException;
import java.util.Observable;

import org.nitlanguage.ndt.core.debug.NitDebugConstants;
import org.nitlanguage.ndt.core.debug.events.DebugOverEvent;

public class NitDebuggerReader extends Observable implements Runnable {

	NitDebuggerConnector socket;

	public NitDebuggerReader(NitDebuggerConnector sc) {
		this.socket = sc;
	}

	@Override
	public void run() {
		try {
			while (socket.isConnected()) {
				if (socket.ready()) {
					String received = socket.receiveMessage();
					this.setChanged();
					this.notifyObservers(received);
				}
				Thread.sleep(25);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
