package com.tzx.sqlspatiatools;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
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

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

/**
 * 新建数据库
 * @author Administrator
 *
 */
public class ConfigDatabaseWindow extends JFrame {

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
	private Gson gson = new Gson();
	private File jsonFile = new File("./databaseinfo.json");
	private JButton button;
	
	private static ConfigDatabaseWindow frame;
	
	
	/**
	 * 来源   New和Old两种来源
	 */
	private String source;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ConfigDatabaseWindow("New");
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
	public ConfigDatabaseWindow(String source) {
		super("数据库配置");
		this.source = source;
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
		JButton btnNewButton = addConfirm();
		contentPane.add(btnNewButton);

		// 添加测试连接按钮
		JButton btnNewButton_1 = addTestConnect();
//		contentPane.add(btnNewButton_1);

		// 添加历史记录按钮
		button = addHistory();
		contentPane.add(button);

		// 初始化数据
//		initDbInfo(0);
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
				if(textField_3.getText() == null || textField_3.getText().equals("")) {
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
				if(textField_3.getText() == null || textField_3.getText().equals("")) {
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
				if(textField_3.getText() == null || textField_3.getText().equals("")) {
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
				if(textField_3.getText() == null || textField_3.getText().equals("")) {
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
		contentPane.add(textField_3);
		textField_3.setColumns(10);

	}

	/**
	 * 生成确认按钮
	 * 
	 * @return
	 */
	public JButton addConfirm() {
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

				// 判断文件是否存在
				if (jsonFile.exists() && jsonFile.length() != 0) {// 文件存在则添加数据
					// 先读取之前存储的数据
					String laststr = CreateFileUtil.getDatafromFile("databaseinfo");
					List<DataBaseInfos> dbInfoList = gson.fromJson(laststr, new TypeToken<List<DataBaseInfos>>() {
					}.getType());
					boolean isRepeat = true; // 是否重复 true:重复 false:不重复
					int num = 0;
					for (int i = 0; i < dbInfoList.size(); i++) {
						if (dbInfoList.get(i).getDbHost().equals(dataBaseInfos.getDbHost())
								&& dbInfoList.get(i).getDbPassword().equals(dataBaseInfos.getDbPassword())
								&& dbInfoList.get(i).getDbPort().equals(dataBaseInfos.getDbPort())
								&& dbInfoList.get(i).getDbType().equals(dataBaseInfos.getDbType())
								&& dbInfoList.get(i).getDbUser().equals(dataBaseInfos.getDbUser())) {
							isRepeat = true;
							num = i;
							break;
						} else {
							isRepeat = false;
						}
					}

					if (!isRepeat) {// 如果没有重复
						dbInfoList.add(0, dataBaseInfos);
					} else {// 如果重复了
						dbInfoList.add(0, dataBaseInfos);
						dbInfoList.remove(num + 1);
					}

					// 创建json文件
					createJson(dbInfoList);

				} else {// 文件不存在则创建新文件并添加数据
					List<DataBaseInfos> dbInfoList = new ArrayList<DataBaseInfos>();
					dbInfoList.add(0, dataBaseInfos);

					// 创建json文件
					createJson(dbInfoList);
				}
				
				if(source.equals("New")) {
					ConfigDbInputDsNewWindow.getFrame().refreshDataBaseValue();
				}else {
					ConfigDbInputDsOldWindow.getFrame().refreshDataBaseValue();
				}

//				JOptionPane.showMessageDialog(null, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
				//关闭当前窗口
				dispose();

			}
		});
		btnNewButton.setBounds(431, 282, 93, 23);
		return btnNewButton;
	}

	/**
	 * 添加历史记录按钮
	 * 
	 * @return
	 */
	public JButton addHistory() {
		button = new JButton("历史记录");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DatabaseHistory(frame,null);
			}
		});
		button.setBounds(28, 282, 93, 23);
		return button;
	}

	/**
	 * 创建json文件
	 * 
	 * @param dbInfoList
	 */
	public void createJson(List<DataBaseInfos> dbInfoList) {
		String objectStr = gson.toJson(dbInfoList);
		// 生成json文件
		CreateFileUtil.createJsonFile(objectStr, "./", "databaseinfo");
	}

	/**
	 * 生成测试连接按钮
	 * 
	 * @return
	 */
	public JButton addTestConnect() {
		JButton btnNewButton_1 = new JButton("测试连接");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 进行验证
				boolean isEffective = verification();
				if (!isEffective) {
					return;
				}

				// 获取值
				setDbInfo();

				// 配置参数
				EDataSourceType dataSourceType = null;
				String dbDataSource = null;
				switch (dataBaseInfos.getDbType()) {
				case "MSSQL":
					dataSourceType = EDataSourceType.MSSQL;
					dbDataSource = DbDataSource.SQLSERVER_DBTYPE;
					break;
				case "MySQL":
					dataSourceType = EDataSourceType.MYSQL;
					dbDataSource = DbDataSource.MYSQL_DBTYPE;
					break;
				case "Oracle":
					dataSourceType = EDataSourceType.ORACLE;
					dbDataSource = DbDataSource.ORACLE_DBTYPE;
					break;
				case "PostGIS":
					dataSourceType = EDataSourceType.PostGIS;
					dbDataSource = DbDataSource.POSTGIS_DBTYPE;
					break;
				}
				DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
				DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
				outputDataSrcArgs.setDbType(dbDataSource);
				outputDataSrcArgs.setDbHost(dataBaseInfos.getDbHost());
				outputDataSrcArgs.setDbName("master");// TODO 这里只有sqlserver
				outputDataSrcArgs.setDbUserName(dataBaseInfos.getDbUser());
				outputDataSrcArgs.setDbPwd(dataBaseInfos.getDbPassword());
				outputDataSrcArgs.setDataSrcType(dataSourceType);
				outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

				boolean isConnect = outputMssqlDataSrc.validConnection(outputMssqlDataSrc);
				if (isConnect) {
					JOptionPane.showMessageDialog(null, "连接成功", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "连接失败", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		btnNewButton_1.setBounds(221, 282, 93, 23);
		return btnNewButton_1;
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

			if(dbInfoList != null && dbInfoList.size() > 0) {
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
		}
	}

	public static ConfigDatabaseWindow getFrame() {
		return frame;
	}

	public static void setFrame(ConfigDatabaseWindow frame) {
		ConfigDatabaseWindow.frame = frame;
	}
	
	
	
	
	
}
