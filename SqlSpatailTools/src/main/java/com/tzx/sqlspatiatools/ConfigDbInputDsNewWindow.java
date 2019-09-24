package com.tzx.sqlspatiatools;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzx.datasource.DataSourceBase;
import com.tzx.datasource.DataSourceFactory;
import com.tzx.datasource.DbDataSource;
import com.tzx.datasource.EDataSourceType;
import com.tzx.datasource.argument.DbConfigArgs;
import com.tzx.sqlspatiatools.bean.DataBaseInfos;
import com.tzx.sqlspatiatools.bean.DataBaseInfosDetail;
import com.tzx.sqlspatiatools.bean.InputFieldListArgs;
import com.tzx.sqlspatiatools.utils.CreateFileUtil;
import com.tzx.sqlspatiatools.utils.TableCellListener;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;

/**
 * 输入数据源：数据库（新格式）
 * 
 * @author Administrator
 *
 */
public class ConfigDbInputDsNewWindow extends JFrame {

	private JPanel contentPane;
	private JTable table_1;
	private JTable table_2;
	private JTable table_3;
	private JComboBox comboBox;
	private JComboBox comboBox_1;
	private JPopupMenu m_popupMenu;

	private DefaultTableModel tableListTabModel;
	private DefaultTableModel inputListTabModel;
	private DefaultTableModel outputListTabModel;

	private Gson gson = new Gson();
	private static ConfigDbInputDsNewWindow frame;
	private List<String> dataBaseName;
	private List<String> tableNameList;
	private List<DataBaseInfos> dbInfoList;
	private DataBaseInfos dbInfo;
	private static List<DataBaseInfosDetail> databaseDetailList = new ArrayList<DataBaseInfosDetail>();
	private MainWindow mainWindow;

	private int rowNumber = -1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ConfigDbInputDsNewWindow(new MainWindow());
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
	public ConfigDbInputDsNewWindow(MainWindow mainWindow) {
		super("数据库（新格式）");
		this.mainWindow = mainWindow;
		// 设置页面属性
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);// 只关闭子窗口，不关闭父窗口
		setBounds(100, 100, 718, 672);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setResizable(false);

