package liyunkai;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
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
 
	//IO
	private DataOutputStream message_to_Server;
	private DataInputStream message_from_Server;
	//cs-username
	private String username = null;
	//users list
	private ArrayList<String> list = new ArrayList<>();
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
 
	// Ⱥ�ķ�����Ϣ
	public void sendBroadcastMessage(String msg) {
		try {
			//Set the date format
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = data.format(new Date()).toString();
			 
			//Do message processing
			if (msg.equals("")) {
				//Prompt sending message cannot be null given
				JOptionPane.showMessageDialog(null, "The message can't be null", "Warning!!!", JOptionPane.ERROR_MESSAGE);
			}else if(msg.equals("EXIT")) {
				//Package the data into JSON format
				JSONObject data_send = new JSONObject();
				data_send.put("username", username);
				data_send.put("msg", msg);
				data_send.put("time", time);
				data_send.put("messageType", "message");
				//Useless account name, set isPrivChat to null
				data_send.put("isPrivChat", "");
				//Send data to server
				message_to_Server.writeUTF(data_send.toString());
				window.frmCommunicationSystem.dispose();;
			}else {
				//Ⱥ��ģʽ
				JSONObject data_send = new JSONObject();
				data_send.put("username", username);
				data_send.put("msg", msg);
				data_send.put("time", time);
				data_send.put("messageType", "message");
				//Useless account name, set isPrivChat to null
				data_send.put("isPrivChat", "");
				message_to_Server.writeUTF(data_send.toString());
				System.out.println("Ⱥ����Ϣ�����������ȴ�ת��");
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	// ˽�ķ�����Ϣ
	public void sendPrivateMessage(String msg, String recivername) {
		try {
			//Set the date format
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = data.format(new Date()).toString();
			
			//System.out.println("Client.sendMessage: " + msg + " " + data);
 
			if (msg.equals("")) {
				//Prompt sending message cannot be null given
				JOptionPane.showMessageDialog(null, "The message can't be null", "Warning!!!", JOptionPane.ERROR_MESSAGE);
			}else if(msg.equals("EXIT")) {
				//Package the data into JSON format
				JSONObject data_send = new JSONObject();
				data_send.put("username", username);
				data_send.put("msg", msg);
				data_send.put("time", time);
				data_send.put("messageType", "message");
				//Useless account name, set isPrivChat to null
				data_send.put("isPrivChat", "");
				//Send data to server
				message_to_Server.writeUTF(data_send.toString());
				window.frmCommunicationSystem.dispose();;
			}else {
				//˽��ģʽ
				//Package the data into JSON format
				JSONObject data_send = new JSONObject();
				data_send.put("username", username);
				data_send.put("msg", msg);
				data_send.put("time", time);
				data_send.put("messageType", "message");
				//Private chat, set isPrivChat to the user_get
				data_send.put("isPrivChat", recivername);
				//Send data to server
				message_to_Server.writeUTF(data_send.toString());
				System.out.println("˽����Ϣ�����������ȴ�ת��");
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	// Ⱥ�ķ����ļ�
	public void sendBroadcastFile(String msg, String filename) {
		try {
			//Set the date format
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = data.format(new Date()).toString();			
			JSONObject data_send = new JSONObject();
			data_send.put("username", username);
			data_send.put("msg", msg);
			data_send.put("time", time);
			data_send.put("messageType", "file");
			data_send.put("isPrivChat", "");  // set isPrivChat to null
			message_to_Server.writeUTF(data_send.toString());
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	// ˽�ķ����ļ�
	public void sendPrivateFile(String msg, String filename, String recivername) {
		try {
			//Set the date format
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = data.format(new Date()).toString();			
			JSONObject data_send = new JSONObject();
			data_send.put("username", username);
			data_send.put("msg", msg);
			data_send.put("time", time);
			data_send.put("messageType", "file");
			data_send.put("isPrivChat", recivername);  // set isPrivChat to null
			message_to_Server.writeUTF(data_send.toString());
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
				// The wireless loop listens for data sent from the server
				while (true) {
					// Read the data from the server
					json = message_from_Server.readUTF();
					JSONObject data = JSONObject.fromObject(json.toString());
 
					if (json != null) {
						// �õ�msg�������ж�����
						String mString = data.getString("msg");
						System.out.println("�ͻ��˽��յ�������ת����Ϣ");
						// �ж����ļ�ѯ�ʻ�����ͨ��Ϣ
						if(mString.equals("Send file request detection to the user...")) {
							// ��ͨ��Ϣ
							if(!data.getString("sender").equals(username)) {
								// �����ļ�
								int opt = JOptionPane.showConfirmDialog(window.frmCommunicationSystem, data.getString("sender") + "���㷢���ļ����Ƿ����?", "ȷ����Ϣ", JOptionPane.YES_NO_OPTION);
								if (opt == JOptionPane.YES_OPTION) {
									String content = data.getString("fileContent");
									window.saveFile(content, data.getString("sender"));
								}else {
									window.setContent("You have rejected file sharing from: " + data.getString("sender") + "\n\n");
								}
							}
						}else {
							// ��ͨ��Ϣ
							if(!data.getString("sender").equals(username)) {
								window.setContent(mString + "\n\n");
							}
						}
						// Refresh the user list
						list.clear();
						JSONArray jsonArray = data.getJSONArray("userlist");
						// Getting a list of users
						for (int i = 0; i < jsonArray.size(); i++) {
							list.add(jsonArray.get(i).toString());
						}
						// Update user list
						window.updateUserlist(list);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
 
}
