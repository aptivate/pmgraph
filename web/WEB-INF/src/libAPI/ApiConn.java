package libAPI;

/*
 * This contains connection. Everything should be here,
 * should operate with this class only
 */


import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author janisk
 */
public class ApiConn extends Thread {

	private Socket sock = null;
	private DataOutputStream out = null;
	private DataInputStream in = null;
	private String ipAddress;
	private int ipPort;
	private boolean connected = false;
	private String message = "Not connected";
	private ReadCommand readCommand = null;
	private WriteCommand writeCommand = null;
	private Thread listener = null;
	LinkedBlockingQueue queue = new LinkedBlockingQueue(40);
	private Exception storedException;

	/**
	 * Constructor of the connection class
	 * @param ipAddress - IP address of the router you want to conenct to
	 * @param ipPort - port used for connection, ROS default is 8728
	 */
	public ApiConn(String ipAddress, int ipPort) {
		this.ipAddress = ipAddress;
		this.ipPort = ipPort;
		this.setName("settings");
	}

	/**
	 * State of connection
	 * @return - if connection is established to router it returns true.
	 */
	public boolean isConnected() {
		return connected;
	}
	public void disconnect() throws IOException{
		listener.interrupt();
		sock.close();
	}
	private void listen() {
		if (this.isConnected()) {
			if (readCommand == null) {
				readCommand = new ReadCommand(in, queue);
			}
			listener = new Thread(readCommand);
			listener.setDaemon(true);
			listener.setName("listener");
			listener.start();

		}
	}

	/**
	 * to get IP address of the connection. Reads data from socket created.
	 * @return InetAddress
	 */
	public InetAddress getIpAddress() {
		return sock == null ? null : sock.getInetAddress();
	}

	/**
	 * returns ip address that socket is asociated with.
	 * @return InetAddress
	 */
	public InetAddress getLocalIpAddress() {
		return sock == null ? null : sock.getLocalAddress();
	}

	/**
	 * Socket remote port number
	 * @return
	 */
	public int getPort() {
		return sock == null ? null : sock.getPort();
	}

	/**
	 * return local prot used by socket
	 * @return
	 */
	public int getLocalPort() {
		return sock == null ? null : sock.getLocalPort();
	}

	/**
	 * Returns status message set up bu class.
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * sets and exectues command (sends it to RouterOS host connected)
	 * @param s - command will be sent to RouterOS for example "/ip/address/print\n=follow="
	 * @return
	 */
	public String sendCommand(String s) {
		return writeCommand.setCommand(s).runCommand();
	}

	/**
	 * exeecutes already set command.
	 * @return returns status of the command sent
	 */
	public String runCommand() {
		return writeCommand.runCommand();
	}

	/**
	 * Tries to fech data that is repllied to commands sent. It will wait till it can return something.
	 * @return returns data sent by RouterOS
	 * @throws java.lang.InterruptedException
	 */
	public String getData() throws InterruptedException {
		String s = (String) queue.take();
		return s;
	}

	/**
	 * returns command that is set at this moment. And will be exectued if runCommand is exectued.
	 * @return
	 */
	public String getCommand() {
		return writeCommand.getCommand();
	}

	/**
	 * set up method that will log you in
	 * @param name - username of the user on the router
	 * @param password - password for the user
	 * @return
	 */
	public String login(String name, char[] password) {
		this.sendCommand("/login");
		String s = "a";
		try {
			s = this.getData();
		} catch (InterruptedException ex) {
			Logger.getLogger(ApiConn.class.getName()).log(Level.SEVERE, null, ex);
			return "failed read #1";
		}
		if (!s.contains("!trap") && s.length() > 4) {
			String[] tmp = s.trim().split("\n");
			if (tmp.length > 1) {
				tmp = tmp[1].split("=ret=");
				s = "";
				String transition = tmp[tmp.length - 1];
				String chal = "";
				chal = Hasher.hexStrToStr("00") + new String(password) + Hasher.hexStrToStr(transition);
				chal = Hasher.hashMD5(chal);
				String m = "/login\n=name=" + name + "\n=response=00" + chal;
				s = this.sendCommand(m);
				try {
					s = this.getData();
				} catch (InterruptedException ex) {
					Logger.getLogger(ApiConn.class.getName()).log(Level.SEVERE, null, ex);
					return "failed read #2";
				}
				if (s.contains("!done")) {
					if (!s.contains("!trap")) {
						return "Login successful";
					}
				}
			}
		}
		return "Login failed";
	}
	
	@Override
	public void run() {
		try {
			InetAddress ia = InetAddress.getByName(ipAddress);
			if (ia.isReachable(1000)) {
				sock = new Socket(ipAddress, ipPort);
				in = new DataInputStream(sock.getInputStream());
				out = new DataOutputStream(sock.getOutputStream());
				connected = true;
				readCommand = new ReadCommand(in, queue);
				writeCommand = new WriteCommand(out);
				this.listen();
				message = "Connected";
			} else {
				message = "Not reachable";
			}
		} catch (UnknownHostException ex) {
			connected = false;
			message = ex.getMessage();
			storedException = ex;
		} catch (IOException ex) {
			connected = false;
			message = ex.getMessage();
			storedException = ex;
		}
	}
	
	public Exception getStoredException()
	{
		return this.storedException;
	}
}