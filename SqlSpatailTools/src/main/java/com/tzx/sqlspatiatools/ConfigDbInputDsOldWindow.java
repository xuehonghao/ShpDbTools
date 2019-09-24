package com.tzx.sqlspatiatools;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;

/**
 * 数据库（旧格式）
 * @author Administrator
 *
 */
public class ConfigDbInputDsOldWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JComboBox comboBox;
	private JComboBox comboBox_0;
	private JComboBox comboBox_1;
	private JComboBox comboBox_2;
	private JTable table_1;
	private JTable table_2;
	private JTable table_3;
	private JPopupMenu m_popupMenu;

	private DefaultTableModel tableListTabModel;
	private DefaultTableModel inputListTabModel;
	private DefaultTableModel outputListTabModel;

	private Gson gson = new Gson();
	private static ConfigDbInputDsOldWindow frame;
	private DataBaseInfos dbInfo;
	private List<DataBaseInfos> dbInfoList;
	private List<String> dataBaseName;
	private List<String> tableNameList;
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
					ConfigDbInputDsOldWindow frame = new ConfigDbInputDsOldWindow(new MainWindow());
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
	public ConfigDbInputDsOldWindow(MainWindow mainWindow) {
		super("数据库（旧格式）");
		this.mainWindow = mainWindow;
		// 设置窗口配置
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);// 只关闭子窗口，不关闭父窗口
		setBounds(100, 100, 927, 622);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setResizable(false);

		// 点击窗体，取消输入列选中状态
//		addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				table_2.clearSelection();
//			}
//		});

		// 添加选择数据库项
		addDbSelect();

		// 添加选择数据库按钮
		addSelectDbButton();

		// 添加数据库名选项
		addDbNameSelect();

		// 添加表名列表项
		addTableList();

		// 添加输入列表项
		addInputList();

		// 添加字段信息项
		addVectorList();

		// 添加字段类型项
		addVectorType();

		// 添加源空间数据字段名
		addVectorSourceName();

		// 添加源空间数据类型
		addVectorSourceType();

		// 添加结果集执行SQL
//		addResultSetSql();
		
		// 添加条件筛选
		addFilterCondition();

		// 添加查看按钮
