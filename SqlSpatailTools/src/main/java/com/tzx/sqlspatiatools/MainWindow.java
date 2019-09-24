package com.tzx.sqlspatiatools;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.tzx.datasource.DataSourceBase;
import com.tzx.datasource.DataSourceFactory;
import com.tzx.datasource.DbDataSource;
import com.tzx.datasource.EDataSourceType;
import com.tzx.datasource.argument.DbConfigArgs;
import com.tzx.datasource.argument.OldDbConfigArgs;
import com.tzx.datasource.argument.OldVectorFileConfigArgs;
import com.tzx.datasource.argument.VectorFileArgs;
import com.tzx.datasource.inter.ProgressListener;
import com.tzx.sqlspatiatools.bean.DataBaseInfosDetail;
import com.tzx.sqlspatiatools.bean.InputFieldListArgs;
import com.tzx.sqlspatiatools.bean.InputListArgs;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.BevelBorder;

/**
 * 主界面
 * 
 * @author Administrator
 *
 */
public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel inputListTabModel;
	private JPopupMenu m_popupMenu;

	private static MainWindow mainWindow;

	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
					mainWindow = new MainWindow();
					mainWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		super("空间数据转换工具");
		// 窗口属性设置
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 773, 552);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setResizable(false);

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				table.clearSelection();
			}
		});

		// 添加标题 输入数据源
		JLabel label = new JLabel("输入数据源：");
		label.setBounds(10, 47, 102, 15);
		contentPane.add(label);

		// 添加标题 输出数据源
		JLabel label_1 = new JLabel("输出数据源：");
		label_1.setBounds(10, 262, 102, 15);
		contentPane.add(label_1);

		// 添加标题任务列表
		JLabel label_2 = new JLabel("任务列表：");
		label_2.setBounds(182, 47, 93, 15);
		contentPane.add(label_2);

		// 添加数据列表框
		addTableList();

		// 添加右键菜单
		createPopupMenu();

	}

	/**
	 * 添加数据列表框
	 */
	public void addTableList() {

		/** 创建表格start */
		Object[][] tableData = new Object[][] {};
		String[] tableHead = { "编号", "新旧格式", "输入信息", "输出信息", "状态", "进度" };
		inputListTabModel = new DefaultTableModel(tableData, tableHead);
		table = new JTable((TableModel) inputListTabModel) {
			public boolean isCellEditable(int row, int column) {
				if (column == 0 || column == 1 || column == 2 || column == 3 || column == 4 || column == 5) {
					return false;// 表格不允许被编辑
				}
				return true;
			}
		};

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(81);
		table.getColumnModel().getColumn(1).setPreferredWidth(97);
		table.getColumnModel().getColumn(2).setPreferredWidth(97);
		table.getColumnModel().getColumn(3).setPreferredWidth(97);
		table.getColumnModel().getColumn(4).setPreferredWidth(97);
		table.getColumnModel().getColumn(5).setPreferredWidth(91);

		jTableMouseListener(table);

		JScrollPane jScrollPane = new JScrollPane(table);
		jScrollPane.setBounds(175, 72, 562, 425);
		contentPane.add(jScrollPane);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBounds(10, 67, 162, 126);
		contentPane.add(panel);
		/** 创建表格end */

		/** 输入数据源start */
		// 添加按钮 矢量文件（新格式）
		JButton button = new JButton("矢量文件（新格式）");
		panel.add(button);

		// 添加按钮 矢量文件（旧格式）
		JButton button_1 = new JButton("矢量文件（旧格式）");
		panel.add(button_1);

		// 添加按钮 数据库（新格式）
		JButton button_2 = new JButton("数据库（新格式）");
		panel.add(button_2);

		// 添加按钮 数据库（旧格式）
		JButton button_3 = new JButton("数据库（旧格式）");
		panel.add(button_3);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ConfigVectorFileDsNewWindow(mainWindow);
			}
		});
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ConfigVectorFileDsOldWindow(mainWindow);
			}
		});
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigDbInputDsNewWindow.setFrame(new ConfigDbInputDsNewWindow(mainWindow));
			}
		});
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigDbInputDsOldWindow.setFrame(new ConfigDbInputDsOldWindow(mainWindow));
			}
		});
		/** 输入数据源end */

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_1.setBounds(10, 280, 162, 72);
		contentPane.add(panel_1);

		/** 输出数据源start */
		// 添加按钮 配置矢量文件输出
		JButton button_4 = new JButton("配置矢量文件输出");
		panel_1.add(button_4);

		// 添加按钮 配置数据库输出
		JButton button_5 = new JButton("配置数据库输出");
		panel_1.add(button_5);

		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] i = table.getSelectedRows();
				if (i.length > 0) {
					boolean flag = true;
					for (int j = 0; j < i.length; j++) {
						if (table.getValueAt(i[j], 2).toString().indexOf("MSSQL") < 0
								&& table.getValueAt(i[j], 2).toString().indexOf("MySQL") < 0
								&& table.getValueAt(i[j], 2).toString().indexOf("Oracle") < 0
								&& table.getValueAt(i[j], 2).toString().indexOf("PostGIS") < 0) {
							flag = false;
							break;
						}
					}
					if (flag) {
						new ConfigVectorFileOutput(i, mainWindow, "");
						// configVectorFileOutputDialog.setModal(true);
						// configVectorFileOutputDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
					} else {
						JOptionPane.showMessageDialog(null, "矢量文件不能配置矢量文件输出", "提示", JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "请选择要配置的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] i = table.getSelectedRows();
				if (i.length > 0) {
					ConfigDatabaseOutput.setFrame(new ConfigDatabaseOutput(i, mainWindow, ""));
				} else {
					JOptionPane.showMessageDialog(null, "请选择要配置的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		/** 输出数据源end */

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBounds(10, 458, 141, 43);
		contentPane.add(panel_2);

		/** 顶部菜单栏start */
		// 添加顶部菜单栏
		addMenu();
		/** 顶部菜单栏end */

		/** 操作区start */
		// 添加操作标签
		JLabel label = new JLabel("操作：");
		label.setBounds(10, 433, 54, 15);
		contentPane.add(label);

		// 添加按钮 提交
		JButton button_6 = new JButton("提交");
		panel_2.add(button_6);

		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 执行提交的操作
				String schedule = "";
				int totalNum = inputListTabModel.getRowCount();
				int endNum = 0;
				int startNum = 0;
				if (totalNum != 0) {
					for (int i = 0; i < inputListTabModel.getRowCount(); i++) {
						schedule = String.valueOf(inputListTabModel.getValueAt(i, 5));
						if (schedule.equals("100%")) {
							endNum++;
						} else if (!schedule.equals("0%")) {
							startNum++;
						}
					}
					if (totalNum == endNum) {// 当执行完的时候
						int n = JOptionPane.showConfirmDialog(null, "是否重新导出?", "确认导出", JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.YES_OPTION) {
							// 重新提交
							submit();
							return;
						} else if (n == JOptionPane.NO_OPTION) {
							// 什么都不做
							return;
						}
					} else if (startNum > 0) {// 没执行完的时候
						JOptionPane.showMessageDialog(null, "执行中，不能重复提交", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					submit();

				}
			}
		});
		/** 操作区end */

		/** 初始化表格数据start */
		RefreshInputListTable();
		/** 初始化表格数据end */
	}

	/**
	 * 刷新输入表格的数据
	 */
	public void RefreshInputListTable() {
		Object[][] tableData = new Object[][] {};
		InputListArgs inputListArgs;

		for (int i = 0; i < inputListTabModel.getRowCount(); i++) {
			inputListTabModel.removeRow(i);
			i--;
		}

		int num = 1;

		// 矢量文件（新格式）
		tableData = new Object[ConfigVectorFileDsNewWindow.getVfList().size()][6];
		for (int i = 0; i < ConfigVectorFileDsNewWindow.getVfList().size(); i++) {
			ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().setTaskNo(num + "");
			ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().setFormat("新");
			inputListArgs = ConfigVectorFileDsNewWindow.getVfList().get(i);
			tableData[i][0] = num;
			tableData[i][1] = inputListArgs.getOutputArgs().getFormat();
			tableData[i][2] = inputListArgs.getPath();
			tableData[i][3] = inputListArgs.getOutputArgs().getInputDbType() == null ? null
					: inputListArgs.getOutputArgs().getInputDbType() + ","
							+ inputListArgs.getOutputArgs().getInputDbHost() + ","
							+ inputListArgs.getOutputArgs().getInputDbName() + "," + inputListArgs.getName();
			tableData[i][4] = inputListArgs.getOutputArgs().getState();
			tableData[i][5] = inputListArgs.getOutputArgs().getProgress() + "%";
			num++;
		}
		for (int i = 0; i < tableData.length; i++) {
			inputListTabModel.addRow(tableData[i]);
		}

		// 矢量文件（旧格式）
		tableData = new Object[ConfigVectorFileDsOldWindow.getVfList().size()][6];
		for (int i = 0; i < ConfigVectorFileDsOldWindow.getVfList().size(); i++) {
			ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().setTaskNo(num + "");
			ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().setFormat("旧");
			inputListArgs = ConfigVectorFileDsOldWindow.getVfList().get(i);
			tableData[i][0] = num;
			tableData[i][1] = inputListArgs.getOutputArgs().getFormat();
			tableData[i][2] = inputListArgs.getPath();
			tableData[i][3] = inputListArgs.getOutputArgs().getInputDbType() == null ? null
					: inputListArgs.getOutputArgs().getInputDbType() + ","
							+ inputListArgs.getOutputArgs().getInputDbHost() + ","
							+ inputListArgs.getOutputArgs().getInputDbName() + "," + inputListArgs.getName();
			tableData[i][4] = inputListArgs.getOutputArgs().getState();
			tableData[i][5] = inputListArgs.getOutputArgs().getProgress() + "%";
			num++;
		}
		for (int i = 0; i < tableData.length; i++) {
			inputListTabModel.addRow(tableData[i]);
		}

		// 数据库（新格式）
		tableData = new Object[ConfigDbInputDsNewWindow.getDatabaseDetailList().size()][6];
		DataBaseInfosDetail databaseInfosDetail;
		for (int j = 0; j < ConfigDbInputDsNewWindow.getDatabaseDetailList().size(); j++) {
			ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs().setTaskNo(num + "");
			ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs().setFormat("新");
			databaseInfosDetail = ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j);
			tableData[j][0] = num;
			tableData[j][1] = databaseInfosDetail.getOutputArgs().getFormat();
			tableData[j][2] = databaseInfosDetail.getDbType() + "," + databaseInfosDetail.getDbHost() + ","
					+ databaseInfosDetail.getDatabaseName() + "," + databaseInfosDetail.getTableName();
			tableData[j][3] = databaseInfosDetail.getOutputArgs().getInputPath() == null
					? databaseInfosDetail.getOutputArgs().getInputDbType() == null ? null
							: databaseInfosDetail.getOutputArgs().getInputDbType() + ","
									+ databaseInfosDetail.getOutputArgs().getInputDbHost() + ","
									+ databaseInfosDetail.getOutputArgs().getInputDbName() + ","
									+ databaseInfosDetail.getOutputArgs().getInputName()
					: databaseInfosDetail.getOutputArgs().getInputPath() + ","
							+ databaseInfosDetail.getOutputArgs().getInputName();
			tableData[j][4] = databaseInfosDetail.getOutputArgs().getState();
			tableData[j][5] = databaseInfosDetail.getOutputArgs().getProgress() + "%";
			num++;
		}
		for (int i = 0; i < tableData.length; i++) {
			inputListTabModel.addRow(tableData[i]);
		}

		// 数据库（旧格式）
		tableData = new Object[ConfigDbInputDsOldWindow.getDatabaseDetailList().size()][6];
		for (int j = 0; j < ConfigDbInputDsOldWindow.getDatabaseDetailList().size(); j++) {
			ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs().setTaskNo(num + "");
			ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs().setFormat("旧");
			databaseInfosDetail = ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j);
			tableData[j][0] = num;
			tableData[j][1] = databaseInfosDetail.getOutputArgs().getFormat();
			tableData[j][2] = databaseInfosDetail.getDbType() + "," + databaseInfosDetail.getDbHost() + ","
					+ databaseInfosDetail.getDatabaseName() + "," + databaseInfosDetail.getTableName();
			tableData[j][3] = databaseInfosDetail.getOutputArgs().getInputPath() == null
					? databaseInfosDetail.getOutputArgs().getInputDbType() == null ? null
							: databaseInfosDetail.getOutputArgs().getInputDbType() + ","
									+ databaseInfosDetail.getOutputArgs().getInputDbHost() + ","
									+ databaseInfosDetail.getOutputArgs().getInputDbName() + ","
									+ databaseInfosDetail.getOutputArgs().getInputName()
					: databaseInfosDetail.getOutputArgs().getInputPath() + ","
							+ databaseInfosDetail.getOutputArgs().getInputName();
			tableData[j][4] = databaseInfosDetail.getOutputArgs().getState();
			tableData[j][5] = databaseInfosDetail.getOutputArgs().getProgress() + "%";
			num++;
		}
		for (int i = 0; i < tableData.length; i++) {
			inputListTabModel.addRow(tableData[i]);
		}
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
				int[] row = table.getSelectedRows();

				for (int i = 0; i < row.length; i++) {
					for (int j = 0; j < ConfigVectorFileDsNewWindow.getVfList().size(); j++) {
						if (Integer.valueOf(ConfigVectorFileDsNewWindow.getVfList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							ConfigVectorFileDsNewWindow.getVfList().remove(j);
							j--;
						}
					}

					for (int j = 0; j < ConfigVectorFileDsOldWindow.getVfList().size(); j++) {
						if (Integer.valueOf(ConfigVectorFileDsOldWindow.getVfList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							ConfigVectorFileDsOldWindow.getVfList().remove(j);
							j--;
						}
					}

					for (int j = 0; j < ConfigDbInputDsNewWindow.getDatabaseDetailList().size(); j++) {
						if (Integer.valueOf(
								ConfigDbInputDsNewWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							ConfigDbInputDsNewWindow.getDatabaseDetailList().remove(j);
							j--;
						}
					}

					for (int j = 0; j < ConfigDbInputDsOldWindow.getDatabaseDetailList().size(); j++) {
						if (Integer.valueOf(
								ConfigDbInputDsOldWindow.getDatabaseDetailList().get(j).getOutputArgs().getTaskNo())
								- 1 == row[i]) {
							ConfigDbInputDsOldWindow.getDatabaseDetailList().remove(j);
							j--;
						}
					}
				}
				RefreshInputListTable();

			}
		});

		// 添加配置数据库输出
		JMenuItem delMenItem_1 = new JMenuItem();
		delMenItem_1.setText("配置数据库输出");
		delMenItem_1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int[] i = table.getSelectedRows();
				ConfigDatabaseOutput.setFrame(new ConfigDatabaseOutput(i, mainWindow, ""));
			}
		});

		// 添加配置矢量文件输出
		JMenuItem delMenItem_2 = new JMenuItem();
		delMenItem_2.setText("配置矢量文件输出");
		delMenItem_2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int[] i = table.getSelectedRows();
				boolean flag = true;
				for (int j = 0; j < i.length; j++) {
					if (table.getValueAt(i[j], 2).toString().indexOf("MSSQL") < 0
							&& table.getValueAt(i[j], 2).toString().indexOf("MySQL") < 0
							&& table.getValueAt(i[j], 2).toString().indexOf("Oracle") < 0
							&& table.getValueAt(i[j], 2).toString().indexOf("PostGIS") < 0) {
						flag = false;
						break;
					}
				}
				if (flag) {
					new ConfigVectorFileOutput(i, mainWindow, "");
					// configVectorFileOutputDialog.setModal(true);
					// configVectorFileOutputDialog.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
				} else {
					JOptionPane.showMessageDialog(null, "矢量文件不能配置矢量文件输出", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		m_popupMenu.add(delMenItem);
		m_popupMenu.add(delMenItem_1);
		m_popupMenu.add(delMenItem_2);
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
			int focusedRowIndex = table.rowAtPoint(evt.getPoint());
			if (focusedRowIndex == -1) {
				return;
			}

			// 获取已选中的行
			int[] rows = table.getSelectedRows();
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
				table.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
			}

			// 将表格所选项设为当前右键点击的行
//			table_2.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
			// 弹出菜单
			m_popupMenu.show(table, evt.getX(), evt.getY());
		}

	}

	/**
	 * 移除未选中的字段
	 */
	public void removeNoChecked(Map<String, Object> fieldToTypeMap, List<InputFieldListArgs> list) {
		Iterator<Map.Entry<String, Object>> it = fieldToTypeMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			boolean bChecked = true;
			for (InputFieldListArgs inputFieldListArgs : list) {
				if (entry.getKey().equals(inputFieldListArgs.getName())) {
					if (!inputFieldListArgs.isChecked()) {
						bChecked = false;
						break;
					}
				}
			}
			if (!bChecked) {
				it.remove();// 使用迭代器的remove()方法删除元素
			}
		}
	}

	/**
	 * 添加顶部菜单栏
	 */
	public void addMenu() {
		// 创建菜单栏
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 773, 20);
		contentPane.add(menuBar);

		// 创建一级菜单
		JMenu inputMenu = new JMenu("输入");
		JMenu editMenu = new JMenu("操作");
		JMenu helpMenu = new JMenu("帮助");

		// 菜单栏中添加一级菜单
		menuBar.add(inputMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);

		// 在一级菜单'输入'中的子菜单
		JMenuItem vectorNewMenuItem = new JMenuItem("矢量文件（新格式）");
		JMenuItem vectorOldMenuItem = new JMenuItem("矢量文件（旧格式）");
		JMenuItem dbNewMenuItem = new JMenuItem("数据库（新格式）");
		JMenuItem dbOldMenuItem = new JMenuItem("数据库（旧格式）");

		// 添加一级菜单'输入'的子菜单
		inputMenu.add(vectorNewMenuItem);
		inputMenu.add(vectorOldMenuItem);
		inputMenu.add(dbNewMenuItem);
		inputMenu.add(dbOldMenuItem);

		// 在一级菜单'操作'中的子菜单
		JMenuItem importMenuItem = new JMenuItem("导入任务");
		JMenuItem exportMenuItem = new JMenuItem("导出任务");
		JMenuItem submitMenuItem = new JMenuItem("提交任务");

		// 添加一级菜单'操作'中的子菜单
		editMenu.add(importMenuItem);
		editMenu.add(exportMenuItem);
		editMenu.add(submitMenuItem);

		// 在一级菜单栏'帮助'中的子菜单
		JMenuItem aboutMenuItem = new JMenuItem("关于");
		helpMenu.add(aboutMenuItem);

		/** 二级菜单监听start */
		// 矢量文件（新格式）
		vectorNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigVectorFileDsNewWindow(mainWindow);
			}
		});

		// 矢量文件（旧格式）
		vectorOldMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ConfigVectorFileDsOldWindow(mainWindow);
			}
		});

		// 数据库（新格式）
		dbNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigDbInputDsNewWindow.setFrame(new ConfigDbInputDsNewWindow(mainWindow));
			}
		});

		// 数据库（旧格式）
		dbOldMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigDbInputDsOldWindow.setFrame(new ConfigDbInputDsOldWindow(mainWindow));
			}
		});

		// 导入任务
		importMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		// 导出任务
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		// 提交任务
		submitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 执行提交的操作
				String schedule = "";
				int totalNum = inputListTabModel.getRowCount();
				int endNum = 0;
				int startNum = 0;
				if (totalNum != 0) {
					for (int i = 0; i < inputListTabModel.getRowCount(); i++) {
						schedule = String.valueOf(inputListTabModel.getValueAt(i, 5));
						if (schedule.equals("100%")) {
							endNum++;
						} else if (!schedule.equals("0%")) {
							startNum++;
						}
					}
					if (totalNum == endNum) {// 当执行完的时候
						int n = JOptionPane.showConfirmDialog(null, "是否重新导出?", "确认导出", JOptionPane.YES_NO_OPTION);
						if (n == JOptionPane.YES_OPTION) {
							// 重新提交
							submit();
							return;
						} else if (n == JOptionPane.NO_OPTION) {
							// 什么都不做
							return;
						}
					} else if (startNum > 0) {// 没执行完的时候
						JOptionPane.showMessageDialog(null, "执行中，不能重复提交", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					submit();

				}
			}
		});

		// 关于
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutSoftware();
			}
		});
		/** 二级菜单监听end */
	}

	/**
	 * 执行提交的操作
	 */
	public void submit() {
		// 矢量文件（新格式）
		int vfNewListNum = ConfigVectorFileDsNewWindow.getVfList().size();
		for (int i = 0; i < vfNewListNum; i++) {
			if (ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getInputDbType() != null) {
				EDataSourceType dataSourceType = EDataSourceType.Shp;
				DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
				VectorFileArgs inputDataSrcArgs = new VectorFileArgs();
				inputDataSrcArgs.setDataSrcType(dataSourceType);
				inputDataSrcArgs.setVectorFilePath(ConfigVectorFileDsNewWindow.getVfList().get(i).getPath());
				inputDataSrcArgs.setCharset(ConfigVectorFileDsNewWindow.getVfList().get(i).getCharset());// 只有输入为矢量文件时才有编码格式
				inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
				try {
					Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);

					List<InputFieldListArgs> list = ConfigVectorFileDsNewWindow.getVfList().get(i).getFieldToTypeList();

					// 剔除不需要的字段
					removeNoChecked(fieldToTypeMap, list);

					inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
				} catch (Exception e2) {
					e2.printStackTrace();
				}

				String dbDataSource = null;
				// 判断类型
				switch (ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getInputDbType()) {
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
				outputMssqlDataSrc.setProgressListener(new ProgressListener() {

					@Override
					public void onProcProgress(String taskNo, int progress) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								int tmpTaskNum = 0;
								ConfigVectorFileDsNewWindow.getVfList().get(Integer.valueOf(taskNo) - 1 - tmpTaskNum)
										.getOutputArgs().setProgress(progress);
								if (progress == 100) {
									ConfigVectorFileDsNewWindow.getVfList()
											.get(Integer.valueOf(taskNo) - 1 - tmpTaskNum).getOutputArgs()
											.setState("已完成");
								} else {
									ConfigVectorFileDsNewWindow.getVfList()
											.get(Integer.valueOf(taskNo) - 1 - tmpTaskNum).getOutputArgs()
											.setState("进行中");

								}
								RefreshInputListTable();
								// System.out.println(taskNo + " " + progress);
							}
						});
						// table.setValueAt(progress, row, column);

					}
				});
				DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
				outputDataSrcArgs.setTaskNo(ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getTaskNo());
				outputDataSrcArgs.setDbType(dbDataSource);
				if(dbDataSource.equals(DbDataSource.ORACLE_DBTYPE)) {//如果是oracle，拆分host和dbname,  格式为：host&dbname
					String host = ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getInputDbHost();
					if(host.contains("&")) {//检测是否包含&符号
						String[] tmp = host.split("&");
						ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().setInputDbHost(tmp[0]);
						ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().setInputDbName(tmp[1]);
					}
				}
				outputDataSrcArgs
						.setDbHost(ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getInputDbHost());
				outputDataSrcArgs
						.setDbName(ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getInputDbName());
				outputDataSrcArgs
						.setDbUserName(ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getInputDbUser());
				outputDataSrcArgs
						.setDbPwd(ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getInputDbPassword());
				outputDataSrcArgs.setDbPort(ConfigVectorFileDsNewWindow.getVfList().get(i).getOutputArgs().getInputDbPort());
				outputDataSrcArgs.setDataSrcType(dataSourceType);
				outputDataSrcArgs.setTableName(ConfigVectorFileDsNewWindow.getVfList().get(i).getName());
				outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

				cachedThreadPool.execute(new Runnable() {
					public void run() {
						outputMssqlDataSrc.exportData(inputShpDataSrc);
					}
				});
			}
		}

		// 矢量文件（旧格式）
		int vfOldListNum = ConfigVectorFileDsOldWindow.getVfList().size();
		for (int i = 0; i < vfOldListNum; i++) {
			EDataSourceType dataSourceType = EDataSourceType.Shp;
			DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
			OldVectorFileConfigArgs inputDataSrcArgs = new OldVectorFileConfigArgs();
			inputDataSrcArgs.setDataSrcType(dataSourceType);
			inputDataSrcArgs.setVectorFilePath(ConfigVectorFileDsOldWindow.getVfList().get(i).getPath());
			inputDataSrcArgs.setCharset(ConfigVectorFileDsOldWindow.getVfList().get(i).getCharset());// 只有输入为矢量文件时才有编码格式
			inputDataSrcArgs
					.setFieldName(ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getFieldName());
			inputDataSrcArgs.setBinaryDataType(
					ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getBinaryDataType());
			inputDataSrcArgs.setType(ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getType());
			inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
			try {
				Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);

				List<InputFieldListArgs> list = ConfigVectorFileDsOldWindow.getVfList().get(i).getFieldToTypeList();

				// 剔除不需要的字段
				removeNoChecked(fieldToTypeMap, list);

				inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			String dbDataSource = null;
			// 判断类型
			switch (ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getInputDbType()) {
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

			outputMssqlDataSrc.setProgressListener(new ProgressListener() {

				@Override
				public void onProcProgress(String taskNo, int progress) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							int tmpTaskNum = 0;
							tmpTaskNum += vfNewListNum;
							ConfigVectorFileDsOldWindow.getVfList().get(Integer.valueOf(taskNo) - 1 - tmpTaskNum)
									.getOutputArgs().setProgress(progress);
							if (progress == 100) {
								ConfigVectorFileDsOldWindow.getVfList().get(Integer.valueOf(taskNo) - 1 - tmpTaskNum)
										.getOutputArgs().setState("已完成");
							} else {
								ConfigVectorFileDsOldWindow.getVfList().get(Integer.valueOf(taskNo) - 1 - tmpTaskNum)
										.getOutputArgs().setState("进行中");

							}
							RefreshInputListTable();
						}
					});

				}
			});
			DbConfigArgs outputDataSrcArgs = new DbConfigArgs();
			outputDataSrcArgs.setTaskNo(ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getTaskNo());
			outputDataSrcArgs.setDbType(dbDataSource);
			if(dbDataSource.equals(DbDataSource.ORACLE_DBTYPE)) {//如果是oracle，拆分host和dbname,  格式为：host&dbname
				String host = ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getInputDbHost();
				if(host.contains("&")) {//检测是否包含&符号
					String[] tmp = host.split("&");
					ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().setInputDbHost(tmp[0]);
					ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().setInputDbName(tmp[1]);
				}
			}
			outputDataSrcArgs
					.setDbHost(ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getInputDbHost());
			outputDataSrcArgs
					.setDbName(ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getInputDbName());
			outputDataSrcArgs
					.setDbUserName(ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getInputDbUser());
			outputDataSrcArgs
					.setDbPwd(ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getInputDbPassword());
			outputDataSrcArgs.setDbPort(ConfigVectorFileDsOldWindow.getVfList().get(i).getOutputArgs().getInputDbPort());
			outputDataSrcArgs.setTableName(ConfigVectorFileDsOldWindow.getVfList().get(i).getName());
			outputDataSrcArgs.setDataSrcType(dataSourceType);
			outputMssqlDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

			cachedThreadPool.execute(new Runnable() {
				public void run() {
					outputMssqlDataSrc.exportOldData(inputShpDataSrc);
				}
			});

		}

		// 数据库（新格式）
		int dbDetailListNum = ConfigDbInputDsNewWindow.getDatabaseDetailList().size();
		for (int i = 0; i < dbDetailListNum; i++) {
			if (ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getOutputArgs().getInputPath() != null) {
				EDataSourceType dataSourceType = null;
				String dbDataSource = null;
				// 判断类型
				switch (ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getDbType()) {
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
				
				if(dbDataSource.equals(DbDataSource.ORACLE_DBTYPE)) {//如果是oracle，拆分host和dbname,  格式为：host&dbname
					String host = ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getDbHost();
					if(host.contains("&")) {//检测是否包含&符号
						String[] tmp = host.split("&");
						ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).setDbHost(tmp[0]);
						ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).setDatabaseName(tmp[1]);
					}
				}
				
				inputDataSrcArgs.setDbHost(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getDbHost());
				
				if(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getDbHost().indexOf("\\") != -1) {
					ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).setDbPort(null);
				}
				
				inputDataSrcArgs.setDbPort(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getDbPort());
				inputDataSrcArgs.setDbName(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getDatabaseName());
				inputDataSrcArgs.setDbUserName(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getDbUser());
				inputDataSrcArgs.setDbPwd(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getDbPassword());
				inputDataSrcArgs.setDataSrcType(dataSourceType);
				inputDataSrcArgs.setTableName(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getTableName());
				inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
				try {
					Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);

					List<InputFieldListArgs> list = ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i)
							.getInputFieldList();

					// 剔除不需要的字段
					removeNoChecked(fieldToTypeMap, list);

					inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
				} catch (Exception e2) {
					e2.printStackTrace();
				}

				dataSourceType = EDataSourceType.Shp;
				DataSourceBase outputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
				outputShpDataSrc.setProgressListener(new ProgressListener() {

					@Override
					public void onProcProgress(String taskNo, int progress) {
						// table.setValueAt(progress, row, column);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								int tmpTaskNum = 0;
								tmpTaskNum += vfNewListNum;
								tmpTaskNum += vfOldListNum;

								ConfigDbInputDsNewWindow.getDatabaseDetailList()
										.get(Integer.valueOf(taskNo) - 1 - tmpTaskNum).getOutputArgs()
										.setProgress(progress);
								if (progress == 100) {
									ConfigDbInputDsNewWindow.getDatabaseDetailList()
											.get(Integer.valueOf(taskNo) - 1 - tmpTaskNum).getOutputArgs()
											.setState("已完成");
								} else {
									ConfigDbInputDsNewWindow.getDatabaseDetailList()
											.get(Integer.valueOf(taskNo) - 1 - tmpTaskNum).getOutputArgs()
											.setState("进行中");

								}
								RefreshInputListTable();

								// System.out.println(taskNo + " " + progress);
							}
						});
					}
				});
				VectorFileArgs outputDataSrcArgs = new VectorFileArgs();
				outputDataSrcArgs
						.setTaskNo(ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getOutputArgs().getTaskNo());
				outputDataSrcArgs.setDataSrcType(dataSourceType);
				outputDataSrcArgs.setVectorFilePath(
						ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getOutputArgs().getInputPath() + "/"
								+ ConfigDbInputDsNewWindow.getDatabaseDetailList().get(i).getOutputArgs().getInputName()
								+ ".shp");
				outputShpDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

				cachedThreadPool.execute(new Runnable() {
					public void run() {
						outputShpDataSrc.exportData(inputMssqlDataSrc);
					}
				});
			}
		}

		// 数据库（旧格式）
		for (int i = 0; i < ConfigDbInputDsOldWindow.getDatabaseDetailList().size(); i++) {
			if (ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getOutputArgs().getInputPath() != null) {
				EDataSourceType dataSourceType = null;
				String dbDataSource = null;
				// 判断类型
				switch (ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getDbType()) {
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
				OldDbConfigArgs inputDataSrcArgs = new OldDbConfigArgs();
				inputDataSrcArgs.setDbType(dbDataSource);
				
				if(dbDataSource.equals(DbDataSource.ORACLE_DBTYPE)) {//如果是oracle，拆分host和dbname,  格式为：host&dbname
					String host = ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getDbHost();
					if(host.contains("&")) {//检测是否包含&符号
						String[] tmp = host.split("&");
						ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).setDbHost(tmp[0]);
						ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).setDatabaseName(tmp[1]);
					}
				}
				
				inputDataSrcArgs.setDbHost(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getDbHost());
				if(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getDbHost().indexOf("\\") != -1) {
					ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).setDbPort(null);
				}
				inputDataSrcArgs.setDbPort(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getDbPort());
				inputDataSrcArgs.setDbName(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getDatabaseName());
				inputDataSrcArgs.setDbUserName(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getDbUser());
				inputDataSrcArgs.setDbPwd(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getDbPassword());
				inputDataSrcArgs.setDataSrcType(dataSourceType);
				inputDataSrcArgs.setTableName(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getTableName());
				inputDataSrcArgs.setFilter(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getFilter());
				inputDataSrcArgs.setFieldName(
						ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getOutputArgs().getFieldName());
				inputDataSrcArgs
						.setType(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getOutputArgs().getType());
				inputDataSrcArgs.setBinaryDataType(
						ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getOutputArgs().getBinaryDataType());
				inputMssqlDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
				try {
					Map<String, Object> fieldToTypeMap = inputMssqlDataSrc.getVectorPropInfo(inputDataSrcArgs);

					List<InputFieldListArgs> list = ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i)
							.getInputFieldList();

					// 剔除不需要的字段
					removeNoChecked(fieldToTypeMap, list);

					inputDataSrcArgs.setFieldToTypeMap(fieldToTypeMap);
				} catch (Exception e2) {
					e2.printStackTrace();
				}

				dataSourceType = EDataSourceType.Shp;
				DataSourceBase outputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
				outputShpDataSrc.setProgressListener(new ProgressListener() {

					@Override
					public void onProcProgress(String taskNo, int progress) {
						// table.setValueAt(progress, row, column);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								System.out.println(taskNo + "   " + progress);
								int tmpTaskNum = 0;
								tmpTaskNum += vfNewListNum;
								tmpTaskNum += vfOldListNum;
								tmpTaskNum += dbDetailListNum;
								ConfigDbInputDsOldWindow.getDatabaseDetailList()
										.get(Integer.valueOf(taskNo) - 1 - tmpTaskNum).getOutputArgs()
										.setProgress(progress);
								if (progress == 100) {
									ConfigDbInputDsOldWindow.getDatabaseDetailList()
											.get(Integer.valueOf(taskNo) - 1 - tmpTaskNum).getOutputArgs()
											.setState("已完成");
								} else {
									ConfigDbInputDsOldWindow.getDatabaseDetailList()
											.get(Integer.valueOf(taskNo) - 1 - tmpTaskNum).getOutputArgs()
											.setState("进行中");

								}
								RefreshInputListTable();
							}
						});

					}
				});
				VectorFileArgs outputDataSrcArgs = new VectorFileArgs();
				outputDataSrcArgs
						.setTaskNo(ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getOutputArgs().getTaskNo());
				outputDataSrcArgs.setDataSrcType(dataSourceType);
				outputDataSrcArgs.setVectorFilePath(
						ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getOutputArgs().getInputPath() + "/"
								+ ConfigDbInputDsOldWindow.getDatabaseDetailList().get(i).getOutputArgs().getInputName()
								+ ".shp");
				outputShpDataSrc.setDataSrcCfgArgs(outputDataSrcArgs);

				cachedThreadPool.execute(new Runnable() {
					public void run() {
						outputShpDataSrc.exportOldData(inputMssqlDataSrc);
					}
				});
			}
		}
	}

	public static MainWindow getMainWindow() {
		return mainWindow;
	}

	public static void setMainWindow(MainWindow mainWindow) {
		MainWindow.mainWindow = mainWindow;
	}
}
