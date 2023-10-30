import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author nawaz
 */
public class JavaServer extends JFrame implements ActionListener, FocusListener, KeyListener {
	
	static ServerSocket server;
	static Socket socket;
	static PrintWriter writer;
	static String userName = "";
	
	static JTextArea msgRec = new JTextArea(50, 100);
	static JTextArea msgSend = new JTextArea(50, 100);
	
	JScrollPane pane, pane2;
	
	JButton btnSend = new JButton("Send");
	
	JMenuBar menuBar = new JMenuBar();
	JMenu messenger = new JMenu("Messenger");
	JMenuItem logout = new JMenuItem("log out");
	
	JMenu help = new JMenu("Help");
	JMenuItem about = new JMenuItem("about");
	JMenuItem s_keys = new JMenuItem("Shortcut Keys");
	
	public JavaServer() throws HeadlessException {
		super("Java Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 410, 500);
		setResizable(false);
		setLayout(null);
		setLocationRelativeTo(null);
		
		pane = new JScrollPane(msgRec);
		pane.setBounds(0, 0, 400, 200);
		msgRec.setEditable(false);
		msgRec.setBackground(Color.BLACK);
		msgRec.setForeground(Color.WHITE);
		add(pane);
		
		pane2 = new JScrollPane(msgSend);
		pane2.setBounds(0, 200, 400, 200);
		msgSend.setBackground(Color.LIGHT_GRAY);
		msgSend.setText("Type message here");
		msgSend.addFocusListener(this);
		msgSend.addKeyListener(this);
		msgSend.setLineWrap(true);
		msgSend.setWrapStyleWord(true);
		add(pane2);
		
		btnSend.setBounds(0, 400, 400, 40);
		add(btnSend);
		btnSend.addActionListener(this);
		
		menuBar.add(messenger);
		messenger.add(logout);
		logout.addActionListener(this);
		
		menuBar.add(help);
		help.add(about);
		help.add(s_keys);
		s_keys.addActionListener(this);
		about.addActionListener(this);
		
		setJMenuBar(menuBar);
		
//		this.addWindowFocusListener(new WindowFocusListener() {
//			
//			@Override
//			public void windowLostFocus(WindowEvent e) {
//				if(!msgRec.getText().equals("")) {
//					System.out.println("Yes focus");
//					writer.println("\t <<<<<< " + userName + " is unavailable.");
//				}
//			}
//			
//			@Override
//			public void windowGainedFocus(WindowEvent e) {
//				if(!msgRec.getText().equals("")) {
//					System.out.println("Yes focus");
//					writer.println("\t <<<<<< " + userName + " is focused to communication.");
//				}
//			}
//		});
		
		if(userName != null && userName != "") {
			setVisible(true);
		} else {
			System.exit(0);
		}
	}
	
	public static void main(String[] args) throws IOException {
		userName = JOptionPane.showInputDialog("User Name (Server)");
		
		new Thread(() -> new JavaServer()).start();
		
		server = new ServerSocket(8888);
		System.out.println(server.getInetAddress().getLocalHost());
		
		socket = server.accept();
		msgRec.setText("Connected!");
		
		new Thread(() -> {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line = null;
				while((line = reader.readLine()) != null) {
					msgRec.append("\n" + line);
				}
			} catch (IOException e) {
				try {
					socket.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}).start();
		
		try {
			writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception e) {
			try {
				server.close();
				socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == logout) {
			System.exit(0);
		} else if(source == about) {
			JOptionPane.showMessageDialog(this,
					"Messenger 1.0 \n Development by nawaz quazi.");
		} else if(source == btnSend) {
			sendMessage();
		} else if(source == s_keys) {
			JOptionPane.showMessageDialog(this, "(shift + Enter) for new line while typing"
					+ "(ctrl + X) to quit");
		}
	}

	private void sendMessage() {
		writer.println(userName + " : " + msgSend.getText());
		
		msgRec.append("\nMe: " + msgSend.getText());
		
		msgSend.setText("");
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(e.getSource() == msgSend) {
			msgSend.setText("");
		} else {
			writer.println("\t *** " + userName + " ... is typing");
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if((e.getKeyCode() == KeyEvent.VK_ENTER) && e.isShiftDown()) {
			msgSend.append("\n");
		} else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			sendMessage();
		} else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			System.exit(0);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}










