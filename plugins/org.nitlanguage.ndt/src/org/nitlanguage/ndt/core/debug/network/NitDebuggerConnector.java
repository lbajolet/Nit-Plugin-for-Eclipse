package org.nitlanguage.ndt.core.debug.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class NitDebuggerConnector {

	private Socket connection;

	private BufferedReader inBuffer;

	private BufferedWriter outBuffer;

	public Boolean isConnected() {
		return !connection.isClosed();
	}

	public NitDebuggerConnector connect(String host, int port)
			throws UnknownHostException, IOException {
		connection = new Socket(host, port);
		inBuffer = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		outBuffer = new BufferedWriter(new OutputStreamWriter(
				connection.getOutputStream()));
		return this;
	}

	public void disconnect() throws IOException {
		this.connection.close();
	}

	public NitDebuggerConnector sendMessage(String message) throws IOException {
		outBuffer.write(message);
		outBuffer.newLine();
		outBuffer.flush();
		return this;
	}

	public String receiveMessage() throws IOException {
		if (inBuffer.ready()) {
			return inBuffer.readLine();
		}
		return "";
	}

	public Boolean ready() throws IOException, InterruptedException {
		return inBuffer.ready();
	}

}
