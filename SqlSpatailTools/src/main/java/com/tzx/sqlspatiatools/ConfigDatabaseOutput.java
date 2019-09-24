package com.tzx.sqlspatiatools;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzx.datasource.DataSourceBase;
import com.tzx.datasource.DataSourceFactory;
import com.tzx.datasource.DbDataSource;
import com.tzx.datasource.EDataSourceType;
import com.tzx.datasource.argument.DbConfigArgs;
import com.tzx.sqlspatiatools.bean.DataBaseInfos;
import com.tzx.sqlspatiatools.utils.CreateFileUtil;

import javax.swing.JComboBox;

/**
 * 配置数据库输出
 * 
 * @author Administrator
 *
 */
public class ConfigDatabaseOutput extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private ButtonGroup buttonGroup;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;
	private JRadioButton rdbtnNewRadioButton_2;
	private JRadioButton rdbtnNewRadioButton_3;

	private DataBaseInfos dataBaseInfos;
	private JButton button;
	private JComboBox comboBox;
	private MainWindow mainWindow;
	private JButton button_1;

	private Gson gson = new Gson();
	private File jsonFile = new File("./databaseinfo.json");

	private static ConfigDatabaseOutput frame;

	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	/**
	 * 来源类型 新格式和旧格式 值为 新 旧
	 */
	private String sourceType;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ConfigDatabaseOutput(new int[] { 0, 1 }, new MainWindow(), "");
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
	public ConfigDatabaseOutput(int[] row, MainWindow mainWindow, String sourceType) {
		super("配置数据库输出");
		this.mainWindow = mainWindow;
		this.sourceType = sourceType;
		setVisible(true);
		dataBaseInfos = new DataBaseInfos();

		// 设置窗口属性
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);// 只关闭当前窗口，不会关闭父窗口
		setBounds(100, 100, 596, 391);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setResizable(false);

		// 生成图形信息选项
		addInsert();

		// 添加确认按钮
		JButton btnNewButton = addConfirm(row);
		contentPane.add(btnNewButton);

		JLabel label = new JLabel("数据库名：");
		label.setBounds(10, 248, 101, 15);
		contentPane.add(label);

		comboBox = new JComboBox();
		comboBox.setBounds(144, 245, 366, 21);
		contentPane.add(comboBox);

		button_1 = new JButton("历史记录");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DatabaseHistory(null, frame);
			}
		});
		button_1.setBounds(10, 302, 93, 23);
		contentPane.add(button_1);

		if (row.length == 1) {

			if (mainWindow != null) {
				for (int i = 0; i < row.length; i++) {
					for (int j = 0; j < ConfigVectorFileDsNewWindow.getVfList().size(); j++) {
						if (Integer.valueOf(ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							textField.setText(
									ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs().getInputDbHost());

							textField_3.setText(ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs()
									.getInputDbPassword());
							textField_1.setText(
									ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs().getInputDbPort());
							String type = ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
									.getInputDbType();
							switch (type == null ? "" : type) {
							case "MSSQL":
								buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
								break;

							case "MySQL":
								buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true);
								break;

							case "Oracle":
								buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true);
								break;

							case "PostGIS":
								buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true);
								break;

							default:
								buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true);
								break;
							}

							textField_2.setText(
									ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs().getInputDbUser());
							if (ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs()
									.getInputDbUser() != null) {
								// 获取值
								setDbInfo();
								// 配置参数
								getDbList();
							}
							comboBox.setSelectedItem(
									ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs().getInputDbName());
							break;
						}
					}

					for (int j = 0; j < ConfigVectorFileDsOldWindow.getVfList().size(); j++) {
						if (Integer.valueOf(ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							textField.setText(
									ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs().getInputDbHost());

							textField_3.setText(ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs()
									.getInputDbPassword());
							textField_1.setText(
									ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs().getInputDbPort());
							String type = ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
									.getInputDbType();
							switch (type == null ? "" : type) {
							case "MSSQL":
								buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
								break;

							case "MySQL":
								buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true); // 默认选择
								break;

							case "Oracle":
								buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true); // 默认选择
								break;

							case "PostGIS":
								buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true); // 默认选择
								break;

							default:
								buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
								break;
							}

							textField_2.setText(
									ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs().getInputDbUser());
							if (ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs()
									.getInputDbUser() != null) {
								// 获取值
								setDbInfo();
								// 配置参数
								getDbList();
							}
							comboBox.setSelectedItem(
									ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs().getInputDbName());
							break;
						}
					}

					for (int j = 0; j < ConfigDbInputDsNewWindow.getDatabaseDetailList().size(); j++) {
						if (Integer.valueOf(
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							textField.setText(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbHost());

							textField_3.setText(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbPassword());
							textField_1.setText(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbPort());
							String type = ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.getInputDbType();
							switch (type == null ? "" : type) {
							case "MSSQL":
								buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
								break;

							case "MySQL":
								buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true); // 默认选择
								break;

							case "Oracle":
								buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true); // 默认选择
								break;

							case "PostGIS":
								buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true); // 默认选择
								break;

							default:
								buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
								break;
							}
							textField_2.setText(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbUser());
							if (ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbUser() != null) {
								// 获取值
								setDbInfo();
								// 配置参数
								getDbList();
							}

							comboBox.setSelectedItem(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j)
									.getOutputArgs().getInputDbName());
							break;
						}
					}

					for (int j = 0; j < ConfigDbInputDsOldWindow.getDatabaseDetailList().size(); j++) {
						if (Integer.valueOf(
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							textField.setText(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbHost());

							textField_3.setText(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbPassword());
							textField_1.setText(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbPort());
							String type = ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.getInputDbType();
							switch (type == null ? "" : type) {
							case "MSSQL":
								buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
								break;

							case "MySQL":
								buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true); // 默认选择
								break;

							case "Oracle":
								buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true); // 默认选择
								break;

							case "PostGIS":
								buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true); // 默认选择
								break;

							default:
								buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
								break;
							}
							textField_2.setText(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbUser());
							if (ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
									.getInputDbUser() != null) {
								// 获取值
								setDbInfo();
								// 配置参数
								getDbList();
							}

							comboBox.setSelectedItem(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j)
									.getOutputArgs().getInputDbName());
							break;
						}
					}
				}
				mainWindow.RefreshInputListTable();
			} else {
				switch (sourceType) {
				case "矢量文件（新格式）":
					for (int i = 0; i < row.length; i++) {
						textField.setText(
								ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs().getInputDbHost());

						textField_3.setText(ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
								.getInputDbPassword());
						textField_1.setText(
								ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs().getInputDbPort());
						String type = ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
								.getInputDbType();
						switch (type == null ? "" : type) {
						case "MSSQL":
							buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
							break;

						case "MySQL":
							buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true); // 默认选择
							break;

						case "Oracle":
							buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true); // 默认选择
							break;

						case "PostGIS":
							buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true); // 默认选择
							break;

						default:
							buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
							break;
						}

						textField_2.setText(
								ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs().getInputDbUser());
						if (ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
								.getInputDbUser() != null) {
							// 获取值
							setDbInfo();
							// 配置参数
							getDbList();
						}

						comboBox.setSelectedItem(
								ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs().getInputDbName());
					}
					break;
				case "矢量文件（旧格式）":
					for (int i = 0; i < row.length; i++) {
						textField.setText(
								ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs().getInputDbHost());

						textField_3.setText(ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
								.getInputDbPassword());
						textField_1.setText(
								ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs().getInputDbPort());
						String type = ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
								.getInputDbType();
						switch (type == null ? "" : type) {
						case "MSSQL":
							buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
							break;

						case "MySQL":
							buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true); // 默认选择
							break;

						case "Oracle":
							buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true); // 默认选择
							break;

						case "PostGIS":
							buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true); // 默认选择
							break;

						default:
							buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
							break;
						}

						textField_2.setText(
								ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs().getInputDbUser());
						if (ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
								.getInputDbUser() != null) {
							// 获取值
							setDbInfo();
							// 配置参数
							getDbList();
						}

						comboBox.setSelectedItem(
								ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs().getInputDbName());
					}
					break;
				case "数据库（新格式）":
					for (int i = 0; i < row.length; i++) {
						textField.setText(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbHost());

						textField_3.setText(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbPassword());
						textField_1.setText(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbPort());
						String type = ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbType();
						switch (type == null ? "" : type) {
						case "MSSQL":
							buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
							break;

						case "MySQL":
							buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true); // 默认选择
							break;

						case "Oracle":
							buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true); // 默认选择
							break;

						case "PostGIS":
							buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true); // 默认选择
							break;

						default:
							buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
							break;
						}
						textField_2.setText(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbUser());
						if (ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbUser() != null) {
							// 获取值
							setDbInfo();
							// 配置参数
							getDbList();

						}

						comboBox.setSelectedItem(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i])
								.getOutputArgs().getInputDbName());

					}
					break;
				case "数据库（旧格式）":
					for (int i = 0; i < row.length; i++) {
						textField.setText(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbHost());

						textField_3.setText(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbPassword());
						textField_1.setText(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbPort());
						String type = ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbType();
						switch (type == null ? "" : type) {
						case "MSSQL":
							buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
							break;

						case "MySQL":
							buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true); // 默认选择
							break;

						case "Oracle":
							buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true); // 默认选择
							break;

						case "PostGIS":
							buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true); // 默认选择
							break;

						default:
							buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择
							break;
						}
						textField_2.setText(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbUser());
						if (ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
								.getInputDbUser() != null) {
							// 获取值
							setDbInfo();
							// 配置参数
							getDbList();
						}
						comboBox.setSelectedItem(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i])
								.getOutputArgs().getInputDbName());
					}
					break;

				}
			}
		}

	}

	/**
	 * 生成添加信息选项
	 */
	public void addInsert() {
		buttonGroup = new ButtonGroup();

		JLabel lblNewLabel = new JLabel("数据库类型：");
		lblNewLabel.setBounds(10, 25, 101, 15);
		contentPane.add(lblNewLabel);

		rdbtnNewRadioButton = new JRadioButton("MSSQL");
		rdbtnNewRadioButton.setBounds(133, 17, 72, 23);
		contentPane.add(rdbtnNewRadioButton);

		rdbtnNewRadioButton_1 = new JRadioButton("MySQL");
		rdbtnNewRadioButton_1.setBounds(242, 17, 72, 23);
		contentPane.add(rdbtnNewRadioButton_1);

		rdbtnNewRadioButton_2 = new JRadioButton("Oracle");
		rdbtnNewRadioButton_2.setBounds(345, 17, 72, 23);
		contentPane.add(rdbtnNewRadioButton_2);

		rdbtnNewRadioButton_3 = new JRadioButton("PostGIS");
		rdbtnNewRadioButton_3.setBounds(447, 17, 77, 23);
		contentPane.add(rdbtnNewRadioButton_3);

		buttonGroup.add(rdbtnNewRadioButton);
		buttonGroup.add(rdbtnNewRadioButton_1);
		buttonGroup.add(rdbtnNewRadioButton_2);
		buttonGroup.add(rdbtnNewRadioButton_3);
		buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true); // 默认选择

		rdbtnNewRadioButton.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("focusGained   MSSQL");
				if (textField_3.getText() == null || textField_3.getText().equals("")) {
					textField_1.setText("1433");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
			}

		});

		rdbtnNewRadioButton_1.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("focusGained  MySQL");
				if (textField_3.getText() == null || textField_3.getText().equals("")) {
					textField_1.setText("3306");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
			}

		});

		rdbtnNewRadioButton_2.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("focusGained  Oracle");
				if (textField_3.getText() == null || textField_3.getText().equals("")) {
					textField_1.setText("1521");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
			}

		});

		rdbtnNewRadioButton_3.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("focusGained  PostGIS");
				if (textField_3.getText() == null || textField_3.getText().equals("")) {
					textField_1.setText("5432");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
			}

		});

		JLabel lblNewLabel_1 = new JLabel("数据库地址：");
		lblNewLabel_1.setBounds(10, 68, 101, 15);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("数据库端口：");
		lblNewLabel_2.setBounds(10, 114, 101, 15);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("数据库用户名：");
		lblNewLabel_3.setBounds(10, 159, 124, 15);
		contentPane.add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("数据库密码：");
		lblNewLabel_4.setBounds(10, 207, 101, 15);
		contentPane.add(lblNewLabel_4);

		textField = new JTextField();
		textField.setBounds(144, 62, 366, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(144, 108, 366, 21);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		textField_2 = new JTextField();
		textField_2.setBounds(144, 153, 366, 21);
		contentPane.add(textField_2);
		textField_2.setColumns(10);

		textField_3 = new JTextField();
		textField_3.setBounds(144, 201, 366, 21);
		textField_3.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {// 失去焦点时
				System.out.println("失去了焦点");
				// 获取值
				setDbInfo();
				// 配置参数
				getDbList();
			}

			public void focusGained(FocusEvent e) {// 获得焦点时
//		        System.out.println("获得了焦点");

			}
		});
		contentPane.add(textField_3);
		textField_3.setColumns(10);

	}

	/**
	 * 生成确认按钮
	 * 
	 * @return
	 */
	public JButton addConfirm(int[] row) {
		JButton btnNewButton = new JButton("确定");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 非空校验
				boolean isEffective = verification();
				if (!isEffective) {
					return;
				}

				// 获取页面上的值
				setDbInfo();

				if (mainWindow != null) {
					for (int i = 0; i < row.length; i++) {
						for (int j = 0; j < ConfigVectorFileDsNewWindow.getVfList().size(); j++) {// 矢量文件（新格式）
							if (Integer
									.valueOf(ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs().getTaskNo())
									- 1 == row[i]) {
								ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs()
										.setInputDbHost(textField.getText());
								ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs()
										.setInputDbName(comboBox.getSelectedItem().toString());
								ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs()
										.setInputDbPassword(textField_3.getText());
								ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs()
										.setInputDbPort(textField_1.getText());

								String inputDbType = "";
								if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {// MSSQL
									inputDbType = rdbtnNewRadioButton.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {// MySQL
									inputDbType = rdbtnNewRadioButton_1.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {// Oracle
									inputDbType = rdbtnNewRadioButton_2.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {// PostGIS
									inputDbType = rdbtnNewRadioButton_3.getText();
								}

								ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs()
										.setInputDbType(inputDbType);

								ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs()
										.setInputDbUser(textField_2.getText());
								break;
							}
						}

						for (int j = 0; j < ConfigVectorFileDsOldWindow.getVfList().size(); j++) {// 矢量文件（旧格式）
							if (Integer
									.valueOf(ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs().getTaskNo())
									- 1 == row[i]) {
								ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs()
										.setInputDbHost(textField.getText());
								ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs()
										.setInputDbName(comboBox.getSelectedItem().toString());
								ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs()
										.setInputDbPassword(textField_3.getText());
								ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs()
										.setInputDbPort(textField_1.getText());

								String inputDbType = "";
								if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {// MSSQL
									inputDbType = rdbtnNewRadioButton.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {// MySQL
									inputDbType = rdbtnNewRadioButton_1.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {// Oracle
									inputDbType = rdbtnNewRadioButton_2.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {// PostGIS
									inputDbType = rdbtnNewRadioButton_3.getText();
								}
								ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs()
										.setInputDbType(inputDbType);

								ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs()
										.setInputDbUser(textField_2.getText());
								break;
							}
						}

						for (int j = 0; j < ConfigDbInputDsNewWindow.getDatabaseDetailList().size(); j++) {// 数据库（新格式）
							if (Integer.valueOf(
									ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
									- 1 == row[i]) {
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbHost(textField.getText());
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbName(comboBox.getSelectedItem().toString());
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbPassword(textField_3.getText());
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbPort(textField_1.getText());

								String inputDbType = "";
								if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {// MSSQL
									inputDbType = rdbtnNewRadioButton.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {// MySQL
									inputDbType = rdbtnNewRadioButton_1.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {// Oracle
									inputDbType = rdbtnNewRadioButton_2.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {// PostGIS
									inputDbType = rdbtnNewRadioButton_3.getText();
								}
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbType(inputDbType);

								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbUser(textField_2.getText());
								break;
							}
						}

						for (int j = 0; j < ConfigDbInputDsOldWindow.getDatabaseDetailList().size(); j++) {// 数据库旧格式
							if (Integer.valueOf(
									ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
									- 1 == row[i]) {
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbHost(textField.getText());
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbName(comboBox.getSelectedItem().toString());
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbPassword(textField_3.getText());
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbPort(textField_1.getText());

								String inputDbType = "";
								if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {// MSSQL
									inputDbType = rdbtnNewRadioButton.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {// MySQL
									inputDbType = rdbtnNewRadioButton_1.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {// Oracle
									inputDbType = rdbtnNewRadioButton_2.getText();
								} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {// PostGIS
									inputDbType = rdbtnNewRadioButton_3.getText();
								}
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbType(inputDbType);

								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs()
										.setInputDbUser(textField_2.getText());
								break;
							}
						}
					}
					mainWindow.RefreshInputListTable();
				} else {
					switch (sourceType) {
					case "矢量文件（新格式）":
						for (int i = 0; i < row.length; i++) {
							ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbHost(textField.getText());
							ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbName(comboBox.getSelectedItem().toString());
							ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbPassword(textField_3.getText());
							ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbPort(textField_1.getText());

							String inputDbType = "";
							if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {// MSSQL
								inputDbType = rdbtnNewRadioButton.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {// MySQL
								inputDbType = rdbtnNewRadioButton_1.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {// Oracle
								inputDbType = rdbtnNewRadioButton_2.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {// PostGIS
								inputDbType = rdbtnNewRadioButton_3.getText();
							}
							ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbType(inputDbType);

							ConfigVectorFileDsNewWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbUser(textField_2.getText());
						}
						break;
					case "矢量文件（旧格式）":
						for (int i = 0; i < row.length; i++) {
							ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbHost(textField.getText());
							ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbName(comboBox.getSelectedItem().toString());
							ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbPassword(textField_3.getText());
							ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbPort(textField_1.getText());

							String inputDbType = "";
							if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {// MSSQL
								inputDbType = rdbtnNewRadioButton.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {// MySQL
								inputDbType = rdbtnNewRadioButton_1.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {// Oracle
								inputDbType = rdbtnNewRadioButton_2.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {// PostGIS
								inputDbType = rdbtnNewRadioButton_3.getText();
							}
							ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbType(inputDbType);

							ConfigVectorFileDsOldWindow.getVfList().get(row[i]).getOutputArgs()
									.setInputDbUser(textField_2.getText());
						}
						break;
					case "数据库（新格式）":
						for (int i = 0; i < row.length; i++) {
							ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbHost(textField.getText());
							ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbName(comboBox.getSelectedItem().toString());
							ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbPassword(textField_3.getText());
							ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbPort(textField_1.getText());

							String inputDbType = "";
							if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {// MSSQL
								inputDbType = rdbtnNewRadioButton.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {// MySQL
								inputDbType = rdbtnNewRadioButton_1.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {// Oracle
								inputDbType = rdbtnNewRadioButton_2.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {// PostGIS
								inputDbType = rdbtnNewRadioButton_3.getText();
							}
							ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbType(inputDbType);

							ConfigDbInputDsNewWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbUser(textField_2.getText());
						}
						break;
					case "数据库（旧格式）":
						for (int i = 0; i < row.length; i++) {
							ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbHost(textField.getText());
							ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbName(comboBox.getSelectedItem().toString());
							ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbPassword(textField_3.getText());
							ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbPort(textField_1.getText());

							String inputDbType = "";
							if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {// MSSQL
								inputDbType = rdbtnNewRadioButton.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {// MySQL
								inputDbType = rdbtnNewRadioButton_1.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {// Oracle
								inputDbType = rdbtnNewRadioButton_2.getText();
							} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {// PostGIS
								inputDbType = rdbtnNewRadioButton_3.getText();
							}
							ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbType(inputDbType);

							ConfigDbInputDsOldWindow.getDatabaseDetailList().get(row[i]).getOutputArgs()
									.setInputDbUser(textField_2.getText());
						}
						break;

					}
				}

//				JOptionPane.showMessageDialog(null, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
				// 关闭当前窗口
				dispose();

			}
		});
		btnNewButton.setBounds(431, 302, 93, 23);
		return btnNewButton;
	}

	/**
	 * 非空校验
	 * 
	 * @return
	 */
	public boolean verification() {
//		if(!textField.getText().trim().equals("")) {
//			if(!textField.getText().contains("&") && rdbtnNewRadioButton_2.getText().equals("Oracle")) {
//				JOptionPane.showMessageDialog(this, "Oracle数据库需要在数据库地址后拼接&符号和服务名");
//				return false;
//			}
//		}
		if (textField.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请输入数据库地址");
			return false;
		} else if (textField_1.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请输入数据库端口");
			return false;
		} else if (textField_2.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请输入数据库用户名");
			return false;
		} else if (textField_3.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "请输入数据库密码");
			return false;
		} else if (comboBox.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(this, "请选择数据库名");
			return false;
		}

		return true;
	}

	/**
	 * 设置数据库参数信息
	 */
	public void setDbInfo() {
		// 1、获取数据库类型
		if (buttonGroup.getSelection().equals(rdbtnNewRadioButton.getModel())) {
			dataBaseInfos.setDbType(rdbtnNewRadioButton.getText());
		} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_1.getModel())) {
			dataBaseInfos.setDbType(rdbtnNewRadioButton_1.getText());
		} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_2.getModel())) {
			dataBaseInfos.setDbType(rdbtnNewRadioButton_2.getText());
		} else if (buttonGroup.getSelection().equals(rdbtnNewRadioButton_3.getModel())) {
			dataBaseInfos.setDbType(rdbtnNewRadioButton_3.getText());
		}
		// 2、获取数据库地址
		dataBaseInfos.setDbHost(textField.getText());
		// 3、获取数据库端口
		dataBaseInfos.setDbPort(textField_1.getText());
		// 4、获取数据库用户名
		dataBaseInfos.setDbUser(textField_2.getText());
		// 5、获取数据库密码
		dataBaseInfos.setDbPassword(textField_3.getText());

	}

	/**
	 * 初始化数据库信息
	 */
	public void initDbInfo(int rowNum) {
		if (jsonFile.exists()) {// 如果databaseinfo.json文件存在，则取文件中的第一条数据填在界面上
			// 读取json文件
			String laststr = CreateFileUtil.getDatafromFile("databaseinfo");
			List<DataBaseInfos> dbInfoList = gson.fromJson(laststr, new TypeToken<List<DataBaseInfos>>() {
			}.getType());

			if (dbInfoList != null && dbInfoList.size() > 0) {
				// 设置数据库类型
				switch (dbInfoList.get(rowNum).getDbType()) {
				case "MSSQL":
					buttonGroup.setSelected(rdbtnNewRadioButton.getModel(), true);
					break;
				case "MySQL":
					buttonGroup.setSelected(rdbtnNewRadioButton_1.getModel(), true);
					break;
				case "Oracle":
					buttonGroup.setSelected(rdbtnNewRadioButton_2.getModel(), true);
					break;
				case "PostGIS":
					buttonGroup.setSelected(rdbtnNewRadioButton_3.getModel(), true);
					break;
				}

				// 设置数据库地址
				textField.setText(dbInfoList.get(rowNum).getDbHost());

				// 设置数据库端口
				textField_1.setText(dbInfoList.get(rowNum).getDbPort());

				// 设置数据库用户名
				textField_2.setText(dbInfoList.get(rowNum).getDbUser());

				// 设置数据库密码
				textField_3.setText(dbInfoList.get(rowNum).getDbPassword());

			}

			// 获取值
			setDbInfo();
			// 配置参数
			getDbList();
		}
	}

	/**
	 * 配置参数获得数据库名
	 */
	public void getDbList() {
		EDataSourceType dataSourceType = null;
		String dbDataSource = null;
		String dbName = "";
		String host = dataBaseInfos.getDbHost();
		switch (dataBaseInfos.getDbType()) {
		case "MSSQL":
			dataSourceType = EDataSourceType.MSSQL;
			dbDataSource = DbDataSource.SQLSERVER_DBTYPE;
			dbName = "master";
			break;
		case "MySQL":
			dataSourceType = EDataSourceType.MYSQL;
			dbDataSource = DbDataSource.MYSQL_DBTYPE;
			dbName = "mysql";
			break;
		case "Oracle":
			dataSourceType = EDataSourceType.ORACLE;
			dbDataSource = DbDataSource.ORACLE_DBTYPE;
			
			if (host.contains("&")) {
				String[] tmp = host.split("&");
				host = tmp[0];
				dbName = tmp[1];
			}else {
				JOptionPane.showMessageDialog(this, "Oracle数据库需要在数据库地址后拼接&符号和服务名");
				return;
			}
			break;
		case "PostGIS":
			dataSourceType = EDataSourceType.PostGIS;
			dbDataSource = DbDataSource.POSTGIS_DBTYPE;
			break;
		}
		
		
		DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
		outputDataSrcArgs.setDbType(dbDataSource);
		outputDataSrcArgs.setDbHost(host);
		outputDataSrcArgs.setDbPort(dataBaseInfos.getDbPort());
		;
		outputDataSrcArgs.setDbName(dbName);
		outputDataSrcArgs.setDbUserName(dataBaseInfos.getDbUser());
		outputDataSrcArgs.setDbPwd(dataBaseInfos.getDbPassword());
		outputDataSrcArgs.setDataSrcType(dataSourceType);
		outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

		cachedThreadPool.execute(new Runnable() {
			public void run() {
				comboBox.removeAllItems();
				boolean isConnect = outputMssqlDataSrc.validConnection(outputMssqlDataSrc);
				if (isConnect) {
//							JOptionPane.showMessageDialog(null, "连接成功", "提示", JOptionPane.INFORMATION_MESSAGE);
					List<String> dbNameList = outputMssqlDataSrc.queryDataBase(outputMssqlDataSrc);
					for (String string : dbNameList) {
						comboBox.addItem(string);
					}
				} else {
					JOptionPane.showMessageDialog(null, "连接失败", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

	}

	public static ConfigDatabaseOutput getFrame() {
		return frame;
	}

	public static void setFrame(ConfigDatabaseOutput frame) {
		ConfigDatabaseOutput.frame = frame;
	}

}
