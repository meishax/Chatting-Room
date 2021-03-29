package view;

import java.awt.EventQueue;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import control.BackstageInterface;

import javax.swing.JButton;
import java.awt.event.ActionEvent;



/**
 * 选择服务器还是客户端窗口
 * @author Mika
 */


public class Choose {

	private JFrame frame;
	private JButton btnServer;
	private JButton btnClient;
	private BackstageInterface backstageInterface;
	
	public static final int CLIENT = 1;
	public static final int SERVER = 2;

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
					Choose window = new Choose(bif);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Choose(BackstageInterface bif) {
		backstageInterface = bif;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 223, 96);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(e.getSource() == btnServer) {
					backstageInterface.returnChoose(SERVER);
				}
				if(e.getSource() == btnClient) {
					backstageInterface.returnChoose(CLIENT);
				}
				frame.dispose();
			}
		};
		
		btnServer = new JButton("Server");
		springLayout.putConstraint(SpringLayout.NORTH, btnServer, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnServer, -10, SpringLayout.EAST, frame.getContentPane());
		btnServer.addActionListener(actionListener);
		frame.getContentPane().add(btnServer);
		
		btnClient = new JButton("Client");
		springLayout.putConstraint(SpringLayout.NORTH, btnClient, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnClient, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnServer, 0, SpringLayout.SOUTH, btnClient);
		springLayout.putConstraint(SpringLayout.WEST, btnClient, 10, SpringLayout.WEST, frame.getContentPane());
		btnClient.addActionListener(actionListener);
		frame.getContentPane().add(btnClient);
	}
	
}
