package liyunkai;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

import org.apache.commons.codec.binary.Base64;

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
@SuppressWarnings("serial")
public class Client extends JFrame {
 
	//IO
	private DataOutputStream message_to_Server;
	private DataInputStream message_from_Server;
	//cs-username
	private String username = null;
	//users list
	private ArrayList<String> list = new ArrayList<>();
	private ClientGUI window;
	private Socket socket;
	private String key;
	

	/**
	 * @author Eric Li
	 *  
	 * Client constructor
	 * 创建线程和连接
	 */
	public Client(String username, String k) {
		this.key = k;
		this.username = username;
		
		window = new ClientGUI(username, this, k);
		window.frmCommunicationSystem.setVisible(true);
 
		// 打包上线消息发送给服务器
		JSONObject data = new JSONObject();
		data.put("username", username);
		data.put("msg", null);
 
		try {
			// Create a socket connection server
			socket = new Socket(InetAddress.getLocalHost(), 8080);
			// Get data from the server
			message_from_Server = new DataInputStream(socket.getInputStream());
			// Send data to the server
			message_to_Server = new DataOutputStream(socket.getOutputStream());
			// Send the user name to the server
			message_to_Server.writeUTF(data.toString());
			// Start a thread to read data sent from the server
			ReadThread readThread = new ReadThread();
			readThread.start();
			window.setState(true);
		} catch (IOException ex) {
			//An exception occurred and the connection to the server failed
			window.setContent("Client: No response from server\n\n");
			window.setState(false);
		}
	}
 
