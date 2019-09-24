package com.tzx.sqlspatiatools;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.tzx.datasource.DataSourceBase;
import com.tzx.datasource.DataSourceFactory;
import com.tzx.datasource.EDataSourceType;
import com.tzx.datasource.argument.VectorFileArgs;
import com.tzx.sqlspatiatools.bean.InputFieldListArgs;
import com.tzx.sqlspatiatools.bean.InputListArgs;
import com.tzx.sqlspatiatools.utils.TableCellListener;
import javax.swing.JTextField;
import javax.swing.JComboBox;

/**
 * 矢量文件旧格式
 * 
 * @author Administrator
 *
 */
public class ConfigVectorFileDsOldWindow extends JFrame {

	private JPanel contentPane;
	private JTable table_1;
	private JTable table_2;
	private JPopupMenu m_popupMenu;

	private List<InputListArgs> vectorFileList;
	private DefaultTableModel inputListTabModel;
	private DefaultTableModel fieldListTabModel;

	private final String[] tableHead = { "路径", "表名" };
	private final String[] tableHead2 = { "是否选中", "列名", "空间列", "Id列" };

	private MainWindow mainWindow;

	private static List<InputListArgs> vfList = new ArrayList<InputListArgs>();

	private JFileChooser fileChooser = new JFileChooser();
	private JFileChooser chooser = new JFileChooser();

	private String sourceType;
	private JTextField textField;
	private JTextField textField_1;
	private JComboBox comboBox;
	private JComboBox comboBox_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConfigVectorFileDsOldWindow frame = new ConfigVectorFileDsOldWindow(new MainWindow());
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
	public ConfigVectorFileDsOldWindow(MainWindow mainWindow) {
		this.sourceType = "矢量文件（旧格式）";
		if (sourceType.equals("矢量文件（新格式）")) {
			this.setTitle("矢量文件（新格式）");
		} else {
			this.setTitle("矢量文件（旧格式）");
		}
		initVaribles();
		initUI();
		createPopupMenu();
		this.mainWindow = mainWindow;

		// 点击窗体，取消输入列选中状态
//		addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				table_1.clearSelection();
//			}
//		});

	}

	private void initUI() {
		// 初始化窗口属性.
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);// 只关闭子窗口，不关闭父窗口
		setBounds(100, 100, 985, 670);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setResizable(false);

		// 初始化单选按钮.
		JButton btnNewButton = selectFile(); // 选择文件按钮
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = selectDire(); // 选择目录按钮
		contentPane.add(btnNewButton_1);

		// 初始化字段类型项
		addVectorType();

		// 初始化源空间字段名项
		addGeoVector();

		// 初始化源空间数据类型项
		addGeoVectorType();

		// 初始化编码格式
		addCharset();

		// 初始化标题（输入列表、字段信息）
		JLabel lblNewLabel = newJLabel1();
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = newJLabel2();
		contentPane.add(lblNewLabel_1);

		// 初始化表格
		JScrollPane jScrollPane_1 = inputList();// 初始化输入表格
		contentPane.add(jScrollPane_1);

		JScrollPane jScrollPane_2 = outputList();// 初始化字段信息
		contentPane.add(jScrollPane_2);

		// 初始化确认按钮
		JButton btnNewButton_2 = confirm();
		contentPane.add(btnNewButton_2);

