package misc;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private ServerSocket server;
	private Socket connectionSocket;
	private Method method;
	public Server() {
		// TODO Auto-generated constructor stub
		super("Shitty Instant Messenger");
		userText = new JTextField();
		userText.setEditable(false);
		// ADD ACTIONLISTENER FOR USERTEXT
		
		userText.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				sendMessageToClient(e.getActionCommand());
				userText.setText(""); //messege has been sent, so reset the userText text field
			}
		});
		this.add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		this.add(new JScrollPane(chatWindow));
		this.setSize(300,150);
		this.setVisible(true);
	}
	
	
	/**Set up and run the server*/
	public void startRunning() {
		try{
			server = new ServerSocket(6789, 100);
			while(true) {
				try {
					//will wait for awhile till connection is found
					waitForConnection();
					//then, once they're connected, set up streams
					setupStreams();
					whileChatting();
				}catch(EOFException eofException) {
					showMessage("\nServer ended the connection! ");
				}finally {
					closeCrap();
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Wait for connection, then display connection information */
	private void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect... \n");
		connectionSocket = server.accept();
		showMessage("Now connected to "+connectionSocket.getInetAddress().getHostName());
	}
	
	/**Get stream to send and receive data*/
	private void setupStreams() throws IOException{
		outputStream = new ObjectOutputStream(connectionSocket.getOutputStream());
		outputStream.flush();
		//only the client sending out data can flush. Inputstream can't be flushed.
		inputStream = new ObjectInputStream(connectionSocket.getInputStream());
		showMessage("\nStreams are now set up.");
		
	}
	
	private void whileChatting() throws IOException {
		String message = "You are now connected.";
		sendMessageToClient(message);
		userText.setEditable(true);
		do {
			//have conversation
			try{
				message = (String)inputStream.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException e) {
				showMessage("\nDunno what the user sent.");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	/** close streams and sockets after you're down chatting*/
	private void closeCrap() {
		showMessage("\nClosing Connection...");
		userText.setEditable(false);
		try {
			outputStream.close();
			inputStream.close();
			connectionSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	/**Send message to client*/
	private void sendMessageToClient(String message) {
		try{
			outputStream.writeObject("SERVER -  " + message);
			outputStream.flush();
			showMessage("\nSERVER -  " + message);
		}catch(IOException e) {
			chatWindow.append("\nERROR: CANNOT SEND THAT MESSAGE");
		}
	}
	
	/**Updates chatWindow*/
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				chatWindow.append(text);
			}
		});
	}
	//made it to video 47
}
