package liyunkai;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.List;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Font;
import javax.swing.JList;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ServerGUI {

	public JFrame frame;
	private TextArea content;
	private Server server;
	private JList<String> userlist;
	private JTextField txtOnlineUserList;

	/**
	 * Create the application.
	 */
	public ServerGUI(Server s) {
		this.server = s;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		content = new TextArea();
		content.setFont(new Font("Times New Roman", Font.BOLD, 20));
		content.setEditable(false);
		content.setBounds(278, 0, 706, 538);
		frame.getContentPane().add(content);
		
		JScrollPane userlistPane = new JScrollPane();
		userlistPane.setBounds(0, 0, 276, 540);
		frame.getContentPane().add(userlistPane);
		
		userlist = new JList<String>();
		userlist.setBackground(Color.LIGHT_GRAY);
		userlist.setFont(new Font("Times New Roman", Font.BOLD, 25));
		userlistPane.setViewportView(userlist);
		
		txtOnlineUserList = new JTextField();
		txtOnlineUserList.setBackground(Color.CYAN);
		txtOnlineUserList.setText("Online User List");
		txtOnlineUserList.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 30));
		userlistPane.setColumnHeaderView(txtOnlineUserList);
		txtOnlineUserList.setColumns(2);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu settingMenu = new JMenu("Setting");
		menuBar.add(settingMenu);
		
		JMenuItem disconnectButton = new JMenuItem("Disconnect");
		settingMenu.add(disconnectButton);
		disconnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				server.DisconnectServer();
			}
		});
		
		JMenuItem detailButton = new JMenuItem("Detail");
		settingMenu.add(detailButton);
		
		JMenu aboutMenu = new JMenu("About");
		menuBar.add(aboutMenu);
		
		JMenuItem aboutButton = new JMenuItem("About");
		aboutMenu.add(aboutButton);
	}
	
	// 添加服务器中消息展示部分内容
	public void addContent(String c) {
		content.append(c);
	}
	
	// 更新服务器中用户列表函数
	public void updateUserlist(ArrayList<User> clientList, ArrayList<String> usernameList) {
		DefaultListModel<String> model = new DefaultListModel<>();
		int i = 0;
	    for (String s : usernameList) {
	      model.add(i, s);
	      i = i + 1;
	    }
		userlist.setModel(model);
	}
}