		// 初始化配置数据库输出
		JButton button = new JButton("配置数据库输出");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] i = table_1.getSelectedRows();
				if (i.length > 0) {
					ConfigDatabaseOutput.setFrame(new ConfigDatabaseOutput(i, null, sourceType));
				} else {
					JOptionPane.showMessageDialog(null, "请选择要配置的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		button.setBounds(45, 587, 155, 23);
		contentPane.add(button);

	}

	private void initVaribles() {
		vectorFileList = new ArrayList<InputListArgs>();
	}

	/**
	 * 刷新输入表格的数据
	 */
	public void RefreshInputListTable() {
		Object[][] tableData = new Object[][] {};

		if (vectorFileList != null && vectorFileList.size() > 0) {
			tableData = new Object[vectorFileList.size()][2];
			InputListArgs inputListArgs;
			for (int i = 0; i < vectorFileList.size(); i++) {
				inputListArgs = vectorFileList.get(i);
				tableData[i][0] = inputListArgs.getPath();
				tableData[i][1] = inputListArgs.getName();
			}
		}
		for (int i = 0; i < tableData.length; i++) {
			inputListTabModel.addRow(tableData[i]);
		}
	}

	/**
	 * 刷新字段表格的信息
	 */
	public void RefreshFieldListTable(int row) {
		Object[][] tableData = new Object[][] {};
		if (vfList != null && vfList.size() > 0) {
			InputListArgs inputListArgs = vfList.get(row);
			List<InputFieldListArgs> fieldListArgsList = inputListArgs.getFieldToTypeList();

			int fieldNum = fieldListArgsList.size();
			tableData = new Object[fieldNum][4];
			InputFieldListArgs inputFieldListArgs = null;
			String className = "";
			for (int i = 0; i < fieldNum; i++) {
				inputFieldListArgs = fieldListArgsList.get(i);
				tableData[i][0] = inputFieldListArgs.isChecked();
				if (inputFieldListArgs.getName().equals("the_geom")) {
					tableData[i][1] = "geom";
				} else {
					tableData[i][1] = inputFieldListArgs.getName();
				}

				tableData[i][3] = inputFieldListArgs.isIdChecked();

				className = inputFieldListArgs.getType().getName();
				if (className.endsWith("Point") || className.endsWith("MultiPoint") || className.endsWith("Line")
						|| className.endsWith("LineString") || className.endsWith("MultiLineString")
						|| className.endsWith("Polygon") || className.endsWith("MultiPolygon")) {
					tableData[i][2] = new Boolean(true);
					// 给字段类型赋值
					if (className.indexOf("Point") != -1) {
						textField_1.setText("Point");
						inputListArgs.getOutputArgs().setType("Point");
						inputListArgs.getOutputArgs().setFieldName("LGTD,LTTD");
						textField.setText("LGTD,LTTD");
					} else if (className.indexOf("Line") != -1) {
						textField_1.setText("Line");
						inputListArgs.getOutputArgs().setType("Line");
						inputListArgs.getOutputArgs().setFieldName("vertex");
						textField.setText("vertex");
					} else if (className.indexOf("Polygon") != -1) {
						textField_1.setText("Polygon");
						inputListArgs.getOutputArgs().setType("Polygon");
						inputListArgs.getOutputArgs().setFieldName("vertex");
						textField.setText("vertex");
					}
				} else {
					tableData[i][2] = new Boolean(false);
				}
			}

		}
		for (int i = 0; i < fieldListTabModel.getRowCount(); i++) {
			fieldListTabModel.removeRow(i);
			i--;
		}
		for (int i = 0; i < tableData.length; i++) {
			fieldListTabModel.addRow(tableData[i]);
		}
	}

	/**
	 * 选择文件按钮
	 * 
	 * @return
	 */
	public JButton selectFile() {
		// 初始化单选按钮.
		JButton btnNewButton = new JButton("选择文件");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1.弹框，选择一个shp文件
//				File file = JFileDataStoreChooser.showOpenFile("shp", null);
				String filePath = JFileChooser1();
				File file = new File(filePath);

				// 判断不能为空
				if (file == null) {
					return;
				}
				if (!file.exists()) {
					return;
				}

				// 判断是否重复
				for (int i = 0; i < inputListTabModel.getRowCount(); i++) {
					String path = (String) inputListTabModel.getValueAt(i, 0);
					if (file.getPath().equals(path)) {
						return;
					}
				}

				// 输入列表.
				InputListArgs vectorFile = new InputListArgs();
				vectorFile.setPath(file.getPath());
				String name = file.getName();
				String[] a = name.split(".shp");
				for (String string : a) {
					vectorFile.setName(string);
					break;
				}

				// 字段列表.
				List<InputFieldListArgs> fieldToTypeList = getFieldToTypeList(file.getPath()); // 获取字段列表
				vectorFile.setFieldToTypeList(fieldToTypeList);
				vectorFile.getOutputArgs().setBinaryDataType("double");
				vectorFile.setCharset("GBK");
				vectorFileList.clear();
				vectorFileList.add(vectorFile);

				vfList.add(vectorFile);

				// 刷新输入列表.
				RefreshInputListTable();

				// 刷新字段列表.
				RefreshFieldListTable(vfList.size() - 1);
			}
		});
		btnNewButton.setBounds(157, 24, 93, 23);
		return btnNewButton;
	}

	/**
	 * 选择目录按钮
	 * 
	 * @return
	 */
	public JButton selectDire() {
		JButton btnNewButton_1 = new JButton("选择目录");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectPath = "";

				chooser.setCurrentDirectory(chooser.getSelectedFile());
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// 设置只能选择目录
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					selectPath = chooser.getSelectedFile().getPath();
					System.out.println("你选择的目录是：" + selectPath);
				}
				File dirPath = new File(selectPath);
				List<File> fileList = getAllJavaFilePaths(dirPath);
				boolean repeat = false;
				for (File file : fileList) {
					// 判断不能为空
					if (file == null) {
						return;
					}
					if (!file.exists()) {
						return;
					}

					// 判断是否重复
					for (int i = 0; i < inputListTabModel.getRowCount(); i++) {
						String path = (String) inputListTabModel.getValueAt(i, 0);
						if (file.getPath().equals(path)) {
							repeat = true;
							break;
						} else {
							repeat = false;
						}
					}
					if (repeat) {
						continue;
					}

					// 输入列表.
					InputListArgs vectorFile = new InputListArgs();
					vectorFile.setPath(file.getPath());
					String name = file.getName();
					String[] a = name.split(".shp");
					for (String string : a) {
						vectorFile.setName(string);
						break;
					}

					// 字段列表.
					List<InputFieldListArgs> fieldToTypeList = getFieldToTypeList(file.getPath()); // 获取字段列表
					vectorFile.setFieldToTypeList(fieldToTypeList);
					vectorFile.getOutputArgs().setBinaryDataType("double");
					vectorFile.setCharset("GBK");
					vectorFileList.clear();
					vectorFileList.add(vectorFile);

					vfList.add(vectorFile);

					// 刷新输入列表.
					RefreshInputListTable();

					// 刷新字段列表.
					RefreshFieldListTable(vfList.size() - 1);
				}
			}
		});
		btnNewButton_1.setBounds(643, 24, 93, 23);
		return btnNewButton_1;
	}

	/**
	 * 输入列表标题
	 * 
	 * @return
	 */
	public JLabel newJLabel1() {
		JLabel lblNewLabel = new JLabel("输入列表：");
		lblNewLabel.setBounds(45, 66, 74, 15);
		return lblNewLabel;
	}

	/**
	 * 字段信息标题
	 * 
	 * @return
	 */
	public JLabel newJLabel2() {
		JLabel lblNewLabel_1 = new JLabel("字段信息：");
		lblNewLabel_1.setBounds(413, 66, 74, 15);
		return lblNewLabel_1;
	}

	/**
	 * 输入表格
	 * 
	 * @return
	 */
	public JScrollPane inputList() {
		Object[][] tableData = new Object[][] {};
		if (inputListTabModel == null) {
			inputListTabModel = new DefaultTableModel(tableData, tableHead);
		}
		table_1 = new JTable((TableModel) inputListTabModel) {
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return false;// 表格不允许被编辑
				}
				return true;
			}
		};

		table_1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table_1.getColumnModel().getColumn(0).setPreferredWidth(142);
		table_1.getColumnModel().getColumn(1).setPreferredWidth(142);

		if (vfList != null && vfList.size() > 0) {
			vectorFileList.addAll(vfList);
		}

		RefreshInputListTable();
		jTableMouseListener(table_1); // 增加鼠标监听

		table_1.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				if (table_1.getRowCount() != 0 && table_1.getRowCount() == vfList.size()) {
					int row = e.getFirstRow();// 改变的单元格所在的行索引，起始值为0
					String inputName = table_1.getValueAt(row, 1).toString();
					System.out.println(inputName);
					vfList.get(row).setName(inputName);
				}

			}
		});

		JScrollPane jScrollPane_1 = new JScrollPane(table_1);
		jScrollPane_1.setBounds(45, 91, 284, 466);
		return jScrollPane_1;
	}

	/**
	 * 字段信息表格
	 * 
	 * @return
	 */
	public JScrollPane outputList() {
		Object[][] tableData2 = new Object[][] {};
		fieldListTabModel = new DefaultTableModel(tableData2, tableHead2);
		table_2 = new JTable(fieldListTabModel) {
			public boolean isCellEditable(int row, int column) {
				if (column == 1 || column == 2) {
					return false;// 表格不允许被编辑
				}
				return true;
			}
		};
		RefreshFieldListTable(vfList.size() - 1);

		// 添加复选框
		TableColumn column1 = table_2.getColumnModel().getColumn(0);
		column1.setCellEditor(table_2.getDefaultEditor(Boolean.class));
		column1.setCellRenderer(table_2.getDefaultRenderer(Boolean.class));

		// 添加复选框
		TableColumn column2 = table_2.getColumnModel().getColumn(2);
		column2.setCellEditor(table_2.getDefaultEditor(Boolean.class));
		column2.setCellRenderer(table_2.getDefaultRenderer(Boolean.class));

		// 添加复选框
		TableColumn column3 = table_2.getColumnModel().getColumn(3);
		column3.setCellEditor(table_2.getDefaultEditor(Boolean.class));
		column3.setCellRenderer(table_2.getDefaultRenderer(Boolean.class));

		// 添加表格监听（当表格数据发生变化时触发）
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				TableCellListener tcl = (TableCellListener) e.getSource();
				System.out.printf("cell changed%n");
				System.out.println("Row   : " + tcl.getRow());
				System.out.println("Column: " + tcl.getColumn());
				System.out.println("Old   : " + tcl.getOldValue());
				System.out.println("New   : " + tcl.getNewValue());

				// 改变fvList中字段列表的值
				// 先取输入列表中的选中项，如果有选中则查选中行，如果没有选中则查最后一行
				int tNum1 = table_1.getSelectedRow();
				if (tNum1 >= 0) {// 选中行
					if (tcl.getColumn() == 0) {
						vfList.get(tNum1).getFieldToTypeList().get(tcl.getRow())
								.setChecked(Boolean.valueOf(tcl.getNewValue().toString()));
					}
					if (tcl.getColumn() == 3) {
						vfList.get(tNum1).getFieldToTypeList().get(tcl.getRow())
								.setIdChecked(Boolean.valueOf(tcl.getNewValue().toString()));
					}
					RefreshFieldListTable(tNum1);
				} else {// 未选中行,默认选最后一行
					if (tcl.getColumn() == 0) {
						vfList.get(vfList.size() - 1).getFieldToTypeList().get(tcl.getRow())
								.setChecked(Boolean.valueOf(tcl.getNewValue().toString()));
					}
					if (tcl.getColumn() == 3) {
						vfList.get(tNum1).getFieldToTypeList().get(tcl.getRow())
								.setIdChecked(Boolean.valueOf(tcl.getNewValue().toString()));
					}
					RefreshFieldListTable(vfList.size() - 1);
				}

			}
		};
		new TableCellListener(table_2, action);

		JScrollPane jScrollPane_2 = new JScrollPane(table_2);
		jScrollPane_2.setBounds(413, 91, 323, 466);
		return jScrollPane_2;
	}

	/**
	 * 确认按钮
	 * 
	 * @return
	 */
	public JButton confirm() {
		JButton btnNewButton_2 = new JButton("确定");
		btnNewButton_2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// 刷新父类窗口的输入框
				mainWindow.RefreshInputListTable();
				// 关闭当前窗口
				dispose();
			}
		});
		btnNewButton_2.setBounds(764, 587, 93, 23);
		return btnNewButton_2;
	}

	/**
	 * 增加鼠标监听（输入列表专用）
	 * 
	 * @param table_1
	 */
	public void jTableMouseListener(JTable table_1) {
		table_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				mouseRightButtonClick(e);
				if (e.getClickCount() == 1) {// 点击几次，这里是单击事件
					tableChanged(table_1);
				}
			}
		});
	}

	/**
	 * 改变字段列表的值 说明：在输入表格中选中一行，则字段信息表格的数据替换成与该行对应的数据
	 * 
	 * @param table_1
	 */
	public void tableChanged(JTable table_1) {
		int row = table_1.getSelectedRow();
		String path = table_1.getValueAt(row, 0).toString(); // 得到所在行的第一个列的值，作为下面事件传递的参数
		InputListArgs vectorFile = new InputListArgs();
//		List<InputFieldListArgs> fieldToTypeList = getFieldToTypeList(path); // 获取字段列表
		List<InputFieldListArgs> fieldToTypeList = vfList.get(row).getFieldToTypeList();
		vectorFile.setFieldToTypeList(fieldToTypeList);
		vectorFileList.clear();
		vectorFileList.add(vectorFile);

		// 获取源空间数据类型并为界面赋值
		String binaryDataType = vfList.get(row).getOutputArgs().getBinaryDataType();
		if (binaryDataType != null) {
			comboBox.setSelectedItem(binaryDataType);
		} else {
			comboBox.setSelectedItem("double");
		}
		
		// 获取编码格式
		String charset = vfList.get(row).getCharset();
		if(charset != null) {
			comboBox_1.setSelectedItem(charset);
		} else {
			comboBox_1.setSelectedItem("GBK");
		}

		// 改变源空间数据字段名的值
		String fieldName = vfList.get(row).getOutputArgs().getFieldName();
		if (fieldName != null) {
			textField.setText(fieldName);
		} else {
			textField.setText("");
		}

		// 刷新字段列表.
		RefreshFieldListTable(row);
	}

	/**
	 * 获取字段列表
	 */
	public List<InputFieldListArgs> getFieldToTypeList(String filePath) {
		EDataSourceType dataSourceType = EDataSourceType.Shp;
		DataSourceBase inputShpDataSrc = (DataSourceBase) DataSourceFactory.createDataSource(dataSourceType);
		VectorFileArgs inputDataSrcArgs = new VectorFileArgs();
		inputDataSrcArgs.setDataSrcType(dataSourceType);
		inputDataSrcArgs.setVectorFilePath(filePath);
		inputShpDataSrc.setDataSrcCfgArgs(inputDataSrcArgs);
		Map<String, Object> fieldToTypeMap = inputShpDataSrc.getVectorPropInfo(inputDataSrcArgs);
		if (null == fieldToTypeMap) {
			return new ArrayList<InputFieldListArgs>();
		}
		List<InputFieldListArgs> fieldToTypeList = new ArrayList<InputFieldListArgs>();
		InputFieldListArgs inputFieldListArgs;
		for (Map.Entry<String, Object> entry : fieldToTypeMap.entrySet()) {
			inputFieldListArgs = new InputFieldListArgs();
			inputFieldListArgs.setName(entry.getKey());
			inputFieldListArgs.setType((Class<?>) entry.getValue());
			fieldToTypeList.add(inputFieldListArgs);
		}
		return fieldToTypeList;
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
				int[] row = table_1.getSelectedRows();

				for (int i = 0; i < row.length; i++) {
					System.out.println(row[i]);
					row[i] = row[i] - i;
					inputListTabModel.removeRow(row[i]);
					vfList.remove(row[i]);
				}

				// 清空字段列表中的信息
				vectorFileList.clear();

				// 刷新字段列表.
				RefreshFieldListTable(vfList.size() - 1);

			}
		});

		// 添加配置数据库输出
		JMenuItem delMenItem_1 = new JMenuItem();
		delMenItem_1.setText("配置数据库输出");
		delMenItem_1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int[] i = table_1.getSelectedRows();
				if (i.length == 0) {
					JOptionPane.showMessageDialog(null, "请选中需要配置的文件", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					ConfigDatabaseOutput.setFrame(new ConfigDatabaseOutput(i, null, sourceType));
				}
			}
		});

		m_popupMenu.add(delMenItem);
		m_popupMenu.add(delMenItem_1);
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
			int focusedRowIndex = table_1.rowAtPoint(evt.getPoint());
			if (focusedRowIndex == -1) {
				return;
			}

			// 获取已选中的行
			int[] rows = table_1.getSelectedRows();
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
				table_1.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
			}

			// 将表格所选项设为当前右键点击的行
