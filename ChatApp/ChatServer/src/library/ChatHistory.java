package library;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatHistory extends JFrame {
	private static final long serialVersionUID = 1L;
	private ArrayList<String> history = null;
	private String filename = null;
	private JPanel panel = new JPanel();
	private JTextArea area = new JTextArea();
	private JScrollPane scroll = new JScrollPane(area);
	private JButton remove = new JButton("Remove History");
	private int width,height = 750;
	public ChatHistory(String usrnm) {
		setSize(width,height);
		setResizable(true);
		setVisible(false);
		panel.setSize(740,740);
		scroll.setPreferredSize(new Dimension(width - 32,height - 100));
		remove.addActionListener(new rAction());
		panel.add(scroll);
		panel.add(remove);
		add(panel);
		history = new ArrayList<String>();
		filename = usrnm + ".txt";
		build();
	}

	private int build() {
		File f = new File(filename);
		if(!f.exists()) return 0;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				history.add(line);
				area.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return 1;
	}

	public int insertMsg(String text) {
		history.add(text);
		appendToFile(text);
		return 1;
	}

	private int appendToFile(String line) {
		File file = new File(filename);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);;
			bw.write(line + "\n");
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return 1;
	}

	public void displayHistory(){
		setVisible(true);
	}
	
	private int removeHistory() {
		File f = new File(filename);
		if(f.exists())
			f.delete();
		area.setText(null);
		return 1;
	}
	
	private class rAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			removeHistory();
			dispose();
		}
	}
}