		// 点击窗体，取消输入列选中状态
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				table_2.clearSelection();
			}
		});

		// 添加选择数据库选项（下拉框）
		newDataBaseOption();

		// 添加新建按钮
		newDataBaseButton();

		// 添加数据库名选项（下拉框）
		newDataBaseNameOption();

		// 添加表名列表项
		newTableNameList();

		// 添加输入列表项
		newInputList();

		// 添加字段信息项
		newVectorList();

		// 添加确认按钮
		newConfirm();

		// 添加右键删除选项
		createPopupMenu();

		// 添加配置矢量文件输出按钮
		JButton btnNewButton_1 = new JButton("配置矢量文件输出");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] i = table_2.getSelectedRows();
				if (i.length == 0) {
					JOptionPane.showMessageDialog(null, "请选中需要配置的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					new ConfigVectorFileOutput(i, null, "数据库（新格式）");
				}
			}
		});
		btnNewButton_1.setBounds(27, 579, 142, 23);
		contentPane.add(btnNewButton_1);

		// 添加配置数据库输出按钮
		JButton button_1 = new JButton("配置数据库输出");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] i = table_2.getSelectedRows();
				if (i.length == 0) {
					JOptionPane.showMessageDialog(null, "请选中需要配置的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					ConfigDatabaseOutput.setFrame(new ConfigDatabaseOutput(i, null, "数据库（新格式）"));
				}
			}
		});
		button_1.setBounds(188, 579, 142, 23);
		contentPane.add(button_1);

	}

	/**
	 * 添加新建按钮
	 */
	public void newDataBaseButton() {
		JButton btnNewButton = new JButton("新建");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigDatabaseWindow.setFrame(new ConfigDatabaseWindow("New"));
//				refreshDataBaseValue();
			}
		});
		btnNewButton.setBounds(548, 28, 93, 23);
		contentPane.add(btnNewButton);
	}

	/**
	 * 添加选择数据库项
	 */
	public void newDataBaseOption() {
		JLabel lblNewLabel = new JLabel("选择数据库：");
		lblNewLabel.setBounds(50, 32, 111, 15);
		contentPane.add(lblNewLabel);

		comboBox = new JComboBox();

		// 查找所有的数据库
		refreshDataBaseValue();

		comboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					new Thread() {
						public void run() {
							String s = (String) e.getItem();
							String[] info = s.split("/");
							dbInfo = new DataBaseInfos();
							dbInfo.setDbType(info[0]);
							dbInfo.setDbHost(info[1]);

							String laststr = CreateFileUtil.getDatafromFile("databaseinfo");
							dbInfoList = gson.fromJson(laststr, new TypeToken<List<DataBaseInfos>>() {
							}.getType());
							for (DataBaseInfos dataBaseInfos : dbInfoList) {
								if (dataBaseInfos.getDbType().equals(dbInfo.getDbType())
										&& dataBaseInfos.getDbHost().equals(dbInfo.getDbHost())) {
									dbInfo.setDbUser(dataBaseInfos.getDbUser());
									dbInfo.setDbPassword(dataBaseInfos.getDbPassword());
									dbInfo.setDbPort(dataBaseInfos.getDbPort());
								}
							}

							// 配置参数
							EDataSourceType dataSourceType = null;
							String dbDataSource = null;
							String dbName = "";
							String host = dbInfo.getDbHost();
							switch (dbInfo.getDbType()) {
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
									JOptionPane.showMessageDialog(null, "连接失败,Oracle数据库需要在数据库地址后拼接&符号和服务名", "提示", JOptionPane.INFORMATION_MESSAGE);
									return;
								}
								break;
							case "PostGIS":
								dataSourceType = EDataSourceType.PostGIS;
								dbDataSource = DbDataSource.POSTGIS_DBTYPE;
								break;
							}
							DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory
									.createDataSource(dataSourceType);
							DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
							outputDataSrcArgs.setDbType(dbDataSource);
							outputDataSrcArgs.setDbHost(host);
							outputDataSrcArgs.setDbPort(dbInfo.getDbPort());
							outputDataSrcArgs.setDbName(dbName);
							outputDataSrcArgs.setDbUserName(dbInfo.getDbUser());
							outputDataSrcArgs.setDbPwd(dbInfo.getDbPassword());
							outputDataSrcArgs.setDataSrcType(dataSourceType);
//							outputDataSrcArgs.setTableName("test3");
							outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

							boolean isConnect = outputMssqlDataSrc.validConnection(outputMssqlDataSrc);
							if (isConnect) {
//								JOptionPane.showMessageDialog(null, "连接成功", "提示", JOptionPane.INFORMATION_MESSAGE);
								dataBaseName = outputMssqlDataSrc.queryDataBase(outputMssqlDataSrc);
								for (String string : dataBaseName) {
									System.out.println(string);
								}
							} else {
								JOptionPane.showMessageDialog(null, "连接失败", "提示", JOptionPane.INFORMATION_MESSAGE);
							}

							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									refreshDataBaseName();// 刷新数据库名称
								}
							});
						}
					}.start();
				}
			}

		});

		comboBox.setSelectedIndex(-1);// 默认什么都不选
		comboBox.setBounds(147, 29, 332, 21);

		contentPane.add(comboBox);
	}

	/**
	 * 刷新数据库信息
	 * 
	 * @param comboBox
	 */
	public void refreshDataBaseValue() {

		comboBox.removeAllItems();
		String laststr = CreateFileUtil.getDatafromFile("databaseinfo");
		List<DataBaseInfos> dbInfoList = gson.fromJson(laststr, new TypeToken<List<DataBaseInfos>>() {
		}.getType());

		if (dbInfoList != null && dbInfoList.size() > 0) {
			for (DataBaseInfos dataBaseInfos : dbInfoList) {
				comboBox.addItem(dataBaseInfos.getDbType() + "/" + dataBaseInfos.getDbHost());
			}
		}

	}

	/**
	 * 添加数据库名项
	 */
	public void newDataBaseNameOption() {
		JLabel lblNewLabel_1 = new JLabel("数据库名：");
		lblNewLabel_1.setBounds(50, 87, 85, 15);
		contentPane.add(lblNewLabel_1);

		comboBox_1 = new JComboBox();

		// 添加监听，改变值触发
		comboBox_1.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String s = (String) e.getItem(); // 获取选中的值
					// 配置参数
					EDataSourceType dataSourceType = null;
					String dbDataSource = null;
					String host = dbInfo.getDbHost();
					switch (dbInfo.getDbType()) {
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
						if (host.contains("&")) {
							String[] tmp = host.split("&");
							host = tmp[0];
							s = tmp[1];
						}
						break;
					case "PostGIS":
						dataSourceType = EDataSourceType.PostGIS;
						dbDataSource = DbDataSource.POSTGIS_DBTYPE;
						break;
					}
					DataSourceBase outputMssqlDataSrc = (DataSourceBase) DataSourceFactory
							.createDataSource(dataSourceType);
					DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
					outputDataSrcArgs.setDbType(dbDataSource);
					outputDataSrcArgs.setDbHost(host);
					outputDataSrcArgs.setDbName(s);
					outputDataSrcArgs.setDbPort(dbInfo.getDbPort());
					outputDataSrcArgs.setDbUserName(dbInfo.getDbUser());
					outputDataSrcArgs.setDbPwd(dbInfo.getDbPassword());
					outputDataSrcArgs.setDataSrcType(dataSourceType);
//					outputDataSrcArgs.setTableName("test3");
					outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);
					tableNameList = outputMssqlDataSrc.getDataTableName(outputMssqlDataSrc);
					for (String string : tableNameList) {
						System.out.println(string);
					}

					refreshTablesName();
				}
			}

		});

		// 添加值（数据库名）
		refreshDataBaseName();

		comboBox_1.setBounds(147, 84, 332, 21);
		contentPane.add(comboBox_1);
	}

	/**
	 * 刷新数据库名称
	 */
	public void refreshDataBaseName() {
		comboBox_1.removeAllItems();
		if (dataBaseName != null && dataBaseName.size() != 0) {
			for (String s : dataBaseName) {
				comboBox_1.addItem(s);
			}
		}
	}

	/**
	 * 添加表名列表项
	 */
	public void newTableNameList() {
		JLabel label = new JLabel("表名列表：");
		label.setBounds(50, 135, 85, 15);
		contentPane.add(label);

		Object[][] tableData = new Object[][] {};
		String[] tableHead = { "是否选中", "表名" };
		tableListTabModel = new DefaultTableModel(tableData, tableHead);
		table_1 = new JTable((TableModel) tableListTabModel) {
			public boolean isCellEditable(int row, int column) {
				if (column == 1) {
					return false;// 表格不允许被编辑
				}
				return true;
			}
		};

		// 添加复选框
		TableColumn column1 = table_1.getColumnModel().getColumn(0);
		column1.setCellEditor(table_1.getDefaultEditor(Boolean.class));
		column1.setCellRenderer(table_1.getDefaultRenderer(Boolean.class));

		JScrollPane jScrollPane_1 = new JScrollPane(table_1);
		jScrollPane_1.setBounds(145, 134, 393, 137);
		contentPane.add(jScrollPane_1);

		// 添加表格监听（当表格数据发生变化时触发）
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				System.out.printf("cell changed%n");
				System.out.println("Row   : " + tcl.getRow());
				System.out.println("Column: " + tcl.getColumn());
				System.out.println("Old   : " + tcl.getOldValue());
				System.out.println("New   : " + tcl.getNewValue());

				// 页面上的数据库信息
				String db = comboBox.getItemAt(comboBox.getSelectedIndex()).toString();
				String[] dbInfo = db.split("/");

				// 页面上的数据库名
				String name = comboBox_1.getItemAt(comboBox_1.getSelectedIndex()).toString();

				// 页面上的表名
				String table = tableListTabModel.getValueAt(tcl.getRow(), 1).toString();

				if (Boolean.valueOf(tcl.getNewValue().toString()) && !Boolean.valueOf(tcl.getOldValue().toString())) {// 如果复选框选中
					// 获取连接数据库
					DataBaseInfosDetail dbInfoDetail = new DataBaseInfosDetail();
					for (int i = 0; i < dbInfoList.size(); i++) {
						if (dbInfoList.get(i).getDbType().equals(dbInfo[0])
								&& dbInfoList.get(i).getDbHost().equals(dbInfo[1])) {
							dbInfoDetail.setDbHost(dbInfoList.get(i).getDbHost());
							dbInfoDetail.setDbPassword(dbInfoList.get(i).getDbPassword());
							dbInfoDetail.setDbPort(dbInfoList.get(i).getDbPort());
							dbInfoDetail.setDbType(dbInfoList.get(i).getDbType());
							dbInfoDetail.setDbUser(dbInfoList.get(i).getDbUser());
							break;
						}
					}

					// 获取数据库名
					dbInfoDetail.setDatabaseName(name);

					// 获取表名
					dbInfoDetail.setTableName(table);

					// 获取输出名
					dbInfoDetail.getOutputArgs().setInputName(table);

					// 获取字段

					// 查询类型
					EDataSourceType dataSourceType = null;
					String dbDataSource = null;
					String host = dbInfoDetail.getDbHost();
					String dbName = dbInfoDetail.getDatabaseName();
					switch (dbInfoDetail.getDbType()) {
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
						if (host.contains("&")) {
							String[] tmp = host.split("&");
							host = tmp[0];
							dbName = tmp[1];
						}
						break;
					case "PostGIS":
						dataSourceType = EDataSourceType.PostGIS;
						dbDataSource = DbDataSource.POSTGIS_DBTYPE;
						break;
					}
					DataSourceBase inputMssqlDataSrc = (DataSourceBase) DataSourceFactory
							.createDataSource(dataSourceType);
					DbConfigArgs inputDataSrcArgs = new DbConfigArgs();
					inputDataSrcArgs.setDbType(dbDataSource);
					inputDataSrcArgs.setDbHost(host);
					inputDataSrcArgs.setDbPort(dbInfoDetail.getDbPort());
					inputDataSrcArgs.setDbName(dbName);
					inputDataSrcArgs.setDbUserName(dbInfoDetail.getDbUser());
					inputDataSrcArgs.setDbPwd(dbInfoDetail.getDbPassword());
					inputDataSrcArgs.setDataSrcType(dataSourceType);
					inputDataSrcArgs.setTableName(dbInfoDetail.getTableName());
					inputDataSrcArgs.setDbPort(dbInfoDetail.getDbPort());//端口号  2018/9/28新增
					inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);

					new Thread() {
						public void run() {
							// 获取字段和字段类型
							Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);
							inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
							List<InputFieldListArgs> list = new ArrayList<InputFieldListArgs>();
							for (Map.Entry<String, Object> map : fieldToTypeMap.entrySet()) {
								InputFieldListArgs a = new InputFieldListArgs();
								a.setName(map.getKey());
								a.setType((Class<?>) map.getValue());
								list.add(a);
							}

							dbInfoDetail.setInputFieldList(list);
							databaseDetailList.add(dbInfoDetail);

							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
//									System.out.println("@@@@@@@@@");
//									for (DataBaseInfosDetail dbList : databaseDetailList) {
//										System.out.println("_____++++");
//										System.out.println("数据库名：" + dbList.getDatabaseName());
//										System.out.println("地址：" + dbList.getDbHost());
//										System.out.println("端口：" + dbList.getDbPort());
//										System.out.println("用户名：" + dbList.getDbUser());
//										System.out.println("密码：" + dbList.getDbPassword());
//										System.out.println("数据库类型：" + dbList.getDbType());
//										System.out.println("表名：" + dbList.getTableName());
//									}

									// 刷新输入列表数据
									refreshInputList();

									// 刷新字段信息数据
									refreshVectorList();
								}
							});
						}
					}.start();
				} else {// 如果复选框取消选中
					for (int i = 0; i < databaseDetailList.size(); i++) {
						if (databaseDetailList.get(i).getDbType().equals(dbInfo[0])
								&& databaseDetailList.get(i).getDbHost().equals(dbInfo[1])
								&& databaseDetailList.get(i).getDatabaseName().equals(name)
								&& databaseDetailList.get(i).getTableName().equals(table)) {
							databaseDetailList.remove(i);
							break;
						}
					}

					// 刷新输入列表数据
					refreshInputList();

					// 刷新字段信息数据
					refreshVectorList();
				}
			}
		};
		new TableCellListener(table_1, action);

	}

	/**
	 * 刷新表名列表数据
	 */
	public void refreshTablesName() {
		Object[][] tableData = new Object[][] {};
		if (tableNameList != null && tableNameList.size() > 0) {
			// 页面上的数据库信息
			String db = comboBox.getItemAt(comboBox.getSelectedIndex()).toString();
			String[] dbInfo = db.split("/");

			// 页面上的数据库名
			String name = comboBox_1.getItemAt(comboBox_1.getSelectedIndex()).toString();
			tableData = new Object[tableNameList.size()][2];
			for (int i = 0; i < tableNameList.size(); i++) {
				if (databaseDetailList != null && databaseDetailList.size() > 0) {
					for (int j = 0; j < databaseDetailList.size(); j++) {
						if (databaseDetailList.get(j).getDbType().equals(dbInfo[0])
								&& databaseDetailList.get(j).getDbHost().equals(dbInfo[1])
								&& databaseDetailList.get(j).getTableName().equals(tableNameList.get(i))) {
							tableData[i][0] = new Boolean(true);
						}
					}
				} else {
					tableData[i][0] = new Boolean(false);
				}
				tableData[i][1] = tableNameList.get(i);
			}
		}
		for (int i = 0; i < tableListTabModel.getRowCount(); i++) {
			tableListTabModel.removeRow(i);
			i--;
		}
		for (int i = 0; i < tableData.length; i++) {
			tableListTabModel.addRow(tableData[i]);
		}
	}

	/**
	 * 添加输入列表项
	 */
	public void newInputList() {
		JLabel label_1 = new JLabel("输入列表：");
		label_1.setBounds(50, 286, 93, 15);
		contentPane.add(label_1);

		Object[][] tableData = new Object[][] {};
		String[] tableHead = { "数据库", "库名", "表名", "输出名" };
		inputListTabModel = new DefaultTableModel(tableData, tableHead);
		table_2 = new JTable((TableModel) inputListTabModel) {
			public boolean isCellEditable(int row, int column) {
				if (column == 0 || column == 1 || column == 2) {
					return false;// 表格不允许被编辑
				}
				return true;
			}
		};
		table_2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		jTableMouseListener(table_2);
		table_2.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				if (table_2.getRowCount() != 0 && table_2.getRowCount() == databaseDetailList.size()) {
					int row = e.getFirstRow();// 改变的单元格所在的行索引，起始值为0
					String inputName = table_2.getValueAt(row, 3).toString();
					System.out.println(inputName);
					databaseDetailList.get(row).getOutputArgs().setInputName(inputName);
				}

			}
		});

		// 初始化输入列表信息
		refreshInputList();

		JScrollPane scrollPane = new JScrollPane(table_2);
		scrollPane.setBounds(50, 311, 280, 241);
		contentPane.add(scrollPane);

	}

	/**
	 * 增加鼠标监听（输入列表专用）
	 * 
	 * @param table_2
	 */
	public void jTableMouseListener(JTable table_2) {
		table_2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				mouseRightButtonClick(e);
				if (e.getClickCount() == 1) {// 点击几次，这里是单击事件
					tableChanged(table_2);
				}
			}
		});
	}

	/**
	 * 改变字段列表的值 说明：在输入表格中选中一行，则字段信息表格的数据替换成与该行对应的数据
	 * 
	 * @param table_2
	 */
	public void tableChanged(JTable table_2) {
		// 选中的行数，如果等于-1则表示没有选中
		rowNumber = table_2.getSelectedRow();

		Object[][] tableData = new Object[][] {};
		if (databaseDetailList != null && databaseDetailList.size() > 0) {
			// 得到选中行的字段
			List<InputFieldListArgs> inputFieldList = databaseDetailList
					.get((rowNumber == -1 ? databaseDetailList.size() - 1 : rowNumber)).getInputFieldList();
			// 将数据放在tableData中
			tableData = new Object[inputFieldList.size()][3];
			for (int i = 0; i < tableData.length; i++) {
				tableData[i][0] = inputFieldList.get(i).isChecked();
				tableData[i][1] = inputFieldList.get(i).getName();
				tableData[i][2] = inputFieldList.get(i).getType().getSimpleName();
			}

		}
		for (int i = 0; i < outputListTabModel.getRowCount(); i++) {
			outputListTabModel.removeRow(i);
			i--;
		}
		for (int i = 0; i < tableData.length; i++) {
			outputListTabModel.addRow(tableData[i]);
		}
	}

	/**
	 * 刷新字段数据
	 * 
	 * @param index
	 * @return
	 */
	public Map<String, Object> getVector(int index) {
		DataBaseInfosDetail dbInfoDetail = new DataBaseInfosDetail();
		dbInfoDetail.setDatabaseName(databaseDetailList.get(index).getDatabaseName());
		dbInfoDetail.setDbHost(databaseDetailList.get(index).getDbHost());
		dbInfoDetail.setDbPassword(databaseDetailList.get(index).getDbPassword());
		dbInfoDetail.setDbPort(databaseDetailList.get(index).getDbPort());
		dbInfoDetail.setDbType(databaseDetailList.get(index).getDbType());
		dbInfoDetail.setDbUser(databaseDetailList.get(index).getDbUser());
		dbInfoDetail.setTableName(databaseDetailList.get(index).getTableName());

		// 查询类型
		EDataSourceType dataSourceType = null;
		String dbDataSource = null;
		switch (dbInfoDetail.getDbType()) {
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
		DataSourceBase inputMssqlDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		DbConfigArgs inputDataSrcArgs = new DbConfigArgs();
		inputDataSrcArgs.setDbType(dbDataSource);
		inputDataSrcArgs.setDbHost(dbInfoDetail.getDbHost());
		inputDataSrcArgs.setDbName(dbInfoDetail.getDatabaseName());
		inputDataSrcArgs.setDbUserName(dbInfoDetail.getDbUser());
		inputDataSrcArgs.setDbPwd(dbInfoDetail.getDbPassword());
		inputDataSrcArgs.setDataSrcType(dataSourceType);
		inputDataSrcArgs.setTableName(dbInfoDetail.getTableName());
		inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);

		// 获取字段和字段类型
		Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);
		inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
		return fieldToTypeMap;
	}

	/**
	 * 创建删除（右键弹框删除）
	 */
	private void createPopupMenu() {
		m_popupMenu = new JPopupMenu();
		JMenuItem delMenItem = new JMenuItem();
		delMenItem.setText("删除 ");
		delMenItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// 删除输入列表中选中的信息
				int[] row = table_2.getSelectedRows();
				for (int i = 0; i < row.length; i++) {
					System.out.println(row[i]);
					row[i] = row[i] - i;
					inputListTabModel.removeRow(row[i]);
					databaseDetailList.remove(row[i]);
				}

				// 刷新输入列表
				refreshInputList();

				// 刷新字段列表.
				refreshVectorList();

				// 刷新表名列表
				refreshTablesName();

			}
		});

		// 添加配置数据库输出
		JMenuItem delMenItem_1 = new JMenuItem();
		delMenItem_1.setText("配置数据库输出");
		delMenItem_1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int[] i = table_2.getSelectedRows();
				ConfigDatabaseOutput.setFrame(new ConfigDatabaseOutput(i, null, "数据库（新格式）"));
			}
		});

		// 添加配置数据库输出
		JMenuItem delMenItem_2 = new JMenuItem();
		delMenItem_2.setText("配置矢量文件输出");
		delMenItem_2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int[] i = table_2.getSelectedRows();
				new ConfigVectorFileOutput(i, null, "数据库（新格式）");
			}
		});

		m_popupMenu.add(delMenItem);
		m_popupMenu.add(delMenItem_1);
		m_popupMenu.add(delMenItem_2);
	}

	/**
	 * 右键弹框删除
	 * 
	 * @param evt
	 */
	private void mouseRightButtonClick(java.awt.event.MouseEvent evt) {
		// 判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键
		if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
			// 通过点击位置找到点击为表格中的行
			int focusedRowIndex = table_2.rowAtPoint(evt.getPoint());
			if (focusedRowIndex == -1) {
				return;
			}

			// 获取已选中的行
			int[] rows = table_2.getSelectedRows();
			boolean inSelected = false;
			// 判断当前右键所在行是否已选中
			for (int r : rows) {
				if (focusedRowIndex == r) {
					inSelected = true;
					break;
				}
			}
			// 当前鼠标右键点击所在行不被选中则高亮显示选中行
			if (!inSelected) {
				table_2.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
			}

			// 将表格所选项设为当前右键点击的行
//			table_2.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
			// 弹出菜单
			m_popupMenu.show(table_2, evt.getX(), evt.getY());
		}

	}

	/**
	 * 刷新输入列表数据
	 */
	public void refreshInputList() {
		Object[][] tableData = new Object[][] {};
		if (databaseDetailList != null && databaseDetailList.size() > 0) {
			tableData = new Object[databaseDetailList.size()][4];
			for (int i = 0; i < databaseDetailList.size(); i++) {
				tableData[i][0] = databaseDetailList.get(i).getDbType() + "/" + databaseDetailList.get(i).getDbHost();
				tableData[i][1] = databaseDetailList.get(i).getDatabaseName();
				tableData[i][2] = databaseDetailList.get(i).getTableName();
				tableData[i][3] = databaseDetailList.get(i).getOutputArgs().getInputName();
				if (i == 0) {
					comboBox.setSelectedItem(
							databaseDetailList.get(i).getDbType() + "/" + databaseDetailList.get(i).getDbHost());
				}
			}
		}
		for (int i = 0; i < inputListTabModel.getRowCount(); i++) {
			inputListTabModel.removeRow(i);
			i--;
		}

		for (int i = 0; i < tableData.length; i++) {
			inputListTabModel.addRow(tableData[i]);
		}
	}

	/**
	 * 添加字段信息项
	 */
	public void newVectorList() {
		JLabel label_2 = new JLabel("字段信息：");
		label_2.setBounds(374, 286, 79, 15);
		contentPane.add(label_2);

		Object[][] tableData = new Object[][] {};
		String[] tableHead = { "是否选中", "列名", "类型" };
		outputListTabModel = new DefaultTableModel(tableData, tableHead);
		table_3 = new JTable((TableModel) outputListTabModel) {
			public boolean isCellEditable(int row, int column) {
				if (column == 1 || column == 2) {
					return false;// 表格不允许被编辑
				}
				return true;
			}
		};

		// 添加复选框
		TableColumn column1 = table_3.getColumnModel().getColumn(0);
		column1.setCellEditor(table_3.getDefaultEditor(Boolean.class));
		column1.setCellRenderer(table_3.getDefaultRenderer(Boolean.class));

		// 添加表格监听（当表格数据发生变化时触发）
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				System.out.printf("cell changed%n");
				System.out.println("Row   : " + tcl.getRow());
				System.out.println("Column: " + tcl.getColumn());
				System.out.println("Old   : " + tcl.getOldValue());
				System.out.println("New   : " + tcl.getNewValue());

				List<InputFieldListArgs> inputFieldList = databaseDetailList
						.get((rowNumber == -1 ? databaseDetailList.size() - 1 : rowNumber)).getInputFieldList();
				for (int i = 0; i < inputFieldList.size(); i++) {
					if (i == tcl.getRow() && tcl.getColumn() == 0) {
						inputFieldList.get(i).setChecked(Boolean.valueOf(tcl.getNewValue().toString()));
					}
					if (i == tcl.getRow() && tcl.getColumn() == 1) {
						inputFieldList.get(i).setName(tcl.getNewValue().toString());
					}
				}

				// 刷新数据
				Object[][] tableData = new Object[][] {};
				if (databaseDetailList != null && databaseDetailList.size() > 0) {
					// 将数据放在tableData中
					tableData = new Object[inputFieldList.size()][3];
					for (int i = 0; i < tableData.length; i++) {
						tableData[i][0] = inputFieldList.get(i).isChecked();
						tableData[i][1] = inputFieldList.get(i).getName();
						tableData[i][2] = inputFieldList.get(i).getType().getSimpleName();
					}

				}
				for (int i = 0; i < outputListTabModel.getRowCount(); i++) {
					outputListTabModel.removeRow(i);
					i--;
				}
				for (int i = 0; i < tableData.length; i++) {
					outputListTabModel.addRow(tableData[i]);
				}

			}
		};
		new TableCellListener(table_3, action);

		JScrollPane scrollPane_1 = new JScrollPane(table_3);
		scrollPane_1.setBounds(374, 311, 280, 241);
		contentPane.add(scrollPane_1);
	}

	/**
	 * 刷新字段信息列表数据
	 */
	public void refreshVectorList() {
		Object[][] tableData = new Object[][] {};
		if (databaseDetailList != null && databaseDetailList.size() > 0) {
			// 默认显示输入列表中最后一条数据的字段

			// 取输入列表中的最后一条数据
			int index = databaseDetailList.size() - 1;

			List<InputFieldListArgs> inputFieldList = databaseDetailList.get(databaseDetailList.size() - 1)
					.getInputFieldList();
			// 将数据放在tableData中
			tableData = new Object[inputFieldList.size()][3];
			
			boolean isGeoFlag = false; //是否包含空间字段
			String typeName = "";
			for (int i = 0; i < tableData.length; i++) {
				tableData[i][0] = inputFieldList.get(i).isChecked();
				tableData[i][1] = inputFieldList.get(i).getName();
				tableData[i][2] = inputFieldList.get(i).getType().getSimpleName();
				typeName = inputFieldList.get(i).getType().getSimpleName();
				if(typeName.equals("Point") || typeName.equals("MultiPoint") 
						|| typeName.equals("LineString") || typeName.equals("MultiLineString")
						|| typeName.equals("Polygon") || typeName.equals("MultiPolygon")) {
					isGeoFlag = true;
				}
			}
			
			if(!isGeoFlag) { //如果不包括空间字段则弹框提醒
				JOptionPane.showMessageDialog(null, "该表无空间字段", "提示", JOptionPane.INFORMATION_MESSAGE);
				databaseDetailList.remove(databaseDetailList.size()-1);
				refreshInputList();
				tableData = new Object[][] {};
				table_1.setValueAt(new Boolean(false), table_1.getSelectedRow(), 0);
			}
		}
		for (int i = 0; i < outputListTabModel.getRowCount(); i++) {
			outputListTabModel.removeRow(i);
			i--;
		}
		for (int i = 0; i < tableData.length; i++) {
			outputListTabModel.addRow(tableData[i]);
		}
	}

	/**
	 * 添加确认按钮
	 */
	public void newConfirm() {
		JButton button = new JButton("确定");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// 刷新父类窗口的输入框
				mainWindow.RefreshInputListTable();
				// 关闭当前窗口
				dispose();
			}
		});
		button.setBounds(561, 579, 93, 23);
		contentPane.add(button);

	}

	public static ConfigDbInputDsNewWindow getFrame() {
		return frame;
	}

	public static void setFrame(ConfigDbInputDsNewWindow frame) {
		ConfigDbInputDsNewWindow.frame = frame;
	}

	
	
	public JComboBox getComboBox() {
		return comboBox;
	}

	public void setComboBox(JComboBox comboBox) {
		this.comboBox = comboBox;
	}

	public static List<DataBaseInfosDetail> getDatabaseDetailList() {
		return databaseDetailList;
	}

	public static void setDatabaseDetailList(List<DataBaseInfosDetail> databaseDetailList) {
		ConfigDbInputDsNewWindow.databaseDetailList = databaseDetailList;
	}

}
