package libAPI;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author janisk
 */
public class WriteCommand {

	private byte[] len = {0};
	private DataOutputStream out = null;
	private String command = "";

	WriteCommand(DataOutputStream out, String command) {
		this.out = out;
		this.command = command.replaceAll("\n", "").trim();
	}

	WriteCommand(DataOutputStream out) {
		this.out = out;
	}

	WriteCommand setCommand(String command) {
		this.command = command.trim();
		return this;
	}

	String getCommand() {
		return command;
	}

	private byte[] writeLen(String command) {
		Integer i = null;
		String s = "";
		String ret = "";
		if (command.length() < 0x80) {
			i = command.length();
		} else if (command.length() < 0x4000) {
			i = Integer.reverseBytes(command.length() | 0x8000);
		} else if (command.length() < 0x20000) {
			i = Integer.reverseBytes(command.length() | 0xC00000);
		} else if (command.length() < 10000000) {
			i = Integer.reverseBytes(command.length() | 0xE0000000);
		} else {
			i = Integer.reverseBytes(command.length());
		}
		s = Integer.toHexString(i);
		if (s.length() < 2) {
			return new byte[]{i.byteValue()};
		} else {
			for (int j = 0; j < s.length(); j += 2) {
				ret += (char) Integer.parseInt(s.substring(j, j + 2), 16) != 0 ? (char) Integer.parseInt(s.substring(j, j + 2), 16) : "";
			}
		}
		char[] ch = ret.toCharArray();
		return ret.getBytes();
	}

	String runCommand() {
		try {
			byte[] ret = new byte[0];
			if (!command.contains("\n")) {
				int i = 0;
				byte[] b = writeLen(command);
				int retLen = b.length + command.length() + 1;
				ret = new byte[retLen];
				for (i = 0; i < b.length; i++) {
					ret[i] = b[i];
				}
				for (byte c : command.getBytes("US-ASCII")) {
					ret[i++] = c;
				}
			} else {
				String[] str = command.split("\n");
				int i = 1;
				int[] iTmp = new int[str.length];
				for (int a = 0; a < str.length; a++) {
					iTmp[a] = writeLen(str[a]).length + str[a].length();
				}
				for (int b : iTmp) {
					i += b;
				}
				ret = new byte[i];
				int counter = 0;
				for (int a = 0; a < str.length; a++) {
					int j = 0;
					byte[] b = writeLen(str[a]);
					for (j = 0; j < b.length; j++) {
						ret[counter++] = b[j];
					}
					for (byte c : str[a].getBytes("US-ASCII")) {
						ret[counter++] = c;
					}
				}
			}
			out.write(ret);
			return "Sent successfully";
		} catch (IOException ex) {
			Logger.getLogger(WriteCommand.class.getName()).log(Level.SEVERE, null, ex);
			return "failed";
		}
	}
}