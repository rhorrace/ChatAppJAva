package clientserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

@SuppressWarnings("unused")
public class ChatWindow extends JFrame {
	public static void main(String[] args) {
		new ChatWindow(null);
	}

	private static final long serialVersionUID = 1L;
	private static int width = 800;
	private static int height = width / 16 * 9;
	private String sendTo;
	private static final JPanel panel = new JPanel();
	private static final JTextArea history = new JTextArea();
	private static final JScrollPane scroll = new JScrollPane(history);
	private static final JTextField message = new JTextField();
	private static final JButton chathistory = new JButton("Chat History");
	private static final JButton logout = new JButton("Logout");
	private static final JButton clear = new JButton("Clear History");
	private static final JButton online = new JButton("Users Online");
	private static final JButton send = new JButton("Send");

	public ChatWindow(String nm) {
		super(nm);
		if (nm == null || nm.trim().isEmpty())
			sendTo = null;
		else
			sendTo = "@" + nm;
		setSize(width, height);
		setResizable(true);
		setVisible(true);

		scroll.setPreferredSize(new Dimension(width - 32, height - 100));
		message.setPreferredSize(new Dimension(width - 32, 16));
		message.addKeyListener(new EnterPressed());
		panel.add(scroll);
		panel.add(message);
		if (sendTo == null || sendTo.trim().isEmpty()) {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			logout.addActionListener(new lAction());
			chathistory.addActionListener(new hAction());
			clear.addActionListener(new cAction());
			online.addActionListener(new oAction());
			send.addActionListener(new sAction());
			panel.add(logout);
			panel.add(chathistory);
			panel.add(clear);
			panel.add(online);
			panel.add(send);

		} else {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			panel.add(clear);
			panel.add(send);
		}
		add(panel);
	}

	public void writeToChat(String text) {
		history.append(text + '\n');
	}

	public void openOnline(String users) {
		new Online(users);
	}

	public void cleanChat() {
		history.setText(null);
	}

	public void close() {
		dispose();
	}

	private class hAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ChatClient.out("/history");
		}
		
	}
	
	private class lAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			ChatClient.out("/logout");
		}
	}

	private class cAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			cleanChat();
		}
	}

	private class oAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			ChatClient.out("/online");
		}
	}

	private class sAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String text = message.getText();
			String msg = (sendTo == null) ? text : sendTo + ":" + text;
			message.setText(null);
			ChatClient.out(msg);
		}
	}

	private class EnterPressed implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String text = message.getText();
				String msg = (sendTo == null) ? text : sendTo + ":" + text;
				message.setText(null);
				ChatClient.out(msg);
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

	}

	private class Online extends JFrame {
		private static final long serialVersionUID = 1L;
		private JPanel onlinepanel = new JPanel(new GridBagLayout());
		private JLabel[] users = new JLabel[10];

		public Online(String names) {
			if (names == null || names.trim().isEmpty()) {
				users[0] = new JLabel("No other users online");
				onlinepanel.add(users[0]);
			} else {
				String [] split = names.split("@");
				for (int i = 0; i < split.length; ++i) {
					users[i] = new JLabel(split[i]);
					users[i].addMouseListener(new labelAction());
					onlinepanel.add(users[i]);
				}
			}
			onlinepanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			add(onlinepanel);
			setSize(400, 300);
			setLocationRelativeTo(null);
			setVisible(true);
		}

		private class labelAction implements MouseListener {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JLabel label = (JLabel) e.getSource();
					String name = label.getText().trim();
					ChatWindow priv = new ChatWindow(name);
				}

			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		}
	}
}