//			table_1.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
			// 弹出菜单
			m_popupMenu.show(table_1, evt.getX(), evt.getY());
		}

	}

	/**
	 * 文件选择器，只支持选单个文件
	 */
	public String JFileChooser1() {
		fileChooser.setCurrentDirectory(fileChooser.getSelectedFile());
		// 下面这句是去掉显示所有文件这个过滤器。
		fileChooser.setAcceptAllFileFilterUsed(false);
		// 添加文件过滤器
		fileChooser.addChoosableFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				// if the file extension is .txt return true, else false
				if (file.getName().endsWith(".shp") || file.isDirectory()) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return "*.shp";
			}

		});

		/*
		 * 使用showOpenDialog()方法，显示出打开选择文件的窗口，当选择了某个文件后，或者关闭此窗口那么都会返回一个
		 * 整型数值，如果返回的是0，代表已经选择了某个文件。如果返回1代表选择了取消按钮或者直接关闭了窗口
		 */
		int result = fileChooser.showOpenDialog(null);

		// JFileChooser.APPROVE_OPTION是个整型常量，代表0。就是说当返回0的值我们才执行相关操作，否则什么也不做。
		String filePath = "";
		if (result == JFileChooser.APPROVE_OPTION) {
			filePath = fileChooser.getSelectedFile().getAbsolutePath();
			System.out.println(filePath);
		} else {
			System.out.println("您已取消并关闭了窗口！");
		}
		return filePath;

	}

	/**
	 * 根据选择的目录，查找该目录下所有的shp文件
	 * 
	 * @param srcFolder
	 * @return
	 */
	private List<File> getAllJavaFilePaths(File srcFolder) {
		// 获取该目录下所有的文件或者文件夹的File数组
		File[] fileArray = srcFolder.listFiles();
		// 储存该目录下所有的shp文件
		List<File> fileList = new ArrayList<File>();
		if (fileArray != null && fileArray.length > 0) {
			// 遍历该File数组，得到每一个File对象
			for (File file : fileArray) {
				// 判断该File对象是否是文件夹
				if (file.isDirectory()) {
					fileList.addAll(getAllJavaFilePaths(file));
				} else {
					// 继续判断是否以.shp结尾
					if (file.getName().endsWith(".shp")) {
						// 存到list中
						fileList.add(file);
						System.out.println("正在处理");
					}
				}
			}
		}
		return fileList;
	}

	/**
	 * 添加源空间数据字段名项
	 */
	public void addGeoVector() {
		JLabel label = new JLabel("源空间数据字段名：");
		label.setBounds(764, 195, 135, 15);
		contentPane.add(label);

		textField = new JTextField();
		textField.setBounds(764, 220, 182, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		// 添加焦点监听器 得到焦点和失去焦点
		textField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {

			}

			@Override
			public void focusLost(FocusEvent e) {
				// 失去焦点的处理
				// 失去焦点，将值保存起来
				String textValue = textField.getText();
				// 取输入列表中选中的行数
				int num = table_1.getSelectedRow();
				if (vfList.size() != 0) {
					if (num >= 0) {// 如果是选中状态
						vfList.get(num).getOutputArgs().setFieldName(textValue);
					} else {// 未选中
						vfList.get(vfList.size() - 1).getOutputArgs().setFieldName(textValue);
					}
				}
			}

		});

		// 初始化数据
		if (vfList.size() > 0) {
			textField.setText(vfList.get(vfList.size() - 1).getOutputArgs().getFieldName());
		}
	}

	/**
	 * 添加源空间数据类型项
	 */
	public void addGeoVectorType() {
		JLabel label_1 = new JLabel("源空间数据类型：");
		label_1.setBounds(764, 297, 182, 15);
		contentPane.add(label_1);

		comboBox = new JComboBox();
		comboBox.setBounds(764, 322, 182, 21);
		contentPane.add(comboBox);

		comboBox.addItem("double");
		comboBox.addItem("float");

		// 添加监听，选项改变时触发
		comboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String s = (String) e.getItem(); // 获取选中的值
					if (inputListTabModel != null) {
						int num = table_1.getSelectedRow(); // 获取输入列表选中的行数
						System.out.println("选中的行数为  " + num);
						if (vfList.size() != 0) {
							if (num >= 0) {// 如果选中
								vfList.get(num).getOutputArgs().setBinaryDataType(s);
							} else {// 如果未选中
								vfList.get(vfList.size() - 1).getOutputArgs().setBinaryDataType(s);
							}
						}
					}
				}
			}

		});

		// 初始化数据
		if (vfList.size() > 0) {
			comboBox.setSelectedItem(vfList.get(vfList.size() - 1).getOutputArgs().getBinaryDataType());
		}
	}

	/**
	 * 添加字段类型
	 */
	public void addVectorType() {
		JLabel label = new JLabel("字段类型：");
		label.setBounds(764, 92, 116, 15);
		contentPane.add(label);

		textField_1 = new JTextField();
		textField_1.setBounds(764, 117, 182, 21);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		textField_1.setEditable(false);

	}

	/**
	 * 添加编码格式
	 */
	public void addCharset() {
		JLabel label = new JLabel("编码格式：");
		label.setBounds(764, 402, 182, 15);
		contentPane.add(label);

		comboBox_1 = new JComboBox();
		comboBox_1.setBounds(764, 427, 182, 21);
		contentPane.add(comboBox_1);

		comboBox_1.addItem("GBK");
		comboBox_1.addItem("UTF-8");

		// 添加监听，选项改变时触发
		comboBox_1.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String s = (String) e.getItem(); // 获取选中的值
					if (inputListTabModel != null) {
						int num = table_1.getSelectedRow(); // 获取输入列表选中的行数
						System.out.println("选中的行数为  " + num);
						if (vfList.size() != 0) {
							if (num >= 0) {// 如果选中
								vfList.get(num).setCharset(s);
							} else {// 如果未选中
								vfList.get(vfList.size() - 1).setCharset(s);
							}
						}
					}
				}
			}

		});

		// 初始化数据
		if (vfList.size() > 0) {
			comboBox_1.setSelectedItem(vfList.get(vfList.size() - 1).getCharset());
		}

	}

	public static List<InputListArgs> getVfList() {
		return vfList;
	}

	public static void setVfList(List<InputListArgs> vfList) {
		ConfigVectorFileDsOldWindow.vfList = vfList;
	}
}
