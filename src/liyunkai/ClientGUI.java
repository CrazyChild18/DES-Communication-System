package liyunkai;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import javax.swing.JScrollPane;

public class ClientGUI {

	public JFrame frmCommunicationSystem;
	public JTextField username_text;
	public JTextField state_text;
	private String username;
	private Client client;
	private JList<String> userlist;
	private JLabel send_to;
	private JButton broadcast_button;
	private JTextArea content;
	private boolean isPrivate = false;
	private boolean stateHint;
	private String privateName;
	private JTextArea beforeEncryptionArea, afterEncryptionArea;
	private JFileChooser fileDialog; // 文件对话框
	private JMenuItem disconnect_item, connect_item;
	private JButton send_button, changeKeyButton;
	private JTextField key_input;
	private String key;

	/**
	 * Create the application.
	 */
	public ClientGUI(String name, Client c, String k) {
		this.client = c;
		this.username = name;
		this.key = k;
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
		
		JMenuItem pic_item = new JMenuItem("File transmission");
		mnNewMenu.add(pic_item);
		fileDialog = new JFileChooser();
		pic_item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int state = fileDialog.showOpenDialog(frmCommunicationSystem);
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
		                //System.out.println("Read content: " + codeString);
		                if(isPrivate == false) {
		                	SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = data.format(new Date()).toString();
							setContent("You (" + time + "):\n" + "Send file \"" + name + "\" to all" + "\n\n");
							client.sendBroadcastFile(codeString, name);
						}else {
							SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = data.format(new Date()).toString();
							setContent("You (" + time + "):\n" + "Send file \"" + name + "\" to " + privateName + "\n\n");
							client.sendPrivateFile(codeString, name, privateName);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JMenu mnNewMenu_1 = new JMenu("Room State");
		menuBar.add(mnNewMenu_1);
		
		disconnect_item = new JMenuItem("Disconnect");
		mnNewMenu_1.add(disconnect_item);
		disconnect_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(stateHint == true) {
					client.disconnect();
					setState(false);
					cleanUserlist();
					delSendButton();
					setContent("You have logged out of the server.\n"
							+ "If you want to continue chatting, click connect\n\n\n");
				}else {
					JOptionPane.showMessageDialog(frmCommunicationSystem, "You are already offline", "Warning!!!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		connect_item = new JMenuItem("Connect");
		mnNewMenu_1.add(connect_item);
		connect_item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(stateHint == false) {
					int result = client.connect();
					if(result == 1) {
						setState(true);
						delSendButton();
						showSendButton();
						setContent("You have connected with the server.\n\n");
					}
				}else {
					JOptionPane.showMessageDialog(frmCommunicationSystem, "You are already online", "Warning!!!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		JMenu mnNewMenu_2 = new JMenu("About");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem About_item = new JMenuItem("About");
		mnNewMenu_2.add(About_item);
		
		JPanel panel = new JPanel();
		panel.setBounds(277, 603, 809, 160);
		frmCommunicationSystem.getContentPane().add(panel);
		panel.setLayout(null);
		
		send_button = new JButton("SEND");
		send_button.setFont(new Font("Times New Roman", Font.BOLD, 25));
		send_button.setBackground(new Color(153, 153, 153));
		send_button.setForeground(new Color(255, 255, 255));
		send_button.setBounds(670, 108, 139, 52);
		panel.add(send_button);
		
		JTextArea text_input = new JTextArea();
		text_input.setBounds(0, 0, 809, 160);
		text_input.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		panel.add(text_input);
		text_input.setCaretPosition(text_input.getText().length());
		
		send_button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 判断是否填入秘钥
				if(key.equals("")) {
					JOptionPane.showMessageDialog(frmCommunicationSystem, "The secret key can't be null!\nPlease enter the same secret key as agreed by the receiver.", "Warning!!!", JOptionPane.ERROR_MESSAGE);
				}else {
					String msg = text_input.getText().trim();
					setBeforeEncryptionArea(msg);
					// 判断群聊还是私聊
					// 调用不同发送函数
					if(isPrivate == false) {
						if (!msg.equals("") && !msg.equals("EXIT")) {
							SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = data.format(new Date()).toString();
							setContent("You (" + time + "):\n" + msg + "\n\n");
						}
						client.sendBroadcastMessage(msg);
					}else {
						if (!msg.equals("") && !msg.equals("EXIT")) {
							SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String time = data.format(new Date()).toString();
							setContent("You private send to " + privateName + " (" + time + "):\n" + msg + "\n\n");
						}
						client.sendPrivateMessage(msg, privateName);
					}
					text_input.setText("");
				}
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
				if(userlist.getValueIsAdjusting()) {
					System.out.println("检测到点击列表");
					getPrivateChatUsername();
				}
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
		lblUsername.setBounds(10, 0, 110, 37);
		panel_2.add(lblUsername);
		
		JLabel lblCurrentState = new JLabel("Current state:");
		lblCurrentState.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentState.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblCurrentState.setBounds(779, 0, 143, 37);
		panel_2.add(lblCurrentState);
		
		username_text = new JTextField(username);
		username_text.setHorizontalAlignment(SwingConstants.CENTER);
		username_text.setBackground(new Color(153, 153, 153));
		username_text.setFont(new Font("Times New Roman", Font.BOLD, 18));
		username_text.setEditable(false);
		username_text.setBounds(119, 0, 210, 37);
		panel_2.add(username_text);
		username_text.setColumns(10);
		
		state_text = new JTextField();
		state_text.setHorizontalAlignment(SwingConstants.CENTER);
		state_text.setBackground(new Color(153, 153, 153));
		state_text.setFont(new Font("Times New Roman", Font.BOLD, 16));
		state_text.setEditable(false);
		state_text.setColumns(10);
		state_text.setBounds(916, 1, 148, 37);
		panel_2.add(state_text);
		
		JLabel lblKey = new JLabel("Secret key:");
		lblKey.setForeground(Color.RED);
		lblKey.setHorizontalAlignment(SwingConstants.CENTER);
		lblKey.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblKey.setBounds(393, 0, 110, 37);
		panel_2.add(lblKey);
		
		key_input = new JTextField(key);
		key_input.setEditable(false);
		key_input.setHorizontalAlignment(SwingConstants.CENTER);
		key_input.setFont(new Font("Times New Roman", Font.BOLD, 16));
		key_input.setColumns(10);
		key_input.setBackground(new Color(204, 255, 255));
		key_input.setBounds(497, 1, 148, 37);
		panel_2.add(key_input);
		
		changeKeyButton = new JButton("Change");
		changeKeyButton.setForeground(new Color(0, 0, 0));
		changeKeyButton.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 12));
		changeKeyButton.setBackground(new Color(153, 153, 153));
		changeKeyButton.setBounds(655, 5, 85, 28);
		panel_2.add(changeKeyButton);
		changeKeyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String result = (String)JOptionPane.showInputDialog(frmCommunicationSystem, "Input new key:", "Change your secret key here",
						JOptionPane.PLAIN_MESSAGE, null, null, key);
				if(result != null && result.length() > 0){
	            	key_input.setText(result);
	            	key = result;
	            	client.changeKey(result);
	            }else {
	            	key_input.setText(key);
	            }
			}
		});
		
		
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
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(277, 77, 809, 363);
		frmCommunicationSystem.getContentPane().add(scrollPane);
		
		content = new JTextArea();
		content.setWrapStyleWord(true);
		content.setLineWrap(true);
		content.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		content.setEditable(false);
		content.setBackground(new Color(204, 204, 204));
		scrollPane.setViewportView(content);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(277, 439, 405, 113);
		frmCommunicationSystem.getContentPane().add(scrollPane_1);
		
		beforeEncryptionArea = new JTextArea();
		beforeEncryptionArea.setEditable(false);
		scrollPane_1.setViewportView(beforeEncryptionArea);
		
		JLabel lblNewLabel_1 = new JLabel("Before Encryption");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		scrollPane_1.setColumnHeaderView(lblNewLabel_1);
		
		JScrollPane scrollPane_1_1 = new JScrollPane();
		scrollPane_1_1.setBounds(681, 439, 405, 113);
		frmCommunicationSystem.getContentPane().add(scrollPane_1_1);
		
		afterEncryptionArea = new JTextArea();
		afterEncryptionArea.setEditable(false);
		scrollPane_1_1.setViewportView(afterEncryptionArea);
		
		JLabel lblNewLabel_2 = new JLabel("After Encryption");
		lblNewLabel_2.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		scrollPane_1_1.setColumnHeaderView(lblNewLabel_2);
		broadcast_button.addActionListener(new ActionListener() {
			// 转为所有人聊天
			@Override
			public void actionPerformed(ActionEvent e) {
				userlist.clearSelection();
				send_to.setText("Currently in Broadcast");
				System.out.println("转为broadcast");
				isPrivate = false;
			}
		});
		
	}
	
