package liyunkai;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

public class ClientGUI {

	public JFrame frmCommunicationSystem;
	public JTextField username_text;
	public JTextField state_text;
	private String username;
	private JTextArea content;
	private Client client;
	private JList<String> userlist;
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ClientGUI window = new ClientGUI();
//					window.frmCommunicationSystem.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

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
		
		JMenuItem TXT_item = new JMenuItem("TXT transmission");
		mnNewMenu.add(TXT_item);
		
		JMenu mnNewMenu_1 = new JMenu("Exit Room");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem exit_item = new JMenuItem("Exit");
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
				// TODO Auto-generated method stub
				System.out.println("12312313123131231231231213");
				String msg = text_input.getText().trim();
				client.sendMessage(msg);
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
		content.setBounds(277, 77, 809, 526);
		frmCommunicationSystem.getContentPane().add(content);
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

}
