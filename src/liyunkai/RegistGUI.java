package liyunkai;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Window.Type;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RegistGUI {

	private JFrame RegisterWindows;
	private JTextField username_input;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegistGUI window = new RegistGUI();
					window.RegisterWindows.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RegistGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		RegisterWindows = new JFrame();
		RegisterWindows.getContentPane().setBackground(new Color(102, 102, 255));
		RegisterWindows.setType(Type.POPUP);
		RegisterWindows.setTitle("Welcome to DES Communication System");
		RegisterWindows.setIconImage(Toolkit.getDefaultToolkit().getImage("D:\\\u5B66\u4E60\\Github\\DES-Communication-System\\res\\logo.jpg"));
		RegisterWindows.setBounds(100, 100, 800, 500);
		RegisterWindows.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		RegisterWindows.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		panel.setBackground(new Color(102, 51, 255));
		panel.setBounds(0, 0, 224, 463);
		RegisterWindows.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("W");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 40));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setBounds(0, 69, 224, 47);
		panel.add(lblNewLabel);
		
		JLabel lblE = new JLabel("E");
		lblE.setHorizontalAlignment(SwingConstants.CENTER);
		lblE.setForeground(Color.WHITE);
		lblE.setFont(new Font("Times New Roman", Font.BOLD, 40));
		lblE.setBounds(0, 117, 224, 47);
		panel.add(lblE);
		
		JLabel lblL = new JLabel("L");
		lblL.setHorizontalAlignment(SwingConstants.CENTER);
		lblL.setForeground(Color.WHITE);
		lblL.setFont(new Font("Times New Roman", Font.BOLD, 40));
		lblL.setBounds(0, 164, 224, 47);
		panel.add(lblL);
		
		JLabel lblC = new JLabel("C");
		lblC.setHorizontalAlignment(SwingConstants.CENTER);
		lblC.setForeground(Color.WHITE);
		lblC.setFont(new Font("Times New Roman", Font.BOLD, 40));
		lblC.setBounds(0, 213, 224, 47);
		panel.add(lblC);
		
		JLabel lblO = new JLabel("O");
		lblO.setHorizontalAlignment(SwingConstants.CENTER);
		lblO.setForeground(Color.WHITE);
		lblO.setFont(new Font("Times New Roman", Font.BOLD, 40));
		lblO.setBounds(0, 261, 224, 47);
		panel.add(lblO);
		
		JLabel lblM = new JLabel("M");
		lblM.setHorizontalAlignment(SwingConstants.CENTER);
		lblM.setForeground(Color.WHITE);
		lblM.setFont(new Font("Times New Roman", Font.BOLD, 40));
		lblM.setBounds(0, 309, 224, 47);
		panel.add(lblM);
		
		JLabel lblE_1 = new JLabel("E");
		lblE_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblE_1.setForeground(Color.WHITE);
		lblE_1.setFont(new Font("Times New Roman", Font.BOLD, 40));
		lblE_1.setBounds(0, 357, 224, 47);
		panel.add(lblE_1);
		
		JLabel lblNewLabel_1 = new JLabel("Login to Explore");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD, 50));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(281, 96, 431, 55);
		RegisterWindows.getContentPane().add(lblNewLabel_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(102, 102, 255));
		panel_2.setForeground(new Color(255, 255, 255));
		panel_2.setBorder(null);
		panel_2.setBounds(300, 235, 105, 45);
		RegisterWindows.getContentPane().add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_2 = new JLabel("Username:");
		lblNewLabel_2.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblNewLabel_2.setForeground(new Color(255, 255, 255));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblNewLabel_2, BorderLayout.CENTER);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(255, 255, 255), 2, true));
		panel_3.setBackground(new Color(102, 102, 255));
		panel_3.setBounds(435, 235, 250, 45);
		RegisterWindows.getContentPane().add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		username_input = new JTextField();
		username_input.setHorizontalAlignment(SwingConstants.CENTER);
		username_input.setForeground(new Color(255, 255, 255));
		username_input.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 21));
		username_input.setBackground(new Color(102, 102, 255));
		panel_3.add(username_input);
		username_input.setColumns(10);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.setFont(new Font("Times New Roman", Font.BOLD, 30));
		btnNewButton.setForeground(new Color(255, 255, 255));
		btnNewButton.setBackground(new Color(102, 102, 255));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Get UserName
				String username = username_input.getText().trim();
 
				if (username.equals("")) {
					// The user name cannot be empty
					JOptionPane.showMessageDialog(RegisterWindows, "The username can't be null", "Warning!!!", JOptionPane.ERROR_MESSAGE);
				} else {
					// Close the Settings page and launch the chat box page
					RegisterWindows.dispose();
					new Client(username);
				}
			}
		});
		btnNewButton.setBounds(408, 337, 154, 45);
		RegisterWindows.getContentPane().add(btnNewButton);
	}
}
