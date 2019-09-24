package com.tzx.test1;

import javax.swing.JInternalFrame;

public class InternalFrame extends JInternalFrame {
	public InternalFrame() {
		super();
		setClosable(true);
		setIconifiable(true);
		setTitle("内部窗体");
		setBounds(50, 50, 400, 300);
		setVisible(true);
	}
}