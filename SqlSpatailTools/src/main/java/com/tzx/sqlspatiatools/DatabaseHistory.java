package com.tzx.sqlspatiatools;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tzx.sqlspatiatools.bean.DataBaseInfos;
import com.tzx.sqlspatiatools.utils.CreateFileUtil;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * 数据库历史记录
 * @author Administrator
 *
 */
public class DatabaseHistory extends JFrame {

	private JPanel contentPane;
	private JTable table_1;

	private DefaultTableModel inputListTabModel;

	private final String[] tableHead = { "记录ID", "记录信息" };
	private JButton button;

	private Gson gson = new Gson();

	private int row;

	private ConfigDatabaseWindow configDatabaseWindow;
	
	private ConfigDatabaseOutput configDatabaseOutput;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DatabaseHistory frame = new DatabaseHistory(new ConfigDatabaseWindow("New"),null);
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
	public DatabaseHistory(ConfigDatabaseWindow configDatabaseWindow,ConfigDatabaseOutput configDatabaseOutput ) {
		super("历史记录");
		this.configDatabaseWindow = configDatabaseWindow;
		this.configDatabaseOutput = configDatabaseOutput;
		setVisible(true);
		setBounds(100, 100, 403, 449);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		contentPane.setLayout(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);// 只关闭当前窗口，不会关闭父窗口
		setResizable(false);

		// 初始化表格
		JScrollPane jScrollPane_1 = inputList();// 初始化输入表格
		contentPane.add(jScrollPane_1);

		// 初始化数据
		initTableData();

		// 初始化确定按钮
		button = addConfirm();
		contentPane.add(button);
	}

	public JScrollPane inputList() {
		Object[][] tableData = new Object[][] {};
		inputListTabModel = new DefaultTableModel(tableData, tableHead);
		table_1 = new JTable((TableModel) inputListTabModel) {
			public boolean isCellEditable(int row, int column) {
				if (column == 0 || column == 1) {
					return false;// 表格不允许被编辑
				}
				return true;
			}
		};
		jTableMouseListener(table_1); // 增加鼠标监听
		JScrollPane jScrollPane_1 = new JScrollPane(table_1);
		jScrollPane_1.setBounds(10, 0, 367, 356);
		return jScrollPane_1;
	}

	/**
	 * 增加鼠标监听
	 * 
	 * @param table_1
	 */
	public void jTableMouseListener(JTable table_1) {
		table_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {// 点击几次，这里是单击事件
					saveIdNum(table_1);
				}
			}
		});
	}

	/**
	 * 保存当前行数
	 * 
	 * @param table_1
	 */
	public void saveIdNum(JTable table_1) {
		// 保存选中的行数
		row = table_1.getSelectedRow();
	}

	public void initTableData() {
		// 读取json文件中的数据
		String laststr = CreateFileUtil.getDatafromFile("databaseinfo");
		List<DataBaseInfos> dbInfoList = gson.fromJson(laststr, new TypeToken<List<DataBaseInfos>>() {
		}.getType());

		Object[][] tableData = new Object[][] {};
		if (dbInfoList != null && dbInfoList.size() > 0) {
			tableData = new Object[dbInfoList.size()][2];
			DataBaseInfos dataBaseInfos;
			for (int i = 0; i < dbInfoList.size(); i++) {
				dataBaseInfos = dbInfoList.get(i);
				tableData[i][0] = (i + 1);
				tableData[i][1] = dataBaseInfos.getDbType() + "," + dataBaseInfos.getDbHost();
			}
		}
		for (int i = 0; i < tableData.length; i++) {
			inputListTabModel.addRow(tableData[i]);
		}
	}

	public JButton addConfirm() {
		button = new JButton("确定");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(configDatabaseWindow != null) {
					// 初始化父类窗口的信息
					configDatabaseWindow.initDbInfo(row);
				}
				
				if(configDatabaseOutput != null) {
					configDatabaseOutput.initDbInfo(row);
				}
				
				//关闭当前窗口
				dispose();
			}
		});
		button.setBounds(284, 366, 93, 23);
		return button;
	}

}
