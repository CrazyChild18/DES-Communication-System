package liyunkai;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Font;
import javax.swing.JList;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

public class ServerGUI {

	public JFrame frame;
	private Server server;
	private JTextField txtOnlineUserList;
	private JList<String> userlist;
	private JTextArea ClientFromArea;
	private JTextArea content;
	private JTextArea ClientToArea;

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
		
		JScrollPane userlistPane = new JScrollPane();
		userlistPane.setBounds(0, 0, 275, 540);
		frame.getContentPane().add(userlistPane);
		
		txtOnlineUserList = new JTextField();
		txtOnlineUserList.setBackground(Color.CYAN);
		txtOnlineUserList.setText("Online User List");
		txtOnlineUserList.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 30));
		userlistPane.setColumnHeaderView(txtOnlineUserList);
		txtOnlineUserList.setColumns(2);
		
		userlist = new JList<String>();
		userlist.setFont(new Font("Times New Roman", Font.BOLD, 25));
		userlist.setBackground(Color.LIGHT_GRAY);
		userlistPane.setViewportView(userlist);
		
		JPanel panel = new JPanel();
		panel.setBounds(272, 0, 714, 540);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(Color.DARK_GRAY, 2));
		panel_1.setBounds(0, 271, 357, 269);
		panel.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Message From Client");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 22));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 0, 357, 46);
		panel_1.add(lblNewLabel);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(64, 64, 64), 2));
		panel_2.setBounds(0, 44, 357, 225);
		panel_1.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_2.add(scrollPane_1, BorderLayout.CENTER);
		
		ClientFromArea = new JTextArea();
		ClientFromArea.setWrapStyleWord(true);
		ClientFromArea.setEditable(false);
		ClientFromArea.setFont(new Font("Times New Roman", Font.BOLD, 16));
		scrollPane_1.setViewportView(ClientFromArea);
		
		JPanel panel_1_1 = new JPanel();
		panel_1_1.setBorder(new LineBorder(Color.DARK_GRAY, 2));
		panel_1_1.setLayout(null);
		panel_1_1.setBounds(357, 271, 357, 269);
		panel.add(panel_1_1);
		
		JLabel ClientToTitle = new JLabel("Message Send To Client");
		ClientToTitle.setHorizontalAlignment(SwingConstants.CENTER);
		ClientToTitle.setFont(new Font("Times New Roman", Font.BOLD, 22));
		ClientToTitle.setBounds(0, 0, 357, 46);
		panel_1_1.add(ClientToTitle);
		
		JPanel panel_2_1 = new JPanel();
		panel_2_1.setBorder(new LineBorder(new Color(64, 64, 64), 2));
		panel_2_1.setBounds(0, 44, 357, 225);
		panel_1_1.add(panel_2_1);
		panel_2_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		panel_2_1.add(scrollPane_2, BorderLayout.CENTER);
		
		ClientToArea = new JTextArea();
		ClientToArea.setWrapStyleWord(true);
		ClientToArea.setFont(new Font("Times New Roman", Font.BOLD, 16));
		ClientToArea.setEditable(false);
		scrollPane_2.setViewportView(ClientToArea);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 0, 714, 269);
		panel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_3.add(scrollPane, BorderLayout.CENTER);
		
		content = new JTextArea();
		content.setRows(10);
		content.setColumns(8);
		content.setFont(new Font("Times New Roman", Font.BOLD, 20));
		content.setEditable(false);
		scrollPane.setViewportView(content);
		
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
		content.setCaretPosition(content.getDocument().getLength());
	}
	
	// 添加服务器中消息展示部分内容
	public void addClientFromContent(String c) {
		ClientFromArea.append(c);
		ClientFromArea.setCaretPosition(ClientFromArea.getDocument().getLength());
	}
	
	// 添加服务器中消息展示部分内容
	public void addClientToContent(String c) {
		ClientToArea.append(c);
		ClientToArea.setCaretPosition(ClientToArea.getDocument().getLength());
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
