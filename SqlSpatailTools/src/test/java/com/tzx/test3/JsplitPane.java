package com.tzx.test3;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSplitPane;

public class JsplitPane extends JFrame {
	private JSplitPane jSplitPane;
	private JLabel jLabel;
	private JList jList;

	public static void main(String[] args) {
		new JsplitPane();
	}

	public JsplitPane() {
		String[] words = { "Java", "Python", "Golang" };
		jLabel = new JLabel(new ImageIcon("F:\\xhh\\projects\\SqlSpatialTools\\outputs\\tzx.ico"));
		jList = new JList(words);
		// JSplitPane 拆分窗格，垂直拆分方式
		jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jList, jLabel);
		this.add(jSplitPane);
		// 设置JFrame属性
		this.setTitle("工程");
		this.setLocation(500, 250);
		this.setSize(350, 200);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
}
