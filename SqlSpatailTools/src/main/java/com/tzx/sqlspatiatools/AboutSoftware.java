package com.tzx.sqlspatiatools;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

/**
 * 关于
 * @author Administrator
 *
 */
public class AboutSoftware extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AboutSoftware frame = new AboutSoftware();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AboutSoftware() {
		super("关于");
		setBounds(100, 100, 421, 253);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);// 只关闭当前窗口，不会关闭父窗口
		setResizable(false);
		
		JPanel panel = new JPanel() {
			ImageIcon icon;
			Image img;
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				//下面这行是为了背景图片可以跟随窗口自行调整大小，可以自己设置成固定大小
				URL u = getClass().getResource("tzx.png");
				icon=new ImageIcon(u);
				img=icon.getImage();
				g.drawImage(img, 0, 0,this.getWidth(), this.getHeight(), this);
			}

		};
		panel.setBounds(26, 32, 160, 160);
		contentPane.add(panel);
		
		JLabel label = new JLabel("北京天智祥信息科技有限公司");
		label.setBounds(217, 32, 207, 15);
		contentPane.add(label);
		
		JLabel label_1 = new JLabel("gaoyang、xuehonghao");
		label_1.setBounds(217, 80, 207, 15);
		contentPane.add(label_1);
		
		JLabel label_2 = new JLabel("0.0.1（试用版）");
		label_2.setBounds(217, 137, 207, 15);
		contentPane.add(label_2);
		
		
		
	}
}
