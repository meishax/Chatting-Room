package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import control.BackstageInterface;
import control.WindowInterface;
import util.MessageUdpUtil;
import util.MessageUdpUtil.ClientInfo;

import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;



/**
 * 登录界面
 * @author Mika
 */


public class LoginWindow implements WindowInterface {

	private JFrame frmLogin;
	private JTextField nicknameTextField;
	private JButton btnLogin;
	private JButton btnNewButton;
	private boolean isFindServer;
	private JLabel lblServerScaning;
	private BackstageInterface backstageInterface;

	/**
	 * Launch the application.
	 */
	public static void _main(final BackstageInterface bif) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow window = new LoginWindow(bif);
					window.frmLogin.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginWindow(BackstageInterface bif) {
		backstageInterface = bif;
		backstageInterface.setEchoMessageInterface(this);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmLogin = new JFrame();
		frmLogin.setTitle("Login");
		frmLogin.setBounds(100, 100, 320, 120);
		frmLogin.setResizable(false);
		frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmLogin.getContentPane().setLayout(springLayout);
		
		JLabel lblUser = new JLabel("Nickname");
		springLayout.putConstraint(SpringLayout.NORTH, lblUser, 16, SpringLayout.NORTH, frmLogin.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblUser, 10, SpringLayout.WEST, frmLogin.getContentPane());
		frmLogin.getContentPane().add(lblUser);
		
		nicknameTextField = new JTextField();
		nicknameTextField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//要求昵称输入框不能为空，服务器当前扫描在线，否则锁定登录按钮
				if(!nicknameTextField.getText().equals("") && isFindServer == true) {
					btnLogin.setEnabled(true);
				}
				else {
					btnLogin.setEnabled(false);
				}
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(isFindServer == true) {
					btnLogin.setEnabled(true);
				}
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		nicknameTextField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
					btnLogin.doClick();
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, nicknameTextField, 13, SpringLayout.NORTH, frmLogin.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, nicknameTextField, 6, SpringLayout.EAST, lblUser);
		frmLogin.getContentPane().add(nicknameTextField);
		nicknameTextField.setColumns(10);
		
		lblServerScaning = new JLabel("");
		springLayout.putConstraint(SpringLayout.WEST, lblServerScaning, 10, SpringLayout.WEST, frmLogin.getContentPane());
		frmLogin.getContentPane().add(lblServerScaning);
		
		btnNewButton = new JButton("Scan");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblServerScaning.setText("Server scaning..");
				btnNewButton.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						isFindServer = backstageInterface.scanServer();
						if(!isFindServer) {
							btnLogin.setEnabled(false);
							lblServerScaning.setText("Server no found.");
						}
						else {
							if(!nicknameTextField.getText().equals("")) {
								btnLogin.setEnabled(true);
							}
							lblServerScaning.setText("Found.");
						}
						btnNewButton.setEnabled(true);
					}
				}).start();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, lblServerScaning, 4, SpringLayout.NORTH, btnNewButton);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton, -10, SpringLayout.SOUTH, frmLogin.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton, 0, SpringLayout.EAST, nicknameTextField);
		frmLogin.getContentPane().add(btnNewButton);
		btnNewButton.doClick();
		
		btnLogin = new JButton("LOGIN");
		btnLogin.setEnabled(false);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnLogin.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String nickname = nicknameTextField.getText();
						backstageInterface.loginRequest(nickname);
					}
				}).start();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, nicknameTextField, -6, SpringLayout.WEST, btnLogin);
		springLayout.putConstraint(SpringLayout.SOUTH, btnLogin, 0, SpringLayout.SOUTH, btnNewButton);
		springLayout.putConstraint(SpringLayout.NORTH, btnLogin, 0, SpringLayout.NORTH, nicknameTextField);
		springLayout.putConstraint(SpringLayout.EAST, btnLogin, -10, SpringLayout.EAST, frmLogin.getContentPane());
		frmLogin.getContentPane().add(btnLogin);
	}

	@Override
	public void echoMessage(String message, String nickname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void otherFunc(boolean b) {
		// TODO Auto-generated method stub
		if(b == false) {
			JOptionPane.showMessageDialog(null, "昵称非法或昵称已存在。", "登录失败", JOptionPane.ERROR_MESSAGE);
		}
		else {
			backstageInterface.loadChatWindow();
			frmLogin.dispose();
		}
		btnLogin.setEnabled(true);
	}

	@Override
	public void addOrDeleteListItem(MessageUdpUtil.ClientInfo clientinfo, String nickname) {
		// TODO Auto-generated method stub
		
	}
}
