package misc;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame implements KeyListener {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private String message = "";
	private String serverIP;
	private Socket connection;
	private int keyStroke;
	//constructor
	public Client(String host) {
		super("Client");
		this.serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		userText.addKeyListener(this);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	}

	
	/**Initialize main procedures to connect to server*/
	public void startRunning() {
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException e) {
			showMessage("\nClient terminated connection");
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}finally {
			closeCrap();
		}
	}
	
	/**Connect to the server*/
	private void connectToServer() throws IOException{
		showMessage("Attempting connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName() );
	}
	
	/**Set up streams to send and receive messages*/
	private void setupStreams() throws IOException {
		outputStream = new ObjectOutputStream(connection.getOutputStream());
		outputStream.flush();
		inputStream = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams have been established.");
	}
	private int getKeyStroke() {
		return this.keyStroke;
	}
	/**While chatting with server*/
	private void whileChatting() throws IOException {
		userText.setEditable(true);
		do {
			try {
				message = (String)inputStream.readObject();
				showMessage("\n" + message);
			}catch (ClassNotFoundException e) {
				showMessage("\nCannot display this object type");
			}
		} while (!message.equals("SERVER - END"));
	}
	
	/**Close the streams and sockets*/
	private void closeCrap() {
		showMessage("\nClosing all connections.");
		userText.setEditable(false);
		try {
			outputStream.close();
			inputStream.close();
			connection.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Send message to server*/
	private void sendMessage(String message) {
		try {
			outputStream.writeObject("CLIENT - " + message);
			outputStream.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException ioe) {
			chatWindow.append("\nError sending message");
		}
	}
	
	/**Update text area, chatWindow*/
	private void showMessage(final String m) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				chatWindow.append(m);
			}
		});
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Keyevent: " + e.getKeyCode());
		if (e.getKeyCode() == KeyEvent.VK_F12) {
			sendMessage("Sent key event: " + e.getKeyCode());
			userText.setText("");
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