	// 群聊发送消息
	public void sendBroadcastMessage(String msg) {
		try {
			
//			System.out.println("当前秘钥为: " + key);
			
			//Set the date format
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = data.format(new Date()).toString();
			 
			// Do message processing
			if (msg.equals("")) {
				// Prompt sending message cannot be null given
				JOptionPane.showMessageDialog(null, "The message can't be null", "Warning!!!", JOptionPane.ERROR_MESSAGE);
			}else if(msg.equals("EXIT")) {
				// Package the data into JSON format
				JSONObject data_send = new JSONObject();
				data_send.put("username", username);
				data_send.put("msg", msg);
				data_send.put("time", time);
				data_send.put("messageType", "message");
				// Useless account name, set isPrivChat to null
				data_send.put("isPrivChat", "");
				// Send data to server
				message_to_Server.writeUTF(data_send.toString());
				window.frmCommunicationSystem.dispose();;
			}else {
				// 群发模式
				DESAlgorithm des = new DESAlgorithm(key);
		        // 将输入内容转化为byte数组
		        byte[] dataBytes = msg.getBytes(Charset.forName("UTF-8"));		        
		        // 调用DesStrat函数进行加密，加密代码为1
		        byte[] result = des.DesStart(dataBytes, 1);
		        // 将加密后的byte转化为string传输展示
		        String atest = Base64.encodeBase64String(result);
				window.setAfterEncryptionArea(atest);
				JSONObject data_send = new JSONObject();
				data_send.put("username", username);
				data_send.put("msg", atest);
				data_send.put("time", time);
				data_send.put("messageType", "message");
				// Useless account name, set isPrivChat to null
				data_send.put("isPrivChat", "");
				message_to_Server.writeUTF(data_send.toString());
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	// 私聊发送消息
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
				//私聊模式
				DESAlgorithm des = new DESAlgorithm(key);
		        // 将输入内容转化为byte数组
		        byte[] dataBytes = msg.getBytes(Charset.forName("UTF-8"));		        
		        // 调用DesStrat函数进行加密，加密代码为1
		        byte[] result = des.DesStart(dataBytes, 1);
		        // 将加密后的byte转化为string传输展示
		        String atest = Base64.encodeBase64String(result);
		        window.setAfterEncryptionArea(atest);
				//Package the data into JSON format
				JSONObject data_send = new JSONObject();
				data_send.put("username", username);
				data_send.put("msg", atest);
				data_send.put("time", time);
				data_send.put("messageType", "message");
				//Private chat, set isPrivChat to the user_get
				data_send.put("isPrivChat", recivername);
				//Send data to server
				message_to_Server.writeUTF(data_send.toString());
				System.out.println("私聊消息至服务器，等待转发");
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	// 群聊发送文件
	public void sendBroadcastFile(String msg, String filename) {
		try {
			DESAlgorithm des = new DESAlgorithm(key);
	        // 将输入内容转化为byte数组
	        byte[] dataBytes = msg.getBytes(Charset.forName("UTF-8"));		        
	        // 调用DesStrat函数进行加密，加密代码为1
	        byte[] result = des.DesStart(dataBytes, 1);
	        // 将加密后的byte转化为string传输展示
	        String atest = Base64.encodeBase64String(result);
	        window.setAfterEncryptionArea(atest);
			//Set the date format
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = data.format(new Date()).toString();			
			JSONObject data_send = new JSONObject();
			data_send.put("username", username);
			data_send.put("msg", atest);
			data_send.put("time", time);
			data_send.put("messageType", "file");
			data_send.put("isPrivChat", "");  // set isPrivChat to null
			message_to_Server.writeUTF(data_send.toString());
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	// 私聊发送文件
	public void sendPrivateFile(String msg, String filename, String recivername) {
		try {
			DESAlgorithm des = new DESAlgorithm(key);
	        // 将输入内容转化为byte数组
	        byte[] dataBytes = msg.getBytes(Charset.forName("UTF-8"));		        
	        // 调用DesStrat函数进行加密，加密代码为1
	        byte[] result = des.DesStart(dataBytes, 1);
	        // 将加密后的byte转化为string传输展示
	        String atest = Base64.encodeBase64String(result);
	        window.setAfterEncryptionArea(atest);
			//Set the date format
			SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = data.format(new Date()).toString();			
			JSONObject data_send = new JSONObject();
			data_send.put("username", username);
			data_send.put("msg", atest);
			data_send.put("time", time);
			data_send.put("messageType", "file");
			data_send.put("isPrivChat", recivername);  // set isPrivChat to null
			message_to_Server.writeUTF(data_send.toString());
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}
	
	// 客户端下线
	public void disconnect() {
		//Set the date format
		SimpleDateFormat dataType = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = dataType.format(new Date()).toString();
		JSONObject data_send = new JSONObject();
		data_send.put("username", username);
		data_send.put("msg", "EXIT");
		data_send.put("time", time);
		data_send.put("messageType", "message");
		//Useless account name, set isPrivChat to null
		data_send.put("isPrivChat", "");
		//Send data to server
		try {
			message_to_Server.writeUTF(data_send.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 客户端上线
	public int connect() {
		try {
			//Set the date format
			SimpleDateFormat dataType = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dataType.format(new Date()).toString();
			JSONObject data_send = new JSONObject();
			data_send.put("username", username);
			data_send.put("msg", "Connect!!!");
			data_send.put("time", time);
			data_send.put("messageType", "message");
			//Useless account name, set isPrivChat to null
			data_send.put("isPrivChat", "");
			message_to_Server.writeUTF(data_send.toString());
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			window.setContent("The server is not online, please check the server status.\n\n");
			return 0;
//			e.printStackTrace();
		}
	}
	
	// 用于改变key
	public void changeKey(String k) {
		key = k;
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
						// 得到msg并进行判断内容
						String mString = data.getString("msg");
						// 判断是文件还是普通消息
						if(data.getString("messageType").equals("file")) {
							if(!data.getString("username").equals(username)) {
								// 传输文件
								int opt = JOptionPane.showConfirmDialog(window.frmCommunicationSystem, data.getString("username") + "向你发送文件，是否接收?", "确认信息", JOptionPane.YES_NO_OPTION);
								if (opt == JOptionPane.YES_OPTION) {
									DESAlgorithm des = new DESAlgorithm(key);
							        // 将输入内容转化为byte数组
							        byte[] dataBytes = Base64.decodeBase64(mString);
							        // 调用DesEncrypt函数进行加密，解密代码为0
							        byte[] decode_text_byte = des.DesStart(dataBytes, 0);
							        String decode_textString = new String(decode_text_byte, "utf-8");
									window.saveFile(decode_textString, data.getString("username"));
								}else {
									window.setContent("You have rejected file sharing from: " + data.getString("username") + "\n\n");
								}
							}
						}else {
							// 普通消息
							if(!data.getString("username").equals(username)) {
								DESAlgorithm des = new DESAlgorithm(key);
						        // 将输入内容转化为byte数组
						        byte[] dataBytes = Base64.decodeBase64(mString);
						        // 调用DesEncrypt函数进行加密，解密代码为0
						        byte[] decode_text_byte = des.DesStart(dataBytes, 0);
						        String decode_textString = new String(decode_text_byte, "utf-8");
								String outputInScreen = data.getString("username") + " (" + data.getString("time") + "):\n"
										+ decode_textString;
								window.setContent(outputInScreen + "\n\n");
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
