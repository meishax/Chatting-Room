package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import control.BackstageInterface;
import control.WindowInterface;
import util.MessageUdpUtil;
import util.MessageUdpUtil.ClientInfo;
import util.HtmlUtil;
import util.MessageConstructor;

import javax.swing.SpringLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;




/**
 * 聊天界面
 * @author Mika
 */



public class ServerWindow implements WindowInterface {

	private JFrame frmServer;
	private JTable table;
	private JScrollPane scrollPane_1;
	private JTextField textField;
	private JButton btnSend;
	private JTextArea textArea;
	private BackstageInterface backstageInterface;
	private Vector<Vector<String>> dataVec;
	private JTextField txtAdminname;
	private JCheckBox checkBox;

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
					ServerWindow window = new ServerWindow(bif);
					window.frmServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerWindow(BackstageInterface bif) {
		backstageInterface = bif;
		backstageInterface.setEchoMessageInterface(this);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServer = new JFrame();
		frmServer.setResizable(false);
		frmServer.setTitle("Server");
		frmServer.setBounds(100, 100, 805, 479);
		frmServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmServer.getContentPane().setLayout(springLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 252, SpringLayout.NORTH, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, frmServer.getContentPane());
		frmServer.getContentPane().add(scrollPane);
		
		Vector<String> colHeader = new Vector<String>(); 
        colHeader.add("nickname"); 
        colHeader.add("ip address"); 
        colHeader.add("port"); 
        dataVec = new Vector<Vector<String>>(); 
		table = new JTable(dataVec, colHeader);
		scrollPane.setViewportView(table);
		/* test */
//
		
		scrollPane_1 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane_1, 6, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane_1, 10, SpringLayout.WEST, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane_1, 0, SpringLayout.EAST, scrollPane);
		frmServer.getContentPane().add(scrollPane_1);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if(arg0.isControlDown() && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						((Appendable) textField).append("\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					arg0.consume();
					btnSend.doClick();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		textField.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				btnSend.setEnabled(true);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(textField.getText().equals("")) {
					btnSend.setEnabled(false);
				}
			}
			
		});
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane_1.setViewportView(textArea);
		springLayout.putConstraint(SpringLayout.SOUTH, textField, -10, SpringLayout.SOUTH, frmServer.getContentPane());
		frmServer.getContentPane().add(textField);
		textField.setColumns(10);
		
		btnSend = new JButton("Send");
		springLayout.putConstraint(SpringLayout.EAST, textField, -6, SpringLayout.WEST, btnSend);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane_1, -4, SpringLayout.NORTH, btnSend);
		btnSend.setEnabled(false);
		btnSend.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String text = HtmlUtil.formatText2HTML(textField.getText(), checkBox.isSelected());
				String appendText = HtmlUtil.addSystemInfo(txtAdminname.getText(), text);
				System.out.println("appendText: "+appendText);
				textField.setText(null);
				textField.grabFocus();
				echoMessage(appendText, null);
				String msg = MessageConstructor.constructMessage(MessageConstructor.Code.UDPMessage.MESSAGE_FROM_SERVER_TO_CLIENT, appendText);
				backstageInterface.sendMessage(msg);
			}
			
		});

		springLayout.putConstraint(SpringLayout.SOUTH, btnSend, -10, SpringLayout.SOUTH, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnSend, 0, SpringLayout.EAST, scrollPane);
		frmServer.getContentPane().add(btnSend);
		
		txtAdminname = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, txtAdminname, 10, SpringLayout.WEST, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, txtAdminname, -10, SpringLayout.SOUTH, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtAdminname, -695, SpringLayout.EAST, frmServer.getContentPane());
		txtAdminname.setText("\u5446\u840C\u7684\u7BA1\u7406\u5458");
		frmServer.getContentPane().add(txtAdminname);
		txtAdminname.setColumns(10);
		
		checkBox = new JCheckBox("");
		springLayout.putConstraint(SpringLayout.WEST, textField, 6, SpringLayout.EAST, checkBox);
		springLayout.putConstraint(SpringLayout.SOUTH, checkBox, -10, SpringLayout.SOUTH, frmServer.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, checkBox, 6, SpringLayout.EAST, txtAdminname);
		frmServer.getContentPane().add(checkBox);
	}

	@Override
	public void echoMessage(String message, String nickname) {
		// TODO Auto-generated method stub
		//我要获取当前的日期
		Date date = new Date();
		//设置要获取到什么样的时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//获取String类型的时间
		String createdate = sdf.format(date);
		textArea.append(message + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	private void addUser2Table(ClientInfo clientInfo) {
		Vector<String> newLine = new Vector<String>(); 
		newLine.add(clientInfo.getNickname());
		newLine.add(clientInfo.getAddress());
		newLine.add(String.valueOf(clientInfo.getPort()));
		dataVec.add(newLine);
		table.updateUI();
	}
	private void deleteUserFromTable(String nickname) {
		for (int i = 0;i < dataVec.size(); i++) {
			Vector<String> data = dataVec.get(i);
			if(data.get(0).equals(nickname)) {
				dataVec.remove(data);
			}
		}
		table.updateUI();
	}

	@Override
	public void otherFunc(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addOrDeleteListItem(ClientInfo clientInfo, String nickname) {
		// TODO Auto-generated method stub
		if(clientInfo == null) {
			System.out.println("Delete:" + nickname);
			deleteUserFromTable(nickname);
		}
		if(nickname == null) {
			System.out.println("Add:" + clientInfo.toString());
			addUser2Table(clientInfo);
		}
	}
}