//		addExamineButton();

		// 添加确认按钮
		addConfirmButton();

		// 添加右键菜单
		createPopupMenu();
	}

	/**
	 * 添加选择数据库项
	 */
	public void addDbSelect() {
		// 添加标题
		JLabel lblNewLabel = new JLabel("选择数据库：");
		lblNewLabel.setBounds(36, 24, 101, 15);
		contentPane.add(lblNewLabel);

		// 添加下拉框
		comboBox = new JComboBox();
		comboBox.setBounds(147, 21, 137, 21);
		contentPane.add(comboBox);

		// 初始化数据
		refreshDataBaseValue();
		comboBox.setSelectedIndex(-1);// 默认什么都不选

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
							outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);
							
							boolean isConnect = outputMssqlDataSrc.validConnection(outputMssqlDataSrc);
							if (isConnect) {
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
									refreshDataBaseName();
								}
							});
						}
					}.start();
				}
			}

		});
	}

	/**
	 * 添加选择数据库按钮
	 */
	public void addSelectDbButton() {
		JButton button = new JButton("选择");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigDatabaseWindow.setFrame(new ConfigDatabaseWindow("Old"));
			}
		});
		button.setBounds(338, 20, 93, 23);
		contentPane.add(button);
	}

	/**
	 * 添加数据库名选项
	 */
	public void addDbNameSelect() {
		JLabel label = new JLabel("数据库名：");
		label.setBounds(517, 24, 66, 15);
		contentPane.add(label);

		comboBox_1 = new JComboBox();
		comboBox_1.setBounds(593, 21, 220, 21);
		contentPane.add(comboBox_1);

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
					outputDataSrcArgs.setDbPort(dbInfo.getDbPort());
					outputDataSrcArgs.setDbName(s);
					outputDataSrcArgs.setDbUserName(dbInfo.getDbUser());
					outputDataSrcArgs.setDbPwd(dbInfo.getDbPassword());
					outputDataSrcArgs.setDataSrcType(dataSourceType);
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
	}

	/**
	 * 添加表名列表项
	 */
	public void addTableList() {
		JLabel label_1 = new JLabel("表名列表：");
		label_1.setBounds(36, 77, 83, 15);
		contentPane.add(label_1);

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

		JScrollPane scrollPane = new JScrollPane(table_1);
		scrollPane.setBounds(147, 77, 370, 143);
		contentPane.add(scrollPane);

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

							dbInfoDetail.getOutputArgs().setType(comboBox_0.getSelectedItem().toString());
							dbInfoDetail.getOutputArgs().setBinaryDataType(comboBox_2.getSelectedItem().toString());

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
	 * 添加输入列表项
	 */
	public void addInputList() {
		JLabel label_2 = new JLabel("输入列表：");
		label_2.setBounds(36, 253, 83, 15);
		contentPane.add(label_2);

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

		JScrollPane scrollPane_1 = new JScrollPane(table_2);
		scrollPane_1.setBounds(36, 278, 203, 247);
		contentPane.add(scrollPane_1);

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
		
		table_2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// 刷新输入列表数据
		refreshInputList();
	}

	/**
	 * 添加字段信息项
	 */
	public void addVectorList() {
		JLabel lblNewLabel_1 = new JLabel("字段信息：");
		lblNewLabel_1.setBounds(271, 253, 83, 15);
		contentPane.add(lblNewLabel_1);

		Object[][] tableData = new Object[][] {};
		String[] tableHead = { "是否选中", "列名", "类型", "空间字段" };
		outputListTabModel = new DefaultTableModel(tableData, tableHead);
		table_3 = new JTable((TableModel) outputListTabModel) {
			public boolean isCellEditable(int row, int column) {
				if (column == 1 || column == 2) {
					return false;// 表格不允许被编辑
				}
				return true;
			}
		};
		
//		table_3.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// 添加复选框
		TableColumn checkColumn = table_3.getColumnModel().getColumn(0);
		checkColumn.setCellEditor(table_3.getDefaultEditor(Boolean.class));
		checkColumn.setCellRenderer(table_3.getDefaultRenderer(Boolean.class));

		TableColumn geoColumn = table_3.getColumnModel().getColumn(3);
		geoColumn.setCellEditor(table_3.getDefaultEditor(Boolean.class));
		geoColumn.setCellRenderer(table_3.getDefaultRenderer(Boolean.class));

		JScrollPane scrollPane_2 = new JScrollPane(table_3);
		scrollPane_2.setBounds(268, 278, 230, 247);
		contentPane.add(scrollPane_2);

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

				int geoCheckedNum = 0;
				for (InputFieldListArgs inputArgs : inputFieldList) {
					if (inputArgs.isGeoChecked()) {
						geoCheckedNum++;
					}
				}
				for (int i = 0; i < inputFieldList.size(); i++) {
					if (i == tcl.getRow() && tcl.getColumn() == 0) {
						inputFieldList.get(i).setChecked(Boolean.valueOf(tcl.getNewValue().toString()));
					}
					if (i == tcl.getRow() && tcl.getColumn() == 1) {
						inputFieldList.get(i).setName(tcl.getNewValue().toString());
					}
					if (i == tcl.getRow() && tcl.getColumn() == 3) {

						if ("Line".equals(String.valueOf(comboBox_0.getSelectedItem()))	|| "Polygon".equals(String.valueOf(comboBox_0.getSelectedItem()))) {
							if (geoCheckedNum >= 1 && tcl.getOldValue().toString().equals("false")) {
								JOptionPane.showMessageDialog(null, "Line和Polygon只能选一个", "提示",
										JOptionPane.INFORMATION_MESSAGE);
							} else {
								inputFieldList.get(i).setGeoChecked(Boolean.valueOf(tcl.getNewValue().toString()));
							}

						} else {
							if (geoCheckedNum >= 2 && tcl.getOldValue().toString().equals("false")) {
								JOptionPane.showMessageDialog(null, "Point只能选两个", "提示",
										JOptionPane.INFORMATION_MESSAGE);
							} else {
								inputFieldList.get(i).setGeoChecked(Boolean.valueOf(tcl.getNewValue().toString()));
							}
						}

					}
				}

				// 刷新数据
				Object[][] tableData = new Object[][] {};
				if (databaseDetailList != null && databaseDetailList.size() > 0) {
					// 将数据放在tableData中
					tableData = new Object[inputFieldList.size()][4];
					for (int i = 0; i < tableData.length; i++) {
						tableData[i][0] = inputFieldList.get(i).isChecked();
						tableData[i][1] = inputFieldList.get(i).getName();
						tableData[i][2] = inputFieldList.get(i).getType().getSimpleName();
						tableData[i][3] = inputFieldList.get(i).isGeoChecked();
					}

				}
				for (int i = 0; i < outputListTabModel.getRowCount(); i++) {
					outputListTabModel.removeRow(i);
					i--;
				}
				for (int i = 0; i < tableData.length; i++) {
					outputListTabModel.addRow(tableData[i]);
				}

				// 刷新源空间数据字段名
				String[] vctName = null;
				if (comboBox_0.getSelectedItem().toString().equals("Line") || comboBox_0.getSelectedItem().toString().equals("Polygon")) {
					vctName = new String[1];
				} else {
					vctName = new String[2];
				}
				int j = 0;
				for (int i = 0; i < inputFieldList.size(); i++) {
					if (vctName.length == 1) {
						if (inputFieldList.get(i).isGeoChecked()) {
							vctName[0] = table_3.getValueAt(i, 1).toString();
							break;
						}
					} else {
						if (inputFieldList.get(i).isGeoChecked()) {
							vctName[j++] = table_3.getValueAt(i, 1).toString();
							if (j == 2) {
								break;
							}
						}
					}
				}

				if (vctName.length == 2) {
					textField.setText(vctName[0] + "," + vctName[1]);
				} else if (vctName.length == 1) {
					textField.setText(vctName[0]);
				}

			}
		};
		new TableCellListener(table_3, action);
	}

	/**
	 * 添加字段类型项
	 */
	public void addVectorType() {
		JLabel lblNewLabel_2 = new JLabel("字段类型：");
		lblNewLabel_2.setBounds(516, 265, 93, 15);
		contentPane.add(lblNewLabel_2);

		comboBox_0 = new JComboBox();
		comboBox_0.setBounds(670, 262, 110, 21);
		contentPane.add(comboBox_0);

		comboBox_0.addItem("Polygon");
		comboBox_0.addItem("Point");
		comboBox_0.addItem("Line");

		comboBox_0.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (databaseDetailList.size() > 0) {
						int a = table_2.getSelectedRow();
						if (a >= 0) {// 选中
							databaseDetailList.get(a).getOutputArgs().setType(comboBox_0.getSelectedItem().toString());
							System.out.println(databaseDetailList.get(a).getOutputArgs().getType());
						} else {// 未选中
							databaseDetailList.get(databaseDetailList.size() - 1).getOutputArgs()
									.setType(comboBox_0.getSelectedItem().toString());
							System.out.println(databaseDetailList.get(databaseDetailList.size() - 1).getOutputArgs().getType());
						}
						
					}
				}
			}

		});
		
		
		// 初始化字段类型
		if(databaseDetailList.size() > 0) {
			comboBox_0.setSelectedItem(databaseDetailList.get(databaseDetailList.size()-1).getOutputArgs().getType());
		}

	}

	/**
	 * 添加源空间数据字段名
	 */
	public void addVectorSourceName() {
		JLabel lblNewLabel_3 = new JLabel("源空间数据字段名：");
		lblNewLabel_3.setBounds(516, 306, 157, 15);
		contentPane.add(lblNewLabel_3);

		textField = new JTextField();
		textField.setBounds(670, 303, 110, 21);
		textField.setColumns(10);
		textField.setEditable(false);
		contentPane.add(textField);

		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (databaseDetailList.size() > 0) {
					int a = table_2.getSelectedRow();
					if (a >= 0) {// 选中
						databaseDetailList.get(a).getOutputArgs().setFieldName(textField.getText());
					} else {// 未选中
						databaseDetailList.get(databaseDetailList.size() - 1).getOutputArgs().setFieldName(textField.getText());
					}
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		
		
		// 初始化源空间数据字段名
		if(databaseDetailList.size() > 0) {
			textField.setText(databaseDetailList.get(databaseDetailList.size()-1).getOutputArgs().getFieldName());
		}

	}

	/**
	 * 添加源空间数据类型
	 */
	public void addVectorSourceType() {
		JLabel lblNewLabel_4 = new JLabel("源空间数据类型：");
		lblNewLabel_4.setBounds(516, 351, 116, 15);
		contentPane.add(lblNewLabel_4);

		comboBox_2 = new JComboBox();
		comboBox_2.setBounds(670, 348, 110, 21);
		contentPane.add(comboBox_2);

		comboBox_2.addItem("double");
		comboBox_2.addItem("float");

		comboBox_2.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (databaseDetailList.size() > 0) {
						int a = table_2.getSelectedRow();
						if (a >= 0) {// 选中
							databaseDetailList.get(a).getOutputArgs().setBinaryDataType(comboBox_2.getSelectedItem().toString());
						} else {// 未选中
							databaseDetailList.get(databaseDetailList.size() - 1).getOutputArgs()
									.setBinaryDataType(comboBox_2.getSelectedItem().toString());
						}
					}
				}
			}

		});
		
		// 初始化源空间数据类型
		if(databaseDetailList.size() > 0) {
			comboBox_2.setSelectedItem(databaseDetailList.get(databaseDetailList.size()-1).getOutputArgs().getBinaryDataType());
		}
	}

	/**
	 * 添加结果集执行sql项
	 */
	/*public void addResultSetSql() {
		JLabel label_3 = new JLabel("结果集执行SQL：");
		label_3.setBounds(516, 397, 116, 15);
		contentPane.add(label_3);

		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);// 设置文本区的换行策略。
		textArea.setTabSize(2);// 使用setTabSize()方法设置[Tab]键的跳离距离
		JScrollPane jScrollPane = new JScrollPane(textArea);
		jScrollPane.setBounds(629, 393, 182, 125);
		contentPane.add(jScrollPane);
	}*/
	
	
	/**
	 * 添加筛选条件
	 */
	public void addFilterCondition() {
		JLabel label_3 = new JLabel("筛选条件：");
		label_3.setBounds(516, 397, 116, 15);
		contentPane.add(label_3);

		JTextArea filterTextArea = new JTextArea();
		filterTextArea.setLineWrap(true);// 设置文本区的换行策略。
		filterTextArea.setTabSize(2);// 使用setTabSize()方法设置[Tab]键的跳离距离
		JScrollPane jScrollPane2 = new JScrollPane(filterTextArea);
		jScrollPane2.setBounds(629, 393, 182, 125);
		contentPane.add(jScrollPane2);
		
		
		filterTextArea.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				//TODO 取文本框中的值，并给List中的对象赋值
				if(databaseDetailList.size() > 0) {
					int row = table_2.getSelectedRow();
					if(row == -1) {
						databaseDetailList.get(databaseDetailList.size()-1).setFilter(filterTextArea.getText());
					}else {
						databaseDetailList.get(row).setFilter(filterTextArea.getText());
					}
				}
			}
			
		});
	}

	/**
	 * 添加查看按钮
	 */
	/*public void addExamineButton() {
		JButton btnNewButton = new JButton("查看");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(821, 393, 69, 23);
		contentPane.add(btnNewButton);
	}*/

	/**
	 * 添加确认按钮
	 */
	public void addConfirmButton() {
		JButton button_1 = new JButton("确定");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 刷新父类窗口的输入框
				mainWindow.RefreshInputListTable();
				// 关闭当前窗口
				dispose();
			}
		});
		button_1.setBounds(797, 544, 93, 23);
		contentPane.add(button_1);

		JButton btnNewButton_1 = new JButton("配置矢量文件输出");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] i = table_2.getSelectedRows();
				if(i.length == 0) {
					JOptionPane.showMessageDialog(null, "请选中需要配置的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
				}else {
					new ConfigVectorFileOutput(i, null, "数据库（旧格式）");
				}
			}
		});
		btnNewButton_1.setBounds(21, 544, 163, 23);
		contentPane.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("配置数据库输出");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] i = table_2.getSelectedRows();
				if(i.length == 0) {
					JOptionPane.showMessageDialog(null, "请选中需要配置的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
				}else {
					ConfigDatabaseOutput.setFrame(new ConfigDatabaseOutput(i, null, "数据库（旧格式）"));
				}
			}
		});
		btnNewButton_2.setBounds(210, 544, 163, 23);
		contentPane.add(btnNewButton_2);

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
				if(i == 0) {
					comboBox.setSelectedItem(databaseDetailList.get(i).getDbType() + "/" + databaseDetailList.get(i).getDbHost());
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
			tableData = new Object[inputFieldList.size()][4];
			for (int i = 0; i < tableData.length; i++) {
				tableData[i][0] = inputFieldList.get(i).isChecked();
				tableData[i][1] = inputFieldList.get(i).getName();
				tableData[i][2] = inputFieldList.get(i).getType().getSimpleName();
				tableData[i][3] = inputFieldList.get(i).isGeoChecked();
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
	 * 创建删除（右键弹框删除）
	 */
	private void createPopupMenu() {
		m_popupMenu = new JPopupMenu();

		// 添加删除
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

				// 刷新输入列表数据
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
				ConfigDatabaseOutput.setFrame(new ConfigDatabaseOutput(i, null, "数据库（旧格式）"));
			}
		});

		// 添加配置数据库输出
		JMenuItem delMenItem_2 = new JMenuItem();
		delMenItem_2.setText("配置矢量文件输出");
		delMenItem_2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int[] i = table_2.getSelectedRows();
				new ConfigVectorFileOutput(i, null, "数据库（旧格式）");
			}
		});

		m_popupMenu.add(delMenItem);
		m_popupMenu.add(delMenItem_1);
		m_popupMenu.add(delMenItem_2);
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
			tableData = new Object[inputFieldList.size()][4];
			for (int i = 0; i < tableData.length; i++) {
				tableData[i][0] = inputFieldList.get(i).isChecked();
				tableData[i][1] = inputFieldList.get(i).getName();
				tableData[i][2] = inputFieldList.get(i).getType().getSimpleName();
				tableData[i][3] = inputFieldList.get(i).isGeoChecked();
			}

			// 改变字段类型
			comboBox_0.setSelectedItem(0);
			if (databaseDetailList.get(rowNumber).getOutputArgs().getType() != null) {
				comboBox_0.setSelectedItem(databaseDetailList.get(rowNumber).getOutputArgs().getType());
			}

			// 改变源空间数据字段名
			textField.setText("");
			if (databaseDetailList.get(rowNumber).getOutputArgs().getFieldName() != null) {
				textField.setText(databaseDetailList.get(rowNumber).getOutputArgs().getFieldName());
			}

			// 改变源空间数据类型

			if (databaseDetailList.get(rowNumber).getOutputArgs().getBinaryDataType() != null) {
				comboBox_2.setSelectedItem(databaseDetailList.get(rowNumber).getOutputArgs().getBinaryDataType());
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

	public static ConfigDbInputDsOldWindow getFrame() {
		return frame;
	}

	public static void setFrame(ConfigDbInputDsOldWindow frame) {
		ConfigDbInputDsOldWindow.frame = frame;
	}

	public static List<DataBaseInfosDetail> getDatabaseDetailList() {
		return databaseDetailList;
	}

	public static void setDatabaseDetailList(List<DataBaseInfosDetail> databaseDetailList) {
		ConfigDbInputDsOldWindow.databaseDetailList = databaseDetailList;
	}
}
