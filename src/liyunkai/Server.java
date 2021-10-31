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
				// 接收到上线连接
				if (socket != null) {
					// 获得上线用户信息
					input = new DataInputStream(socket.getInputStream());
					String json = input.readUTF();
					JSONObject data = JSONObject.fromObject(json.toString());
					window.addContent("Client online: " + data.getString("username") + " at: " + new Date() + "\n\n");
					// Create a new user object
					user = new User();
					user.setSocket(socket);
					user.setUserName(data.getString("username"));
					// 更新服务器储存在线用户信息
					clientList.add(user);
					usernamelist.add(data.getString("username"));
					// 将服务器在线用户列表更新
					window.addContent("Update userlist at: " + new Date() + "\n\n");
					window.updateUserlist(clientList, usernamelist);
				}

				// 创建新的服务器消息，并发送给客户端
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
				
				// 为客户端创建线程
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
						// 下线消息
						// 执行下线函数来通知
						for(int i = 0; i < clientList.size(); i++) {
							if (clientList.get(i).getUserName().equals(data.getString("username"))) {
								offLine(i);
								window.addContent("Client ("  + data.getString("username") + ") offline at:" + new Date() + "\n\n");
							}
						}
					}else if(data.getString("msg").equals("Connect!!!")) {
						System.out.println("服务器收到重新连接请求");
						// 重新上线消息
						window.addContent("Client online: " + data.getString("username") + " at: " + new Date() + "\n\n");
						// Create a new user object
						for(int i = 0; i < offlineClientList.size(); i++) {
							if(offlineUsernamelist.get(i).equals(data.getString("username"))) {
								user = offlineClientList.get(i);
							}
						}
						// 更新服务器储存在线用户信息
						clientList.add(user);
						usernamelist.add(data.getString("username"));
						// 将服务器在线用户列表更新
						window.addContent("Update userlist at: " + new Date() + "\n\n");
						window.updateUserlist(clientList, usernamelist);
						// 创建新的服务器消息，并发送给客户端
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
								System.out.println("服务器转发重新连接请求给: " + user.getUserName());
							} catch (IOException ex) {
								System.err.println(ex);
							}
						}
					}else {
						boolean isPrivate = false;
						// 私聊消息
						// 找到目标用户并发送消息
						for (int i = 0; i < clientList.size(); i++) {
							// 找到私聊对象
							if (clientList.get(i).getUserName().equals(data.getString("isPrivChat"))) {
								// 判断为文本消息还是文件消息
								if (data.getString("messageType").equals("message")) {
									// 在服务器中记录消息来源
									window.addClientFromContent("The server receives a message from (" + data.getString("username")+ "), the encrypted content is: " + data.getString("msg") + "\n\n");
									// 打包消息内容并发送到指定客户端
									packMsg(i, data.getString("msg"), data, "");
									// 在服务器中记录消息走向
									window.addClientToContent("The server forwards the message to the user: " + data.getString("isPrivChat") + "\n\n");
									i++;
									// 以此跳过群发检测
									isPrivate = true;
									break;
								}else if(data.getString("messageType").equals("file")) {
									// 在服务器中记录消息来源
									window.addClientFromContent("The server receives a file from (" + data.getString("username")+ "), the encrypted content is: " + data.getString("msg") + "\n\n");
									// 打包消息内容并发送到指定客户端
									packMsg(i, data.getString("msg"), data, data.getString("fileName"));
									// 在服务器中记录消息走向
									window.addClientToContent("The server forwards the file to the user: " + data.getString("isPrivChat") + "\n\n");
									i++;
									// 以此跳过群发检测
									isPrivate = true;
									break;
								}
							}
						}
						// 群聊消息
						// 向列表所有用户发送消息
						if (isPrivate == false) {
							if(data.getString("messageType").equals("message")) {
								// 在服务器中记录消息来源
								window.addClientFromContent("The server receives a message from (" + data.getString("username")+ "), the encrypted content is: " + data.getString("msg") + "\n\n");
								// 在服务器中记录消息走向
								window.addClientToContent("The server forwards the message to the following user:\n");
							}else if(data.getString("messageType").equals("file")) {
								// 在服务器中记录消息来源
								window.addClientFromContent("The server receives a file from (" + data.getString("username")+ "), the encrypted content is: " + data.getString("msg") + "\n\n");
								// 在服务器中记录消息走向
								window.addClientToContent("The server forwards the file to the following user:\n");
							}
							for (int i = 0; i < clientList.size();) {
								if(data.getString("messageType").equals("message")) {
									window.addClientToContent("    " + clientList.get(i).getUserName() + "\n");
									// 将消息内容打包并转发给客户端
									packMsg(i, data.getString("msg"), data, "");
									i++;
								}else if(data.getString("messageType").equals("file")) {
									window.addClientToContent("    " + clientList.get(i).getUserName() + "\n");
									// 将文件信息和标识打包发送给客户端
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

		// 对信息内容、时间、发送者进行整理打包
		// 同时包含服务器在线用户列表，用以同步客户端的列表
		public void packMsg(int i, String msg, JSONObject data, String fileName) {
			SimpleDateFormat dataType = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dataType.format(new Date()).toString();
			// packing data 
			JSONObject chatMessage = new JSONObject();
			chatMessage.put("userlist", usernamelist);
			chatMessage.put("msg", msg);
			chatMessage.put("time", time);
			// 获取接受者
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

		// 客户端下线，打包下线消息
		public void offLine(int i) {
			User outuser = clientList.get(i);
			// 加入到下线列表
			offlineClientList.add(outuser);
			offlineUsernamelist.add(outuser.getUserName());
			// Removed from the list
			clientList.remove(i);
			usernamelist.remove(outuser.getUserName());
			// 将服务器在线用户列表更新
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
			// 打包被踢出的发送消息
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