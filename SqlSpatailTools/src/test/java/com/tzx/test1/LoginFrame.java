package com.tzx.test1;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class LoginFrame extends JFrame {
	JButton button = new JButton("点击我");

	class LoginOKAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "将进入另一个窗体！");
			new MainFrame();
			setVisible(false);
		}
	}

	public LoginFrame() {
		super();
		this.setResizable(false);
		this.setSize(new Dimension(300, 205));
		this.setTitle("第一个窗体");
		this.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocation(300, 200);
		this.setVisible(true);
		this.getContentPane().add(button, null);
		button.setBounds(new Rectangle(111, 70, 78, 27));
		button.addActionListener(new LoginOKAction());// 给按钮加事件监听器
	}

	public static void main(String[] args) {
		new LoginFrame();
	}
}