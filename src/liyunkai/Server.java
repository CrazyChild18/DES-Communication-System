package liyunkai;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

import org.apache.commons.codec.binary.Base64;

import net.sf.json.JSONObject;

/**
 * @author Eric Li
 * 
 * The server class inherits the JFrame and implements the windowed interface
 * Responsible for receiving and distributing messages to the server
 * 
 * {@code}ArrayList<User>: Use list to store users
 * {@code}DataOutputStream: output stream
 * {@code}DataInputStream: input stream
 */
@SuppressWarnings("serial")
public class Server extends JFrame {

	//List of online users
	ArrayList<User> clientList = new ArrayList<User>();
	//List of online user names
	ArrayList<String> usernamelist = new ArrayList<String>();
	//List of offline users
	ArrayList<User> offlineClientList = new ArrayList<User>();
	//List of offline user names
	ArrayList<String> offlineUsernamelist = new ArrayList<String>();
	//User object, which has two variables socket and username
	private User user = null;
	//Declare an output stream
	DataOutputStream output = null;
	//Declare an input stream
	DataInputStream input = null;
	private ServerGUI window;

	public static void main(String[] args) {
		new Server();
	}

	/**
	 * Server construction method
	 * drawing graphical interface
	 * listening socket connection
	 */
	public Server() {
		
		window = new ServerGUI(this);
		window.frame.setVisible(true);

		try {
			// Create a server socket, bind port 8000
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(8080);
			// Print startup time
			window.addContent("Server: Startup time --> " + new Date() + "\n\n");
			
			// Infinite loop listens for new client connections
			while (true) {
				//Listen for a new connection
				Socket socket = serverSocket.accept();
				// ���յ���������
				if (socket != null) {
					// ��������û���Ϣ
					input = new DataInputStream(socket.getInputStream());
					String json = input.readUTF();
					JSONObject data = JSONObject.fromObject(json.toString());
					window.addContent("Client online: " + data.getString("username") + " at: " + new Date() + "\n\n");
					// Create a new user object
					user = new User();
					user.setSocket(socket);
					user.setUserName(data.getString("username"));
					// ���·��������������û���Ϣ
					clientList.add(user);
					usernamelist.add(data.getString("username"));
					// �������������û��б����
					window.addContent("Update userlist at: " + new Date() + "\n\n");
					window.updateUserlist(clientList, usernamelist);
				}

				// �����µķ�������Ϣ�������͸��ͻ���
				JSONObject online = new JSONObject();
				SimpleDateFormat dataType = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = dataType.format(new Date()).toString();
				online.put("userlist", usernamelist);
				String onlineMessage = "-logged in-";
				
		        online.put("msg", onlineMessage);
				online.put("username", "Server");
				online.put("messageType", "message");
				online.put("time", time);
				for (int i = 0; i < clientList.size(); i++) {
					try {
						User user = clientList.get(i);
						// Get each user socket, get the output stream
						output = new DataOutputStream(user.getSocket().getOutputStream());
						output.writeUTF(online.toString());
					} catch (IOException ex) {
						System.err.println(ex);
					}
				}
				
				// Ϊ�ͻ��˴����߳�
				HandleAClient task = new HandleAClient(socket);
				new Thread(task).start();
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	
	/**
	 * Custom user thread class
	 * determine whether private chat
	 * prompt the user to log off
	 */
	class HandleAClient implements Runnable {
		//Connected sockets
		private Socket socket;

		public HandleAClient(Socket socket) {
			this.socket = socket;
		}

		public void run() {

			try {
				// Gets the input stream from the socket client that this thread is listening to
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());

				// Cycle to monitor
				while (true) {

					// Get the client data
					String json = inputFromClient.readUTF();
					JSONObject data = JSONObject.fromObject(json.toString());
					
					
					if(data.getString("msg").equals("EXIT")) {
						// ������Ϣ
						// ִ�����ߺ�����֪ͨ
						for(int i = 0; i < clientList.size(); i++) {
							if (clientList.get(i).getUserName().equals(data.getString("username"))) {
								offLine(i);
								window.addContent("Client ("  + data.getString("username") + ") offline at:" + new Date() + "\n\n");
							}
						}
					}else if(data.getString("msg").equals("Connect!!!")) {
						System.out.println("�������յ�������������");
						// ����������Ϣ
						window.addContent("Client online: " + data.getString("username") + " at: " + new Date() + "\n\n");
						// Create a new user object
						for(int i = 0; i < offlineClientList.size(); i++) {
							if(offlineUsernamelist.get(i).equals(data.getString("username"))) {
								user = offlineClientList.get(i);
							}
						}
						// ���·��������������û���Ϣ
						clientList.add(user);
						usernamelist.add(data.getString("username"));
						// �������������û��б����
						window.addContent("Update userlist at: " + new Date() + "\n\n");
						window.updateUserlist(clientList, usernamelist);
						// �����µķ�������Ϣ�������͸��ͻ���
						JSONObject online = new JSONObject();
						SimpleDateFormat dataType = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String time = dataType.format(new Date()).toString();
						online.put("userlist", usernamelist);
						String onlineMessage = "-logged in-";
				        online.put("msg", onlineMessage);
						online.put("username", "Server");
						online.put("messageType", "message");
						online.put("time", time);
						for (int i = 0; i < clientList.size(); i++) {
							try {
								User user = clientList.get(i);
								// Get each user socket, get the output stream
								output = new DataOutputStream(user.getSocket().getOutputStream());
								output.writeUTF(online.toString());
								System.out.println("������ת���������������: " + user.getUserName());
							} catch (IOException ex) {
								System.err.println(ex);
							}
						}
					}else {
						boolean isPrivate = false;
						// ˽����Ϣ
						// �ҵ�Ŀ���û���������Ϣ
						for (int i = 0; i < clientList.size(); i++) {
							// �ҵ�˽�Ķ���
							if (clientList.get(i).getUserName().equals(data.getString("isPrivChat"))) {
								// �ж�Ϊ�ı���Ϣ�����ļ���Ϣ
								if (data.getString("messageType").equals("message")) {
									// �ڷ������м�¼��Ϣ��Դ
									window.addClientFromContent("The server receives a message from (" + data.getString("username")+ "), the encrypted content is: " + data.getString("msg") + "\n\n");
									// �����Ϣ���ݲ����͵�ָ���ͻ���
									packMsg(i, data.getString("msg"), data, "");
									// �ڷ������м�¼��Ϣ����
									window.addClientToContent("The server forwards the message to the user: " + data.getString("isPrivChat") + "\n\n");
									i++;
									// �Դ�����Ⱥ�����
									isPrivate = true;
									break;
								}else if(data.getString("messageType").equals("file")) {
									// �ڷ������м�¼��Ϣ��Դ
									window.addClientFromContent("The server receives a file from (" + data.getString("username")+ "), the encrypted content is: " + data.getString("msg") + "\n\n");
									// �����Ϣ���ݲ����͵�ָ���ͻ���
									packMsg(i, data.getString("msg"), data, data.getString("fileName"));
									// �ڷ������м�¼��Ϣ����
									window.addClientToContent("The server forwards the file to the user: " + data.getString("isPrivChat") + "\n\n");
									i++;
									// �Դ�����Ⱥ�����
									isPrivate = true;
									break;
								}
							}
						}
						// Ⱥ����Ϣ
						// ���б������û�������Ϣ
						if (isPrivate == false) {
							if(data.getString("messageType").equals("message")) {
								// �ڷ������м�¼��Ϣ��Դ
								window.addClientFromContent("The server receives a message from (" + data.getString("username")+ "), the encrypted content is: " + data.getString("msg") + "\n\n");
								// �ڷ������м�¼��Ϣ����
								window.addClientToContent("The server forwards the message to the following user:\n");
							}else if(data.getString("messageType").equals("file")) {
								// �ڷ������м�¼��Ϣ��Դ
								window.addClientFromContent("The server receives a file from (" + data.getString("username")+ "), the encrypted content is: " + data.getString("msg") + "\n\n");
								// �ڷ������м�¼��Ϣ����
								window.addClientToContent("The server forwards the file to the following user:\n");
							}
							for (int i = 0; i < clientList.size();) {
								if(data.getString("messageType").equals("message")) {
									window.addClientToContent("    " + clientList.get(i).getUserName() + "\n");
									// ����Ϣ���ݴ����ת�����ͻ���
									packMsg(i, data.getString("msg"), data, "");
									i++;
								}else if(data.getString("messageType").equals("file")) {
									window.addClientToContent("    " + clientList.get(i).getUserName() + "\n");
									// ���ļ���Ϣ�ͱ�ʶ������͸��ͻ���
									packMsg(i, data.getString("msg"), data, data.getString("fileName"));
									i++;
								}
							}
							window.addClientFromContent("\n");
						}
					}
				}
			} catch (IOException e) {
				System.err.println(e);
			}
		}

		// ����Ϣ���ݡ�ʱ�䡢�����߽���������
		// ͬʱ���������������û��б�����ͬ���ͻ��˵��б�
		public void packMsg(int i, String msg, JSONObject data, String fileName) {
			SimpleDateFormat dataType = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dataType.format(new Date()).toString();
			// packing data 
			JSONObject chatMessage = new JSONObject();
			chatMessage.put("userlist", usernamelist);
			chatMessage.put("msg", msg);
			chatMessage.put("time", time);
			// ��ȡ������
			User user = clientList.get(i);
			chatMessage.put("username", data.getString("username"));
			chatMessage.put("messageType", data.getString("messageType"));
			if(!fileName.equals("")) {
				chatMessage.put("fileName", fileName);
			}
			try {
				output = new DataOutputStream(user.getSocket().getOutputStream());
				output.writeUTF(chatMessage.toString());
			} catch (IOException e) {
				offLine(i);
			}
		}

		// �ͻ������ߣ����������Ϣ
		public void offLine(int i) {
			User outuser = clientList.get(i);
			// ���뵽�����б�
			offlineClientList.add(outuser);
			offlineUsernamelist.add(outuser.getUserName());
			// Removed from the list
			clientList.remove(i);
			usernamelist.remove(outuser.getUserName());
			// �������������û��б����
			window.updateUserlist(clientList, usernamelist);
			// Package the outgoing message that goes offline
			JSONObject out = new JSONObject();
			out.put("userlist", usernamelist);
			String offlineMessage = "-EXIT-";
			out.put("msg", offlineMessage);
			out.put("username", outuser.getUserName());
			SimpleDateFormat dataType = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dataType.format(new Date()).toString();
			out.put("time", time);
			out.put("messageType", "message");

			//Prompt each user to log off
			for (int j = 0; j < clientList.size(); j++) {
				try {
					User user = clientList.get(j);
					output = new DataOutputStream(user.getSocket().getOutputStream());
					output.writeUTF(out.toString());
				} catch (IOException ex1) {
				}
			}
		}
	}
	

	/**
	 * 	The server goes offline
	 */
	public void DisconnectServer() {
		try {
			SimpleDateFormat dataType = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String outlinetime = dataType.format(new Date()).toString();
			window.addContent("Server: Offline time --> " + outlinetime + "\n\n");
			// ������߳��ķ�����Ϣ
			JSONObject out = new JSONObject();
			out.put("userlist", usernamelist);
			out.put("username", "Server");
			out.put("messageType", "message");
			out.put("time", outlinetime);
			out.put("msg", "STOP");
			
			//Loop the UserList to notify each client server that it is quitting
			for (int j = 0; j < clientList.size(); j++) {
				try {
					User user = clientList.get(j);
					output = new DataOutputStream(user.getSocket().getOutputStream());
					output.writeUTF(out.toString());
				} catch (IOException ex1) {
				}
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

}