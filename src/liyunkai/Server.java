package liyunkai;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
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
					window.addContent(", IP address is：" + inetAddress.getHostAddress() + "\n\n");

					//Create a new user object, set socket, user name
					user = new User();
					user.setSocket(socket);
					user.setUserName(data.getString("username"));

					//Join the list of online user groups
					clientList.add(user);
					//Add user name list (users are displayed in the client user list)
					usernamelist.add(data.getString("username"));
					
					
					// 将服务器在线用户列表更新
					window.updateUserlist(clientList, usernamelist);
				}

				//When users are prompted to go online, package the data into JSON format
				JSONObject online = new JSONObject();
				online.put("userlist", usernamelist);
				online.put("msg", user.getUserName() + " logged in");

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
						//If is EXIT, the offLine function is executed
						for(int i = 0; i < clientList.size(); i++) {
							if (clientList.get(i).getUserName().equals(data.getString("username"))) {
								offLine(i);
								window.addContent("Client offline: " + data.getString("username") + " at:" + new Date());
							}
						}
					}else {
						boolean isPrivate = false;

						// Private chat, the acquired data forward to the designated user
						for (int i = 0; i < clientList.size(); i++) {
							// 找到私聊对象
							if (clientList.get(i).getUserName().equals(data.getString("isPrivChat"))) {
								// 判断为文本消息还是文件消息
								if (data.getString("messageType").equals("message")) {
									// 编辑文本内容
									String msg = data.getString("username") + " private send to you:\n" + 
											"(" + data.getString("time") + ") "+ data.getString("msg") + "\n\n";
									// 打包消息内容并发送到指定客户端
									packMsg(i, msg, "");
									i++;
									// 以此跳过群发检测
									isPrivate = true;
									break;
								}else if(data.getString("messageType").equals("file")) {
									// 编辑文件检测信息
									String msg = "Send file request detection to the user...";
									String fileContent = data.getString("msg");
									// 打包消息内容并发送到指定客户端
									packMsg(i, msg, fileContent);
									i++;
									// 以此跳过群发检测
									isPrivate = true;
									break;
								}
							}
						}
						//group chat
						//Forward the acquired data to each user
						if (isPrivate == false) {
							for (int i = 0; i < clientList.size();) {
								if(data.getString("messageType").equals("message")) {
									//The chat information and user list are packaged into JSON format and sent to each client
									String msg = data.getString("username") + ":\n" + 
											"(" + data.getString("time") + ") " + data.getString("msg") + "\n\n";
									packMsg(i, msg, "");
									i++;
								}else if(data.getString("messageType").equals("file")) {
									//The chat information and user list are packaged into JSON format and sent to each client
									String msg = "Send file request detection to the user...";
									String fileContent = data.getString("msg");
									packMsg(i, msg, fileContent);
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

		// The chat information and user list are packaged into JSON format and sent to a client
		public void packMsg(int i, String msg, String fileContent) {
			//packing data 
			JSONObject chatMessage = new JSONObject();
			chatMessage.put("userlist", usernamelist);
			chatMessage.put("msg", msg);
			if(!fileContent.equals("")) {
				chatMessage.put("fileContent", fileContent);
			}
			//Get a user
			User user = clientList.get(i);
			chatMessage.put("sender", user.getUserName());
			//Send a message to the fetch user
			try {
				output = new DataOutputStream(user.getSocket().getOutputStream());
				output.writeUTF(chatMessage.toString());
			} catch (IOException e) {
				//Prevents the client from shutting down directly rather than quitting using instructions
				offLine(i);
			}

		}

		//Prompt log off
		public void offLine(int i) {
			User outuser = clientList.get(i);

			//Removed from the list
			clientList.remove(i);
			usernamelist.remove(outuser.getUserName());
			// 将服务器在线用户列表更新
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
			// 打包被踢出的发送消息
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