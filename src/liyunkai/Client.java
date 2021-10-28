package liyunkai;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
 
import javax.swing.*;
import javax.swing.border.TitledBorder;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
 

/**
 * @author Eric Li
 * 
 * client
 * Start by registering the class
 * JFrame implements a windowed interface
 * Implement chat and receive server messages
 * JSON packages are introduced to enable the transfer of data between classes
 */
public class Client extends JFrame {
 
	//Accept message box
	private JTextField sendMessage = new JTextField();
	//Display information box
	private JTextArea showMessage = new JTextArea();
	//Display user list
	private JTextArea userlist = new JTextArea(10, 10);
	//IO
	private DataOutputStream message_to_Server;
	private DataInputStream message_from_Server;
	//cs-username
	private String username = null;
	//users list
	private ArrayList<String> list = new ArrayList<>();
	private boolean isKick = false;
	private ClientGUI window;
	

	/**
	 * Client constructor, drawing interface
	 */
	public Client(String username) {
 
		this.username = username;
		
		window = new ClientGUI(username, this);
		window.frmCommunicationSystem.setVisible(true);
 
		//Package the message into a JSON data format
		JSONObject data = new JSONObject();
		data.put("username", username);
		data.put("msg", null);
 
		try {
			//Create a socket connection server
			Socket socket = new Socket(InetAddress.getLocalHost(), 8080);
			//Get data from the server
			message_from_Server = new DataInputStream(socket.getInputStream());
			//Send data to the server
			message_to_Server = new DataOutputStream(socket.getOutputStream());
			//Send the user name to the server
			message_to_Server.writeUTF(data.toString());
			//Start a thread to read data sent from the server
			ReadThread readThread = new ReadThread();
			readThread.start();
		} catch (IOException ex) {
			//An exception occurred and the connection to the server failed
			window.setContent("No response from server");
		}
	}
 
	
	
	public void sendMessage(String msg) {
		try {
			//Set the date format
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = data.format(new Date()).toString();
			
			System.out.println("Client.sendMessage: " + msg + " " + data);
 
			//Do message processing
			if (msg.equals("")) {
				//Prompt sending message cannot be null given
				JOptionPane.showMessageDialog(null, "The message can't be null", "Warning!!!", JOptionPane.ERROR_MESSAGE);
			}else if (msg.equals("STOP")) {
				//The message cannot be sent for STOP to prevent triggering the server to exit with an exception
				JOptionPane.showMessageDialog(null, "The message can't be STOP", "Warning!!!", JOptionPane.ERROR_MESSAGE);
			}else if(msg.equals("EXIT")) {
				//Package the data into JSON format
				JSONObject data_send = new JSONObject();
				data_send.put("username", username);
				data_send.put("msg", msg);
				data_send.put("time", time);
				//Useless account name, set isPrivChat to null
				data_send.put("isPrivChat", "");
				//Send data to server
				message_to_Server.writeUTF(data_send.toString());
				System.exit(EXIT_ON_CLOSE);
			}else {
				//Split the message into the chat content and the user name
				String[] msg1 = msg.split("-");
				//Distinguish between group chats and private chats
				if(msg1.length == 2) {
					//私聊模式
					//Package the data into JSON format
					JSONObject data_send = new JSONObject();
					data_send.put("username", username);
					data_send.put("msg", msg1[1]);
					data_send.put("time", time);
					//Private chat, set isPrivChat to the user_get
					data_send.put("isPrivChat", msg1[0]);
					//Send data to server
					message_to_Server.writeUTF(data_send.toString());
				}else {
					//群发模式
					JSONObject data_send = new JSONObject();
					data_send.put("username", username);
					data_send.put("msg", msg1[0]);
					data_send.put("time", time);
					//Useless account name, set isPrivChat to null
					data_send.put("isPrivChat", "");
					message_to_Server.writeUTF(data_send.toString());
				}
			}
			//sendMessage.setText("");
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	/** 
	 * @author Eric Li
	 *
	 * Read the thread class from the server
	 */
	public class ReadThread extends Thread {
 
		public void run() {
			String json = null;
			try {
				//The wireless loop listens for data sent from the server
				while (true) {
					//Read the data from the server
					json = message_from_Server.readUTF();
					JSONObject data = JSONObject.fromObject(json.toString());
 
					if (json != null) {
						//得到msg并进行判断内容
						String mString = data.getString("msg");

						//Whether been kicked out of a group chat
						if (mString.contains("been kicked out") && mString.contains(username)) {
							isKick = true;
							window.setContent(username + ",You've been kicked out\n" + "Client will be close in 5s");
						}else {
							//Print chat messages or system prompts
							window.setContent(mString + "\n\n");

							//Refresh the user list
							list.clear();
							JSONArray jsonArray = data.getJSONArray("userlist");
 
							//Getting a list of users
							for (int i = 0; i < jsonArray.size(); i++) {
								list.add(jsonArray.get(i).toString());
							}
 
							// Update user list
							window.updateUserlist(list);
						}
					}
 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
 
}