	public void delSendButton() {
		send_button.setVisible(false);
	}
	
	public void showSendButton() {
		send_button.setVisible(true);
	}
	
	public void setBeforeEncryptionArea(String c) {
		beforeEncryptionArea.setText(c);
		beforeEncryptionArea.setCaretPosition(0);
	}
	
	public void setAfterEncryptionArea(String c) {
		afterEncryptionArea.setText(c);
		afterEncryptionArea.setCaretPosition(0);
	}
	
	public void setContent(String text) {
		content.append(text);
		content.setCaretPosition(content.getDocument().getLength());
	}

	// 更新服务器中用户列表函数
	public void updateUserlist(ArrayList<String> usernameList) {
		System.out.println("开始更新用户列表");
		DefaultListModel<String> model = new DefaultListModel<>();
		int i = 0;
	    for (String s : usernameList) {
	      model.add(i, s);
	      i = i + 1;
	    }
		userlist.setModel(model);
	}

	// 更新服务器中用户列表函数
	public void cleanUserlist() {
		DefaultListModel<String> model = new DefaultListModel<>();
		userlist.setModel(model);
	}
	
	// 点击用户列表执行函数
	public void getPrivateChatUsername() {
		send_to.setText("Currently in Private. The receiver is: " + userlist.getSelectedValue());
		System.out.println("转为private");
		isPrivate = true;
		privateName = userlist.getSelectedValue();
	}

	public int saveFile(String fileContent, String senderName) {
		int state = fileDialog.showSaveDialog(frmCommunicationSystem);
        if (state == JFileChooser.APPROVE_OPTION) {
            try {
                // 保存文件
                File dir = fileDialog.getCurrentDirectory();
                String name = fileDialog.getSelectedFile().getName();
                File file = new File(dir, name);
                FileOutputStream out = new FileOutputStream(file);
                String codeString = fileContent;
                byte[] buff = Base64.decodeBase64(codeString);
                out.write(buff);
                out.close();
                content.append("The file from: " + senderName + " has been successfully received\n\n");
            } catch (IOException exp) {}
        }
		return 1;
	}

	public void setState(Boolean state) {
		if (state) {
			state_text.setText("Online");
			stateHint = true;
		}else {
			state_text.setText("Offline");
			stateHint = false;
		}
	}
}
