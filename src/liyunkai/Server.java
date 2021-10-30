package liyunkai;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import net.sf.json.JSON;
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
public class Server extends JFrame {

	//List of online users
	ArrayList<User> clientList = new ArrayList<User>();
	//List of online user names
	ArrayList<String> usernamelist = new ArrayList<String>();
	//Kick out the user name
	private String username_kick = null;
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
			//Create a server socket, bind port 8000
			ServerSocket serverSocket = new ServerSocket(8080);
			//Print startup time
			window.addContent("Server: Startup time --> " + new Date() + "\n\n");
			
			//Infinite loop listens for new client connections
			while (true) {

				//Listen for a new connection
				Socket socket = serverSocket.accept();

				//When there is client connected
				if (socket != null) {
					//get user info
					input = new DataInputStream(socket.getInputStream());
					String json = input.readUTF();
					JSONObject data = JSONObject.fromObject(json.toString());
					window.addContent("Client online: " + data.getString("username") + " at:" + new Date());

					//Display user IP
					InetAddress inetAddress = socket.getInetAddress();
					window.addContent(", IP address is��" + inetAddress.getHostAddress() + "\n\n");

					// Create a new user object, set socket, user name
					user = new User();
					user.setSocket(socket);
					user.setUserName(data.getString("username"));

					// ���·��������������û���Ϣ
					clientList.add(user);
					usernamelist.add(data.getString("username"));
					// �������������û��б����
					window.updateUserlist(clientList, usernamelist);
				}

				//When users are prompted to go online, package the data into JSON format
				JSONObject online = new JSONObject();
				online.put("userlist", usernamelist);
				online.put("msg", user.getUserName() + " logged in");
				online.put("sender", user.getUserName());

				//Prompt all users to have new users online
				for (int i = 0; i < clientList.size(); i++) {
					try {
						User user = clientList.get(i);
						//Get each user socket, get the output stream
						output = new DataOutputStream(user.getSocket().getOutputStream());
						//Send data to each client
						output.writeUTF(online.toString());
					} catch (IOException ex) {
						System.err.println(ex);
					}
				}
				
				//Socket as a parameter
				//Create a thread for the current connected user to listen for the socket's data
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
				//Gets the input stream from the socket client that this thread is listening to
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());

				//Cycle to monitor
				while (true) {

					//Get the client data
					String json = inputFromClient.readUTF();
					JSONObject data = JSONObject.fromObject(json.toString());
					
					
					if(data.getString("msg").equals("EXIT")) {
						// ������Ϣ
						// ִ�����ߺ�����֪ͨ
						for(int i = 0; i < clientList.size(); i++) {
							if (clientList.get(i).getUserName().equals(data.getString("username"))) {
								offLine(i);
								window.addContent("Client offline: " + data.getString("username") + " at:" + new Date());
							}
						}
					}else {
						boolean isPrivate = false;
						// ˽����Ϣ
						// �ҵ�Ŀ���û���������Ϣ
						for (int i = 0; i < clientList.size(); i++) {
							// �ҵ�˽�Ķ���
							System.out.println("�������ҵ��û�: " + clientList.get(i).getUserName());
							if (clientList.get(i).getUserName().equals(data.getString("isPrivChat"))) {
								// �ж�Ϊ�ı���Ϣ�����ļ���Ϣ
								if (data.getString("messageType").equals("message")) {
									// �༭�ı�����
									String msg = data.getString("username") + " (" + data.getString("time") + ") "+ " private send to you:\n" + 
											 data.getString("msg");
									// �����Ϣ���ݲ����͵�ָ���ͻ���
									System.out.println("��������װ��Ϣ");
									packMsg(i, msg, "", data);
									i++;
									// �Դ�����Ⱥ�����
									isPrivate = true;
									break;
								}else if(data.getString("messageType").equals("file")) {
									// �༭�ļ������Ϣ
									String msg = "Send file request detection to the user...";
									String fileContent = data.getString("msg");
									// �����Ϣ���ݲ����͵�ָ���ͻ���
									packMsg(i, msg, fileContent, data);
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
							for (int i = 0; i < clientList.size();) {
								System.out.println("�������ҵ��û�: " + clientList.get(i).getUserName());
								if(data.getString("messageType").equals("message")) {
									// ����Ϣ���ݴ����ת�����ͻ���
									String msg = data.getString("username") + "(" + data.getString("time") + "):\n"+ 
												data.getString("msg");
									packMsg(i, msg, "", data);
									i++;
								}else if(data.getString("messageType").equals("file")) {
									// ���ļ���Ϣ�ͱ�ʶ������͸��ͻ���
									String msg = "Send file request detection to the user...";
									String fileContent = data.getString("msg");
									packMsg(i, msg, fileContent, data);
									i++;
								}
							}
						}
					}
				}
			} catch (IOException e) {
				System.err.println(e);
			}
		}

		// ����Ϣ���ݡ�ʱ�䡢�����߽���������
		// ͬʱ���������������û��б�����ͬ���ͻ��˵��б�
		public void packMsg(int i, String msg, String fileContent, JSONObject data) {
			//packing data 
			JSONObject chatMessage = new JSONObject();
			chatMessage.put("userlist", usernamelist);
			chatMessage.put("msg", msg);
			if(!fileContent.equals("")) {
				chatMessage.put("fileContent", fileContent);
			}
			// ��ȡ������
			User user = clientList.get(i);
			chatMessage.put("sender", data.getString("username"));
			try {
				output = new DataOutputStream(user.getSocket().getOutputStream());
				output.writeUTF(chatMessage.toString());
				System.out.println("��������Ϣ���͸��û�: " + user.getUserName());
			} catch (IOException e) {
				offLine(i);
			}
		}

		// �ͻ������ߣ����������Ϣ
		public void offLine(int i) {
			User outuser = clientList.get(i);

			//Removed from the list
			clientList.remove(i);
			usernamelist.remove(outuser.getUserName());
			// �������������û��б����
			window.updateUserlist(clientList, usernamelist);
			//Package the outgoing message that goes offline
			JSONObject out = new JSONObject();
			out.put("userlist", usernamelist);
			out.put("msg", outuser.getUserName() + " exit\n");

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
			window.addContent("Server: Offline time --> " + new Date() + "\n\n");
			// ������߳��ķ�����Ϣ
			JSONObject out = new JSONObject();
			out.put("userlist", usernamelist);
			out.put("msg", "The server has been stop\n");
			
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