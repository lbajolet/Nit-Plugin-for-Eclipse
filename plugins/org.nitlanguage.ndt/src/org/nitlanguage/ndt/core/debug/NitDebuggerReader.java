package org.nitlanguage.ndt.core.debug;

import java.io.IOException;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NitDebuggerReader extends Observable implements Runnable {

	ConcurrentLinkedQueue<String> logger;

	NitDebuggerConnector socket;

	public NitDebuggerReader(NitDebuggerConnector sc) {
		this.socket = sc;
		this.logger = new ConcurrentLinkedQueue<String>();
	}

	@Override
	public void run() {
		try {
			while (socket.isConnected()) {
				if (socket.receivable()) {
					String received = socket.receiveMessage();
					logger.add(received);
					if (received.equals("$DBG DONE WORK ON SELF")) {
						socket.disconnect();
						this.setChanged();
						this.notifyObservers("Debugging Over");
						break;
					}
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
