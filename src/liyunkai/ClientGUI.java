package liyunkai;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.codec.binary.Base64;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class ClientGUI {

	public JFrame frmCommunicationSystem;
	public JTextField username_text;
	public JTextField state_text;
	private String username;
	private JTextArea content;
	private Client client;
	private JList<String> userlist;
	private JLabel send_to;
	private JButton broadcast_button;
	
	private boolean isPrivate = false;
	private String privateName;
	private JFileChooser fileDialog; // 文件对话框


	/**
	 * Create the application.
	 */
	public ClientGUI(String name, Client c) {
		this.client = c;
		this.username = name;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCommunicationSystem = new JFrame();
		frmCommunicationSystem.setTitle("Communication System");
		frmCommunicationSystem.setBounds(100, 100, 1100, 800);
		frmCommunicationSystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCommunicationSystem.getContentPane().setLayout(null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 1086, 40);
		frmCommunicationSystem.getContentPane().add(menuBar);
		
		JMenu mnNewMenu = new JMenu("File Operation");
		menuBar.add(mnNewMenu);
		
		JMenuItem pic_item = new JMenuItem("Picture transmission");
		mnNewMenu.add(pic_item);
		fileDialog = new JFileChooser();
		pic_item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int state = fileDialog.showOpenDialog(null);
				if (state == JFileChooser.APPROVE_OPTION) {
					// 打开文件
					try {
						File dir = fileDialog.getCurrentDirectory();
		                String name = fileDialog.getSelectedFile().getName();
		                File file = new File(dir, name);
		                FileInputStream in;
						in = new FileInputStream(file);
						byte[] buff = new byte[in.available()];
		                in.read(buff);
		                in.close();
		                String codeString = Base64.encodeBase64String(buff);
		                System.out.println("Read content: " + codeString);
		                if(isPrivate == false) {
							client.sendBroadcastFile(codeString, name);
						}else {
							client.sendPrivateMessage(codeString, privateName);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JMenuItem TXT_item = new JMenuItem("TXT transmission");
		mnNewMenu.add(TXT_item);
		
		JMenu mnNewMenu_1 = new JMenu("Exit Room");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem exit_item = new JMenuItem("Disconnect");
		mnNewMenu_1.add(exit_item);
		
		JMenu mnNewMenu_2 = new JMenu("About");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem About_item = new JMenuItem("About");
		mnNewMenu_2.add(About_item);
		
		JPanel panel = new JPanel();
		panel.setBounds(277, 603, 809, 160);
		frmCommunicationSystem.getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton send_button = new JButton("SEND");
		send_button.setFont(new Font("Times New Roman", Font.BOLD, 25));
		send_button.setBackground(new Color(153, 153, 153));
		send_button.setForeground(new Color(255, 255, 255));
		send_button.setBounds(670, 108, 139, 52);
		panel.add(send_button);
		
		JTextArea text_input = new JTextArea();
		text_input.setBounds(0, 0, 809, 160);
		text_input.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		panel.add(text_input);
		
		send_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = text_input.getText().trim();
				// 判断群聊还是私聊
				// 调用不同发送函数
				if(isPrivate == false) {
					client.sendBroadcastMessage(msg);
				}else {
					client.sendPrivateMessage(msg, privateName);
				}
				text_input.setText("");
			}
		});
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_1.setBounds(0, 77, 277, 686);
		frmCommunicationSystem.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Online User");
		lblNewLabel.setBackground(new Color(51, 102, 255));
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 30));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 0, 277, 57);
		panel_1.add(lblNewLabel);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel_3.setBounds(0, 52, 277, 634);
		panel_1.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		userlist = new JList<String>();
		userlist.setFont(new Font("Times New Roman", Font.BOLD, 30));
		panel_3.add(userlist);
		userlist.addListSelectionListener(new ListSelectionListener() {
			// 添加对list中item的点击事件，以此实现私聊
			@Override
			public void valueChanged(ListSelectionEvent e) {
				getPrivateChatUsername();
			}
		});
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(null);
		panel_2.setBounds(0, 40, 1086, 37);
		frmCommunicationSystem.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setBounds(86, 0, 110, 37);
		panel_2.add(lblUsername);
		
		JLabel lblCurrentState = new JLabel("Current state:");
		lblCurrentState.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentState.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblCurrentState.setBounds(620, 0, 143, 37);
		panel_2.add(lblCurrentState);
		
		username_text = new JTextField(username);
		username_text.setHorizontalAlignment(SwingConstants.CENTER);
		username_text.setBackground(new Color(153, 153, 153));
		username_text.setFont(new Font("Times New Roman", Font.BOLD, 18));
		username_text.setEditable(false);
		username_text.setBounds(206, 0, 250, 37);
		panel_2.add(username_text);
		username_text.setColumns(10);
		
		state_text = new JTextField();
		state_text.setHorizontalAlignment(SwingConstants.CENTER);
		state_text.setBackground(new Color(153, 153, 153));
		state_text.setFont(new Font("Times New Roman", Font.BOLD, 16));
		state_text.setEditable(false);
		state_text.setColumns(10);
		state_text.setBounds(773, 0, 148, 37);
		panel_2.add(state_text);
		
		content = new JTextArea();
		content.setLineWrap(true);
		content.setEditable(false);
		content.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		content.setBackground(new Color(204, 204, 204));
		content.setBounds(277, 77, 809, 475);
		frmCommunicationSystem.getContentPane().add(content);
		
		send_to = new JLabel("Currently in Broadcast");
		send_to.setForeground(new Color(255, 0, 0));
		send_to.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 17));
		send_to.setHorizontalAlignment(SwingConstants.CENTER);
		send_to.setBounds(277, 555, 809, 50);
		frmCommunicationSystem.getContentPane().add(send_to);
		
		broadcast_button = new JButton("SEND TO ALL");
		broadcast_button.setFont(new Font("Times New Roman", Font.BOLD, 12));
		broadcast_button.setBounds(955, 562, 121, 33);
		frmCommunicationSystem.getContentPane().add(broadcast_button);
		broadcast_button.addActionListener(new ActionListener() {
			// 转为所有人聊天
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				send_to.setText("Currently in Broadcast");
			}
		});
		
	}
	
	public void setContent(String text) {
		content.append(text);
	}

	// 更新服务器中用户列表函数
	public void updateUserlist(ArrayList<String> usernameList) {
		DefaultListModel<String> model = new DefaultListModel<>();
		int i = 0;
	    for (String s : usernameList) {
	      model.add(i, s);
	      i = i + 1;
	    }
		userlist.setModel(model);
	}

	// 点击用户列表执行函数
	public void getPrivateChatUsername() {
		send_to.setText("Currently in Private. The receiver is: " + userlist.getSelectedValue());
		isPrivate = true;
		privateName = userlist.getSelectedValue();
	}
}
