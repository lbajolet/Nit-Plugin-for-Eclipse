package builder;

import plugin.NitActivator;

public class NitCompilerMessage {

	public static final int NIT_ERROR_TYPE = 1;
	public static final int NIT_WARNING_TYPE = 2;
	public static final int NIT_OTHER_TYPE = 3;

	private String rawMessage;
	private int type;
	private String fileName;
	private int line;
	private int startIndex;
	private int endIndex;
	private String realMessage;

	public NitCompilerMessage(String rawMessage) {
		setRawMessage(rawMessage);
	}

	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
		try {
			parseMessage();
		} catch (Exception e) {
			if (NitActivator.DEBUG_MODE)
				e.printStackTrace();
		}
	}

	public String getRawMessage() {
		return this.rawMessage;
	}

	private void parseMessage() {
		// Gets the type of message
		if (this.rawMessage.toLowerCase().contains("error")) {
			this.setType(NIT_ERROR_TYPE);
		} else if (this.rawMessage.toLowerCase().contains("warning")) {
			this.setType(NIT_WARNING_TYPE);
		} else {
			this.setType(NIT_OTHER_TYPE);
		}

		// Now, the other informations
		String[] messageParts = this.rawMessage.split(":");
		if (messageParts.length >= 3) {
			// Get File Name
			String[] fileNameWithFS = messageParts[0].split("/");
			this.setFileName(fileNameWithFS[fileNameWithFS.length - 1]);

			// Get Line + start/end infos
			int[] infos = getInfos(messageParts[1]);

			if (infos.length == 3) {
				this.setLine(infos[0]);
				this.setStartIndex(infos[1]);
				this.setEndIndex(infos[2]);
			}

			// Get Error message
			StringBuffer errBuf = new StringBuffer();
			for (int i = 2; i < messageParts.length; i++) {
				errBuf.append(messageParts[i]);
				if (i != messageParts.length - 1) {
					errBuf.append(":");
				}
			}
			this.setRealMessage(errBuf.toString());
		}
	}

	private int[] getInfos(String midStringInCompilerMessage) {
		// This is what the line looks like : 410,25--15

		// Create the array
		int[] infos = new int[3];

		// Parse line first
		StringBuffer lineBuf = new StringBuffer();
		int currIndexInString = 0;

		while (midStringInCompilerMessage.charAt(currIndexInString) != ','
				&& currIndexInString < midStringInCompilerMessage.length()) {
			lineBuf.append(midStringInCompilerMessage.charAt(currIndexInString));
			currIndexInString++;
		}
		currIndexInString++;
		infos[0] = Integer.parseInt(lineBuf.toString());

		// Parse Start Index
		StringBuffer startIndex = new StringBuffer();

		while (currIndexInString < midStringInCompilerMessage.length()
				&& midStringInCompilerMessage.charAt(currIndexInString) != '-') {
			startIndex.append(midStringInCompilerMessage
					.charAt(currIndexInString));
			currIndexInString++;
		}

		infos[1] = Integer.parseInt(startIndex.toString());

		// Parse End Index
		StringBuffer endIndex = new StringBuffer();

		while (currIndexInString < midStringInCompilerMessage.length()) {
			if (midStringInCompilerMessage.charAt(currIndexInString) != '-') {
				endIndex.append(midStringInCompilerMessage
						.charAt(currIndexInString));
			}
			currIndexInString++;
		}

		String end = endIndex.toString();

		if (end.compareTo("") != 0) {
			infos[2] = Integer.parseInt(endIndex.toString());
		} else {
			infos[2] = infos[1] + 1;
		}

		return infos;
	}

	public String getFileName() {
		return fileName;
	}

	private void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getType() {
		return type;
	}

	private void setType(int type) {
		this.type = type;
	}

	public int getLine() {
		return line;
	}

	private void setLine(int line) {
		this.line = line;
	}

	public int getStartIndex() {
		return startIndex;
	}

	private void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	private void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public String getRealMessage() {
		return realMessage;
	}

	private void setRealMessage(String realMessage) {
		this.realMessage = realMessage;
	}
}
