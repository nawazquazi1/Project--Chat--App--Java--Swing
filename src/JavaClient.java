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
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author nawaz
 */
public class JavaClient extends JFrame implements ActionListener, FocusListener, KeyListener {
	
	static String userName = "";
	static String ipAddress;
	
	static Socket socket;
	static PrintWriter writer;
	
	static JTextArea msgRec = new JTextArea(50, 100);
	static JTextArea msgSend = new JTextArea(50, 100);
	
	JScrollPane pane, pane2;
	
	JButton btnSend = new JButton("Send");
	
	public JavaClient() throws HeadlessException {
		super("Java Client");
		
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
		msgSend.setLineWrap(true);
		msgSend.setWrapStyleWord(true);
		msgSend.addKeyListener(this);
		add(pane2);
		
		btnSend.setBounds(0, 400, 400, 40);
		add(btnSend);
		btnSend.addActionListener(this);
		
//		this.addWindowFocusListener(new WindowFocusListener() {
//			@Override
//			public void windowLostFocus(WindowEvent e) {
//				if(!msgRec.getText().equals("")) {
//					System.out.println("Yes focus");
//					writer.println("\t <<<<<< " + userName + " is unavailable.");
//				}
//			}
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
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		userName = JOptionPane.showInputDialog("User Name (Client)");
		ipAddress = JOptionPane.showInputDialog("Enter Server ipAddress");
		
		new Thread(() -> new JavaClient()).start();
		
		socket = new Socket(ipAddress, 8888);
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
				socket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == btnSend) {
			sendMessage();
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
		// TODO Auto-generated method stub
		
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
