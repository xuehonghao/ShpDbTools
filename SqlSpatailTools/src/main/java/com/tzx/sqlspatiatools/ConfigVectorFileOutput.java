package com.tzx.sqlspatiatools;

import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * 配置矢量文件输出
 * @author Administrator
 *
 */
public class ConfigVectorFileOutput extends JDialog {

	private JPanel contentPane;
	private JTextField textField;
	private JButton btnNewButton_1;

	private String selecteddir;

	private MainWindow mainWindow;

	/**
	 * 来源类型 新格式和旧格式 值为 新 旧
	 */
	private String sourceType;

	private JFileChooser chooser = new JFileChooser();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigVectorFileOutput frame = new ConfigVectorFileOutput(new int[] { 0 }, new MainWindow(), "");
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
	public ConfigVectorFileOutput(int[] row, MainWindow mainWindow, String sourceType) {
		setTitle("配置矢量文件输出");
		this.mainWindow = mainWindow;
		this.sourceType = sourceType;
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 483, 202);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setResizable(false);
		// setModal(true);
//		setAlwaysOnTop(true);

		// 添加选择目录项
		addDirSelect();

		// 添加确认按钮
		addConfirmButton(row);

		// 初始化数据
		if (row.length == 1) {
			if (mainWindow != null) {
				for (int i = 0; i < row.length; i++) {
					for (int j = 0; j < ConfigDbInputDsNewWindow.getDatabaseDetailList().size(); j++) {
						if (Integer.valueOf(
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							selecteddir = ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputPath();
							textField.setText(selecteddir);
							
							break;
						}
					}

					for (int j = 0; j < ConfigDbInputDsOldWindow.getDatabaseDetailList().size(); j++) {
						if (Integer.valueOf(
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							selecteddir = ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputPath();
							textField.setText(selecteddir);
							
							break;
						}
					}
				}

				mainWindow.RefreshInputListTable();
			} else {
				for (int i = 0; i < row.length; i++) {
					switch (sourceType) {
					case "矢量文件（新格式）":
						// 暂不支持矢量文件转矢量文件
						break;
					case "矢量文件（旧格式）":
						// 暂不支持矢量文件转矢量文件
						break;
					case "数据库（新格式）":
						selecteddir = ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputPath();
						textField.setText(selecteddir);
						break;
					case "数据库（旧格式）":
						selecteddir = ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputPath();
						textField.setText(selecteddir);
						
						break;

					}
				}
			}

		}

	}

	/**
	 * 添加选择目录项
	 */
	public void addDirSelect() {
		textField = new JTextField();
		textField.setBounds(26, 28, 272, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnNewButton = new JButton("选择目录");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectPath = "";

				chooser.setCurrentDirectory(chooser.getSelectedFile());
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置只能选择目录
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					selectPath = chooser.getSelectedFile().getPath();
					System.out.println("你选择的目录是：" + selectPath);
				}
				selecteddir = selectPath;
				if (selecteddir == null) {
					return;
				} else {
					System.out.println("您选中的文件夹目录为：" + selecteddir);
					textField.setText(selecteddir);
				}
			}
		});
		btnNewButton.setBounds(328, 27, 93, 23);
		contentPane.add(btnNewButton);
	}

	/**
	 * 添加确认按钮
	 */
	public void addConfirmButton(int[] row) {
		btnNewButton_1 = new JButton("确定");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (mainWindow != null) {
					for (int i = 0; i < row.length; i++) {
						for (int j = 0; j < ConfigDbInputDsNewWindow.getDatabaseDetailList().size(); j++) {
							if (Integer.valueOf(
									ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
									- 1 == row[i]) {
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputPath(selecteddir);
								break;
							}
						}

						for (int j = 0; j < ConfigDbInputDsOldWindow.getDatabaseDetailList().size(); j++) {
							if (Integer.valueOf(
									ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
									- 1 == row[i]) {
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputPath(selecteddir);
								break;
							}
						}
					}

					mainWindow.RefreshInputListTable();
				} else {
					for (int i = 0; i < row.length; i++) {
						switch (sourceType) {
						case "矢量文件（新格式）":
							// 暂不支持矢量文件转矢量文件
							break;
						case "矢量文件（旧格式）":
							// 暂不支持矢量文件转矢量文件
							break;
						case "数据库（新格式）":
							ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputPath(selecteddir);
							break;
						case "数据库（旧格式）":
							ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputPath(selecteddir);
							break;

						}
					}
				}

				// 关闭当前窗口
				dispose();
			}
		});
		btnNewButton_1.setBounds(328, 108, 93, 23);
		contentPane.add(btnNewButton_1);
	}
}
