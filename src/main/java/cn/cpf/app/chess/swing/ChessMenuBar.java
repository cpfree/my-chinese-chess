package cn.cpf.app.chess.swing;

import cn.cpf.app.chess.inter.LambdaMouseListener;
import cn.cpf.app.chess.main.ChessConfig;

import javax.swing.*;

public class ChessMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	private BoardPanel gBPanel = null;

	public ChessMenuBar() {
		addUserMenu();
		addGameMenu();
		addDebugMenu();
		addSettingMenu();
	}

	private void addGameMenu() {
		JMenu mu_game = new JMenu("game");
		add(mu_game);
		
		JMenuItem ml_reopen = new JMenuItem("重新开局");
		mu_game.add(ml_reopen);
		ml_reopen.addMouseListener((LambdaMouseListener) (e) -> {
		});
	}

	private void addSettingMenu() {
		JMenu mu_setting = new JMenu("setting");
		add(mu_setting);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("难度");
		mu_setting.add(mntmNewMenuItem);
	}


	private void addDebugMenu() {
		JMenu mu_debug = new JMenu("Debug");
		add(mu_debug);
		
		JMenuItem ml_showScore = new JMenuItem("查看分数");
		ml_showScore.addMouseListener((LambdaMouseListener) (e) -> {
		});
		mu_debug.add(ml_showScore);
		
		JMenuItem com_go = new JMenuItem("COM GO");
		com_go.addMouseListener((LambdaMouseListener) (e) -> {
			getBoardPanel().comRunOneStep();
		});
		mu_debug.add(com_go);

		JMenuItem com_start = new JMenuItem("COM start");
		com_start.addMouseListener((LambdaMouseListener) (e) -> {
			ChessConfig.comRunnable = true;
			getBoardPanel().run();
		});
		mu_debug.add(com_start);
	}


	private void addUserMenu() {
		JMenu mn_user = new JMenu("user");
		add(mn_user);
		
		JMenuItem ml_userInfo = new JMenuItem("个人信息");
		mn_user.add(ml_userInfo);
		
		JMenuItem ml_exit = new JMenuItem("退出");
		mn_user.add(ml_exit);
	}

	private BoardPanel getBoardPanel(){
		return (BoardPanel) ((ChessPanel) ((ChessFrame) ChessMenuBar.this.getParent().getParent().getParent()).getContentPane()).getBoardPanel();
	}
}
