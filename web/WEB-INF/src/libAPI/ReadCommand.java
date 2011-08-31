package libAPI;

/*
 * CommandRead.java
 *
 * Created on 19 June 2007, 10:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.io.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author janisk
 */
public class ReadCommand implements Runnable {

	private DataInputStream in = null;
	LinkedBlockingQueue queue = null;

	/**
	 * Creates a new instance of CommandRead
	 * @param in - Data input stream of socket
	 * @param queue - data output inteface
	 */
	public ReadCommand(DataInputStream in, LinkedBlockingQueue queue) {
		this.in = in;
		this.queue = queue;
	}

	public void run() {
		byte b = 0;
		String s = "";
		char ch;
		int lenFirstByte = 0;
		while (true) {
			int length = 0;
			int lengthWordLen;
			
			try {
				lenFirstByte = in.read();
			} catch (IOException ex) {
				return;
			}
			
			if (lenFirstByte != 0 && lenFirstByte > 0) {
				if (lenFirstByte < 0x80) {
					lengthWordLen = 1;
					length = lenFirstByte;
				} else if (lenFirstByte < 0xC0) {
					lengthWordLen = 2;
					length = lenFirstByte & ~0x80;
				} else if (lenFirstByte < 0xE0) {
					lengthWordLen = 3;
					length = lenFirstByte & ~0xC0;
				} else if (lenFirstByte < 0xF0) {
					lengthWordLen = 4;
					length = lenFirstByte & ~0xE0;
				} else if (lenFirstByte < 0xF8) {
					lengthWordLen = 4;
					length = 0;
				} else {
					Logger.getLogger(ReadCommand.class.getName()).log(Level.SEVERE,
							"Don't know how to interpret length byte: " + lenFirstByte);
					break;
				}

				try {
					for (int i = 1; i < lengthWordLen; i++) {
						length <<= 8;
						int newByte = in.read();
						length |= newByte;
					}
				} catch (IOException ex) {
					Logger.getLogger(ReadCommand.class.getName()).log(Level.SEVERE, null, ex);
					return;
				}

				s += "\n";
				byte[] dataBytes = new byte[length];
				try {
					lenFirstByte = in.read(dataBytes, 0, length);
				} catch (IOException ex) {
					lenFirstByte = 0;
					ex.printStackTrace();
					return;
				}
				if (lenFirstByte > 0) {
					s += new String(dataBytes);
				}
			} else if (b == -1) {
				System.out.println("Error, it should not happen ever, or connected to wrong port");
			} else {
				try {
					queue.put(s);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					System.out.println("exiting reader");
					return;
				}
				s = "";
			}
		}
	}
}