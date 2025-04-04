package wordgame.main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import wordgame.extra.ImagePanel;
import wordgame.extra.NewFrame;
import wordgame.member.MemberDAO;
import wordgame.member.MemberDAOImple;
import wordgame.record.RecordDAO;
import wordgame.record.RecordDAOImple;
import wordgame.record.RecordVO;
import wordgame.word.WordDAO;
import wordgame.word.WordDAOImple;
import wordgame.word.WordVO;

public class GameMain {

	public static MemberDAO memberDao;
	public static WordDAO wordDao;
	public static RecordDAO recordDao;

	private NewFrame mainFrame;

	// 단어 에디터 표
	private JTable wordTable;
	private String[] colNames = { "단어 번호", "단어", "난이도" };
	public Object[] records = new Object[colNames.length];
	private DefaultTableModel tableModel1;

	// 기록실 표
	private JTable recordTable;
	private String[] colNames2 = { "닉네임", "단어개수", "단어점수", "시간점수", "정답률(%)" };
	public Object[] records2 = new Object[colNames2.length];
	private DefaultTableModel tableModel2;

	// 로그인정보 임시 저장(계속 메소드 호출하지 않기 위해)
	private String tempId; // 회원 정보 수정할 때 띄우기 위해서 임시로 저장
	private String tempNick; // 게임 하고 부르기 위해서 임시로 저장
	private boolean isLogIn; // 로그인했는지 안했는지 여부를 판단하기 위한 boolean 변수 설정

	// 게임 진행을 위한 변수 설정
	public ArrayList<WordVO> wordsList = new ArrayList<>(); // 단어 모두를 저장할 멤버 변수
	private JTextField gameTextInput;
	private JLabel displayWord;

	private int wordScore;
	private int currentWordIndex = 1;
	private Timer wordTimer;
	private int timeLimit = 4000;

	// 게임 기록 표시를 위한 변수 설정
	private LocalDateTime gameStartTime;
	private LocalDateTime gameEndTime;
	private Duration surviveTime;

	private JLabel currentWord;
	private JLabel passedWord;
	private JLabel missedWord;
	private JLabel skippedWord;
	private JLabel scoreMsg4;

	// 게임 기록 저장을 위한 변수 설정
	private int stackedPassedWords = 0;
	private int stackedMissedWords = 0;
	private int stackedSkippedWords = 0;
	private int accuracy;
	private String recordNickname;
	private int recordTotalWords;
	private int recordWordScore;
	private int recordSurviveScore;
	private JButton btnAfterGame;

	private int step = 0; // 타이틀이 서서히 사라지게 만들기 위한 변수 설정

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					GameMain window = new GameMain();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	} // end main()

	public GameMain() {
		initialize();
		memberDao = MemberDAOImple.getInstance();
		wordDao = WordDAOImple.getInstance();
		recordDao = RecordDAOImple.getInstance();
	}

	private void initialize() {
		// 1. 프레임 설정
		// 1) 메인프레임 설정
		mainFrame = new NewFrame();
		mainFrame.setTitle("단어 입력 게임");
		mainFrame.setSize(500, 500);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(false);
		mainFrame.getContentPane().setLayout(null);

		// 2) 엑스트라 프레임 설정
		NewFrame extraFrame = new NewFrame();
		extraFrame.setTitle("MyPage");
		extraFrame.setSize(450, 550);
		extraFrame.setResizable(false);
		extraFrame.setVisible(false);

		// 3) 기록실 프레임 설정
		NewFrame recordFrame = new NewFrame();
		recordFrame.setTitle("기록실");
		recordFrame.setSize(450, 550);
		recordFrame.setResizable(false);
		recordFrame.setVisible(false);

		// 2. 패널 설정 및 구성
		// 1) 로그인패널 설정
		JPanel taskBarPanel = new JPanel();
		taskBarPanel.setLayout(new GridLayout(1, 4));
		taskBarPanel.setBorder(null);
		taskBarPanel.setBounds(0, 433, mainFrame.getWidth() - 12, 30);
		taskBarPanel.setVisible(true);

		// 2) 타이틀패널 설정
		ImagePanel titlePanel = new ImagePanel("/titleWallpaper.png");
		titlePanel.setBounds(0, 0, mainFrame.getWidth(), mainFrame.getHeight());
		titlePanel.setLayout(null);
		titlePanel.setVisible(true);

		// 3) 게임패널 설정
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(null);
		gamePanel.setBounds(0, 0, mainFrame.getWidth(), mainFrame.getHeight());

		// 4) 엑스트라패널(MyPage, 기록실) 설정
		ImagePanel extraPanel = new ImagePanel("/titleWallpaper.png");
		extraPanel.setLayout(null);
		extraPanel.setBounds(0, 0, mainFrame.getWidth(), mainFrame.getHeight());
		extraPanel.setVisible(false);

		// 5) MyPage패널 설정
		JPanel myPagePanel = new JPanel();
		myPagePanel.setLayout(null);
		myPagePanel.setBounds(0, 0, extraFrame.getWidth(), extraFrame.getHeight());
		myPagePanel.setVisible(true);

		// 6) 회원가입 패널 설정
		JPanel insertPanel = new JPanel();
		insertPanel.setLayout(null);
		insertPanel.setBounds(0, 0, extraFrame.getWidth(), extraFrame.getHeight());
		insertPanel.setVisible(false);

		// 7) ID/PW 찾기 패널 설정
		JPanel forgotPanel = new JPanel();
		forgotPanel.setLayout(null);
		forgotPanel.setBounds(0, 0, extraFrame.getWidth(), extraFrame.getHeight());
		forgotPanel.setVisible(false);

		// 8) 회원정보 수정 패널 설정
		JPanel updatePanel = new JPanel();
		updatePanel.setLayout(null);
		updatePanel.setBounds(0, 0, extraFrame.getWidth(), extraFrame.getHeight());
		updatePanel.setVisible(false);

		// 9) 회원정보 수정 패널2 설정
		JPanel inUpdatePanel = new JPanel();
		inUpdatePanel.setLayout(null);
		inUpdatePanel.setBounds(0, 0, extraFrame.getWidth(), extraFrame.getHeight());
		inUpdatePanel.setVisible(false);

		// 10) 회원 삭제 패널 설정
		JPanel deletePanel = new JPanel();
		deletePanel.setLayout(null);
		deletePanel.setBounds(0, 0, extraFrame.getWidth(), extraFrame.getHeight());
		deletePanel.setVisible(false);

		// 11) 단어 에디터 패널 설정
		JPanel wordEditorPanel = new JPanel();
		wordEditorPanel.setLayout(null);
		wordEditorPanel.setBounds(0, 0, extraFrame.getWidth(), extraFrame.getHeight());
		wordEditorPanel.setVisible(false);

		// 12) 점수 패널 설정
		JPanel scorePanel = new JPanel();
		scorePanel.setLayout(new GridLayout(1, 4));
		scorePanel.setBorder(null);
		scorePanel.setBounds(0, 0, mainFrame.getWidth() - 13, 30);
		scorePanel.setVisible(false);

		// 13) 기록실 패널 설정
		JPanel recordPanel = new JPanel();
		recordPanel.setLayout(null);
		recordPanel.setBounds(0, 0, extraFrame.getWidth(), extraFrame.getHeight());

		mainFrame.add(taskBarPanel);
		mainFrame.add(scorePanel);
		mainFrame.add(titlePanel);
		mainFrame.add(extraPanel);
		// 게임패널은 게임 시작시에 add
		extraFrame.add(myPagePanel);
		extraFrame.add(insertPanel);
		extraFrame.add(forgotPanel);
		extraFrame.add(updatePanel);
		extraFrame.add(inUpdatePanel);
		extraFrame.add(deletePanel);
		extraFrame.add(wordEditorPanel);
		recordFrame.add(recordPanel);

		// 2. 패널 구성
		Font titleFont = new Font("나눔바른고딕", Font.BOLD, 40);
		Font btnFont = new Font("나눔바른고딕", Font.PLAIN, 20);
		Font taskBarFont = new Font("나눔바른고딕", Font.BOLD, 15);
		Font dialogFont = new Font("돋움", Font.PLAIN, 13);
		int buttonWidth = 200;
		int buttonHeight = 50;
		UIManager.put("OptionPane.messageFont", dialogFont);
		UIManager.put("OptionPane.buttonFont", dialogFont);

		// 1) 로그인패널 구성 - ID, Password
		JTextField idInput = new JTextField("ID");
		idInput.setForeground(Color.GRAY);
		taskBarPanel.add(idInput);
		idInput.setColumns(10);
		idInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (idInput.getText().equals("ID")) {
					idInput.setText("");
					idInput.setForeground(Color.black);
				} else if (!idInput.getText().equals("")) {
					idInput.setForeground(Color.black);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (idInput.getText().equals("")) {
					idInput.setForeground(Color.gray);
					idInput.setText("ID");
				}
			}
		});

		JPasswordField pwInput = new JPasswordField("Password");
		pwInput.setForeground(Color.GRAY);
		pwInput.setEchoChar((char) 0);
		taskBarPanel.add(pwInput);
		pwInput.setColumns(10);
		pwInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				char[] hiddenPw = pwInput.getPassword();
				String revealedPw = new String(hiddenPw);
				if (revealedPw.equals("Password")) {
					pwInput.setText("");
					pwInput.setForeground(Color.BLACK);
					pwInput.setEchoChar('●');
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (pwInput.getPassword().length == 0) {
					pwInput.setForeground(Color.GRAY);
					pwInput.setText("Password");
					pwInput.setEchoChar((char) 0);
				}
			}
		});

		// 2) 로그인패널 구성 - 로그인 메시지
		JLabel logInMsg = new JLabel("안녕하세요!");
		logInMsg.setBorder(new BevelBorder(BevelBorder.RAISED));
		logInMsg.setFont(taskBarFont);
		logInMsg.setHorizontalAlignment(JLabel.CENTER);

		// 최초 로그인 시간을 나타내기 위한 time의 틀
		String FormattedTime = "초기 시간";
		JLabel time = new JLabel("로그인 시각 : " + FormattedTime + "   ");
		time.setHorizontalAlignment(JLabel.RIGHT);
		time.setFont(taskBarFont);

		JLabel logOutMsg = new JLabel("Wrong Password");
		logOutMsg.setFont(taskBarFont);
		logOutMsg.setHorizontalAlignment(JLabel.LEFT);
		logOutMsg.setForeground(Color.BLUE);

		JButton btnLogIn = new JButton("로그인");
		btnLogIn.setFont(taskBarFont);
		btnLogIn.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnLogIn.setContentAreaFilled(false);
		btnLogIn.setFocusPainted(false);
		btnLogIn.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (btnLogIn.isEnabled()) {

					if (extraFrame.isVisible()) {
						extraFrame.setVisible(false);
					}
					btnLogIn.setBorder(new BevelBorder(BevelBorder.LOWERED));
					String id = idInput.getText();
					String password = new String(pwInput.getPassword());
					String nickname = memberDao.login(id, password);
					if (nickname.equals("")) { // 서버에서 불러온 닉네임이 없으면 - 로그인이 안 됐으면
						logInMsg.setForeground(Color.RED);
						logInMsg.setText("잘못된 ID/PW");
					} else { // 서버에서 불러온 닉네임이 있으면 - 로그인이 됐으면
						taskBarPanel.remove(idInput);
						taskBarPanel.remove(pwInput);
						taskBarPanel.remove(btnLogIn);
						taskBarPanel.remove(logInMsg);
						logOutMsg.setForeground(Color.BLUE);
						logOutMsg.setText("   " + nickname + "님 환영합니다 :)");
						tempId = id;
						tempNick = nickname;
						isLogIn = true;
						taskBarPanel.add(logOutMsg);
						LocalDateTime now = LocalDateTime.now(); // 현재 시각 기록
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
						String FormattedTime = now.format(formatter);
						time.setText("로그인 시각 : " + FormattedTime + "   ");
						taskBarPanel.add(time);
						taskBarPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				btnLogIn.setBorder(new BevelBorder(BevelBorder.RAISED));

			}
		});

		ActionListener enterLogin = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (extraFrame.isVisible()) {
					extraFrame.setVisible(false);
				}
				btnLogIn.setBorder(new BevelBorder(BevelBorder.LOWERED));
				String id = idInput.getText();
				String password = new String(pwInput.getPassword());
				String nickname = memberDao.login(id, password);
				if (nickname.equals("")) { // 서버에서 불러온 닉네임이 없으면 - 로그인이 안 됐으면
					logInMsg.setForeground(Color.RED);
					logInMsg.setText("잘못된 ID/PW");
					btnLogIn.setBorder(new BevelBorder(BevelBorder.RAISED));
				} else { // 서버에서 불러온 닉네임이 있으면 - 로그인이 됐으면
					taskBarPanel.remove(idInput);
					taskBarPanel.remove(pwInput);
					taskBarPanel.remove(btnLogIn);
					taskBarPanel.remove(logInMsg);
					logOutMsg.setForeground(Color.BLUE);
					logOutMsg.setText("   " + nickname + "님 환영합니다 :)");
					tempId = id;
					tempNick = nickname;
					isLogIn = true;
					taskBarPanel.add(logOutMsg);
					LocalDateTime now = LocalDateTime.now(); // 현재 시각 기록
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
					String FormattedTime = now.format(formatter);
					time.setText("로그인 시각 : " + FormattedTime + "   ");
					taskBarPanel.add(time);
					taskBarPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
				}

			}
		};
		btnLogIn.addActionListener(enterLogin); // 엔터키를 눌러도 로그인 버튼을 누른 것과 같게 만들기

		pwInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnLogIn.doClick();
			}
		});

		idInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnLogIn.doClick();
			}
		});

		logOutMsg.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {

				logOutMsg.setForeground(new Color(255, 102, 0));
				logOutMsg.setText("   " + "로그아웃할까요?");

			}

			@Override
			public void mouseExited(MouseEvent e) {
				logOutMsg.setForeground(Color.BLUE);
				logOutMsg.setHorizontalAlignment(JLabel.LEFT);
				logOutMsg.setText("   " + tempNick + "님 환영합니다 :)");
			}

			@Override
			public void mouseClicked(MouseEvent e) {

				isLogIn = false;
				if (extraFrame.isVisible()) {
					extraFrame.setVisible(isLogIn);
				}
				JOptionPane.showMessageDialog(mainFrame, "로그아웃되었습니다");
				System.out.println(tempId);
				tempId = "";
				tempNick = "";
				logInMsg.setForeground(Color.BLACK);
				logInMsg.setText("환영합니다!");
				taskBarPanel.setBorder(null);
				idInput.setText("ID");
				pwInput.setText("Password");
				pwInput.setEchoChar((char) 0);
				idInput.setForeground(Color.GRAY);
				pwInput.setForeground(Color.GRAY);
				taskBarPanel.remove(time);
				taskBarPanel.remove(logOutMsg);
				taskBarPanel.add(idInput);
				taskBarPanel.add(pwInput);
				taskBarPanel.add(btnLogIn);
				taskBarPanel.add(logInMsg);

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				btnLogIn.setBorder(new BevelBorder(BevelBorder.RAISED));
			}
		});

		taskBarPanel.add(btnLogIn);
		taskBarPanel.add(logInMsg);

		// 2) 타이틀패널 구성

		JLabel gameTitle = new JLabel("단어 입력 게임");
		gameTitle.setHorizontalAlignment(JLabel.CENTER);
		gameTitle.setBounds(120, 50, 250, 50);
		gameTitle.setFont(titleFont);
		titlePanel.add(gameTitle);

		JButton btnExtra = new JButton("MyPage / 기록실");
		btnExtra.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnExtra.setContentAreaFilled(false);
		btnExtra.setFocusPainted(false);
		btnExtra.setFont(btnFont);
		btnExtra.setBounds(144, 280, buttonWidth, buttonHeight);
		btnExtra.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				titlePanel.setVisible(false);
				extraPanel.setVisible(true);
			}
		});
		titlePanel.add(btnExtra);

		JButton btnGameStart = new JButton("게임 시작");
		btnGameStart.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnGameStart.setContentAreaFilled(false);
		btnGameStart.setFocusPainted(false);

		btnGameStart.setFont(btnFont);
		btnGameStart.setBounds(144, 340, buttonWidth, buttonHeight);
		titlePanel.add(btnGameStart);

		// 엑스트라 패널 구성
		ImageIcon backIcon = new ImageIcon(GameMain.class.getResource("/backToTitle.png"));
		JLabel backToTitle = new JLabel(backIcon);
		backToTitle.setBounds(260, 40, 150, 100);
		backToTitle.setVisible(false);
		extraPanel.add(backToTitle);

		ImageIcon myPageInfoIcon = new ImageIcon(GameMain.class.getResource("/myPage.png"));
		JLabel myPageInfo = new JLabel(myPageInfoIcon);
		myPageInfo.setBounds(260, 40, 150, 100);
		myPageInfo.setVisible(false);
		extraPanel.add(myPageInfo);

		ImageIcon scoreBoardInfoIcon = new ImageIcon(GameMain.class.getResource("/scoreBoard.png"));
		JLabel scoreBoardInfo = new JLabel(scoreBoardInfoIcon);
		scoreBoardInfo.setBounds(260, 40, 150, 100);
		scoreBoardInfo.setVisible(false);
		extraPanel.add(scoreBoardInfo);

		JLabel gameTitle2 = new JLabel("단어 입력 게임");
		gameTitle2.setHorizontalAlignment(JLabel.CENTER);
		gameTitle2.setBounds(75, 50, 340, 50);
		gameTitle2.setFont(titleFont);
		extraPanel.add(gameTitle2);

		JButton btnRecord = new JButton("기록실");
		btnRecord.setFont(btnFont);
		btnRecord.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnRecord.setContentAreaFilled(false);
		btnRecord.setFocusPainted(false);
		btnRecord.setBounds(264, 280, 100, 100);
		btnRecord.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				fadeOut(gameTitle2, 200);
				scoreBoardInfo.setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				scoreBoardInfo.setVisible(false);
				gameTitle2.setForeground(Color.BLACK);
				gameTitle2.setVisible(true);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				int gap = 10; // 프레임 간격
				int x = mainFrame.getX() + mainFrame.getWidth() + gap; // 메인 프레임 오른쪽 + 간격
				int y = mainFrame.getY();
				ArrayList<RecordVO> list = recordDao.select();
				tableModel2.setRowCount(0);
				for (int i = 0; i < list.size(); i++) {
					records2 = new Object[] { list.get(i).getNickname(), list.get(i).getTotalWords(),
							list.get(i).getWordScore(), list.get(i).getSurviveScore(), list.get(i).getAccuracy() };
					tableModel2.addRow(records2);
				}
				recordFrame.setLocation(x, y);
				recordFrame.setVisible(true);
			}
		});
		extraPanel.add(btnRecord);

		// * 관리자만 볼 수 있는 버튼(단어 입력, 수정 및 삭제)
		JButton btnAdmin = new JButton("단어 에디터");
		btnAdmin.setFont(btnFont);
		btnAdmin.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnAdmin.setContentAreaFilled(false);
		btnAdmin.setFocusPainted(false);
		btnAdmin.setBounds(120, 400, buttonWidth, buttonHeight);
		btnAdmin.setVisible(false);
		myPagePanel.add(btnAdmin);

		// 단어 에디터 패널 구성
		ButtonGroup modeGroup = new ButtonGroup();

		JRadioButton editMode = new JRadioButton("편집모드");
		editMode.setFont(taskBarFont);
		editMode.setFocusPainted(false);
		editMode.setBounds(100, 5, 100, 30);
		modeGroup.add(editMode);
		wordEditorPanel.add(editMode);

		JRadioButton selectMode = new JRadioButton("검색모드");
		selectMode.setFont(taskBarFont);
		selectMode.setFocusPainted(false);
		selectMode.setBounds(250, 5, 100, 30);
		modeGroup.add(selectMode);
		wordEditorPanel.add(selectMode);

		btnAdmin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				myPagePanel.setVisible(false);
				wordEditorPanel.setVisible(true);

			}
		});

		JTextField wordInput = new JTextField("새로운 단어 입력");
		wordInput.setFont(btnFont);
		wordInput.setHorizontalAlignment(JLabel.CENTER);
		wordInput.setForeground(Color.GRAY);
		wordInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		wordInput.setEnabled(false);
		wordInput.setBounds(40, 40, 180, 70);
		wordInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (wordInput.getText().equals("새로운 단어 입력")) {
					wordInput.setText("");
					wordInput.setForeground(Color.black);
				} else if (!wordInput.getText().equals("")) {
					wordInput.setForeground(Color.black);
				}

			}

			@Override
			public void focusLost(FocusEvent e) {
				if (wordInput.getText().equals("")) {
					wordInput.setForeground(Color.gray);
					wordInput.setText("새로운 단어 입력");
				}
			}
		});

		wordInput.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!wordInput.isEnabled()) {
					JOptionPane.showMessageDialog(extraFrame, "먼저 모드를 선택해 주세요");
				}
			}
		});

		tableModel1 = new DefaultTableModel(colNames, 0);
		wordTable = new JTable(tableModel1);
		wordTable.setDefaultEditor(Object.class, null); // 편집 불가능하게
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		wordTable.setDefaultRenderer(Object.class, centerRenderer);
		TableColumn secondColumn = wordTable.getColumnModel().getColumn(1); // 두번째 칼럼(단어)를 가져온다
		secondColumn.setPreferredWidth(100); // 그 칼럼의 크기를 100으로 설정

		JScrollPane wordScrollPane = new JScrollPane(wordTable);
		wordScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		wordScrollPane.setBounds(40, 180, 360, 240);

		JTextArea wordInfoArea = new JTextArea("\n" + "         버튼에 마우스를 올려 설명을 보세요");
		wordInfoArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
		wordInfoArea.setBounds(40, 440, 360, 50);
		wordInfoArea.setEditable(false);
		wordInfoArea.setFont(btnFont);
		wordInfoArea.setForeground(Color.GRAY);
		wordEditorPanel.add(wordInfoArea);

		JButton btnWordSelect = new JButton("단어 검사");
		btnWordSelect.setFont(taskBarFont);
		btnWordSelect.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnWordSelect.setContentAreaFilled(false);
		btnWordSelect.setFocusPainted(false);
		btnWordSelect.setBounds(45, 130, 90, 30);

		JButton btnWordInsert = new JButton("등록");
		btnWordInsert.setFont(taskBarFont);
		btnWordInsert.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnWordInsert.setContentAreaFilled(false);
		btnWordInsert.setFocusPainted(false);
		btnWordInsert.setEnabled(false);
		btnWordInsert.setBounds(135, 130, 70, 30);

		JButton btnWordUpdate = new JButton("수정");
		btnWordUpdate.setFont(taskBarFont);
		btnWordUpdate.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnWordUpdate.setContentAreaFilled(false);
		btnWordUpdate.setFocusPainted(false);
		btnWordUpdate.setEnabled(false);
		btnWordUpdate.setBounds(205, 130, 70, 30);

		JButton btnWordDelete = new JButton("삭제");
		btnWordDelete.setFont(taskBarFont);
		btnWordDelete.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnWordDelete.setContentAreaFilled(false);
		btnWordDelete.setFocusPainted(false);
		btnWordDelete.setEnabled(false);
		btnWordDelete.setBounds(275, 130, 70, 30);

		JButton btnWordSelectByDiff = new JButton("난도별 검색");
		btnWordSelectByDiff.setFont(taskBarFont);
		btnWordSelectByDiff.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnWordSelectByDiff.setContentAreaFilled(false);
		btnWordSelectByDiff.setFocusPainted(false);
		btnWordSelectByDiff.setBounds(195, 130, 150, 30);
		btnWordSelectByDiff.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				wordInfoArea.setVisible(true);
				wordInfoArea.setText("\n" + "    검색한 난이도의 단어를 모두 보여줍니다");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				wordInfoArea.setText("\n" + "         버튼에 마우스를 올려 설명을 보세요");
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (wordInput.getText().equals("난이도 입력")) {
					JOptionPane.showMessageDialog(extraFrame, "숫자(난이도)로 입력해주세요");
				} else {
					int difficulty = Integer.parseInt(wordInput.getText());
					int count = wordDao.isExist(difficulty);
					if (count == 0) {// 해당 난이도의 단어가 없으면
						JOptionPane.showMessageDialog(extraFrame, "해당 난이도의 단어가 없습니다");
					} else {// 해당 난이도의 단어가 있으면
						tableModel1.setRowCount(0);
						ArrayList<WordVO> list = wordDao.select(difficulty);
						for (int i = 0; i < list.size(); i++) {
							records = new Object[] { list.get(i).getWordId(), list.get(i).getWord(),
									list.get(i).getDifficulty() };
							tableModel1.addRow(records);
						}
					}
				}
			}

		});

		JButton btnselectAll = new JButton("전체 검색");
		btnselectAll.setFont(taskBarFont);
		btnselectAll.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnselectAll.setContentAreaFilled(false);
		btnselectAll.setFocusPainted(false);
		btnselectAll.setBounds(45, 130, 150, 30);
		btnselectAll.setVisible(false);
		btnselectAll.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				wordInfoArea.setVisible(true);
				wordInfoArea.setText("\n" + "                    모든 단어를 검색합니다");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				wordInfoArea.setText("\n" + "         버튼에 마우스를 올려 설명을 보세요");
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				ArrayList<WordVO> list = wordDao.selectAll();
				tableModel1.setRowCount(0);
				for (int i = 0; i < list.size(); i++) {
					records = new Object[] { list.get(i).getWordId(), list.get(i).getWord(),
							list.get(i).getDifficulty() };
					tableModel1.addRow(records);
				}
			}
		});

		JTextField newWordInput = new JTextField("대체할 단어 입력");
		newWordInput.setFont(btnFont);
		newWordInput.setHorizontalAlignment(JLabel.CENTER);
		newWordInput.setForeground(Color.GRAY);
		newWordInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		newWordInput.setEnabled(false);
		newWordInput.setBounds(220, 40, 180, 70);
		newWordInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (newWordInput.getText().equals("대체할 단어 입력")) {
					newWordInput.setText("");
					newWordInput.setForeground(Color.black);
				} else if (!newWordInput.getText().equals("")) {
					newWordInput.setForeground(Color.black);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (newWordInput.getText().equals("")) {
					newWordInput.setForeground(Color.gray);
					newWordInput.setText("대체할 단어 입력");
				}
			}
		});

		newWordInput.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!newWordInput.isEnabled()) {
					JOptionPane.showMessageDialog(extraFrame, "먼저 모드를 선택해주세요");
				}
			}
		});
		wordEditorPanel.add(newWordInput);

		btnWordDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				wordInfoArea.setVisible(true);
				wordInfoArea.setText("\n" + "              해당 번호의 단어를 삭제합니다");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				wordInfoArea.setText("\n" + "         버튼에 마우스를 올려 설명을 보세요");
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				String word = wordInput.getText();
				if (!btnWordDelete.isEnabled()) {
					JOptionPane.showMessageDialog(extraFrame, "먼저 단어를 검사해주세요");
				} else { // 버튼이 활성화되면
					System.out.println(word);
					records[0] = "위(↑)";
					records[1] = "단어는";
					records[2] = "삭제되었습니다";
					tableModel1.addRow(records);
					wordDao.delete(word);
					JOptionPane.showMessageDialog(extraFrame, "단어 삭제 완료");
					wordInput.setForeground(Color.GRAY);
					wordInput.setText("새로운 단어 입력");
					newWordInput.setText("대체할 단어 입력");
					btnWordUpdate.setEnabled(false);
					btnWordDelete.setEnabled(false);
				}
			}
		});

		btnWordSelect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				wordInfoArea.setText("\n" + "              단어가 DB에 있는지 확인합니다");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				wordInfoArea.setText("\n" + "         버튼에 마우스를 올려 설명을 보세요");
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				String word = wordInput.getText();
				if (word.equals("새로운 단어 입력")) {
					JOptionPane.showMessageDialog(extraFrame, "입력한 값이 없습니다");
				} else {
					word = wordInput.getText();
					int wordId = wordDao.findWordId(word); // result : 단어번호
					if (wordId == 0) { // 단어번호가 0이면 -> 단어가 없으면
						JOptionPane.showMessageDialog(extraFrame, "새로운 단어입니다");
						btnWordInsert.setEnabled(true);
					} else { // 단어가 있으면
						tableModel1.setRowCount(0);
						JOptionPane.showMessageDialog(extraFrame, "등록된 단어입니다");
						btnWordUpdate.setEnabled(true);
						btnWordDelete.setEnabled(true);
						records[0] = wordId;
						records[1] = word;
						records[2] = word.length();
						tableModel1.addRow(records);
						wordInput.setForeground(Color.GRAY);
						newWordInput.requestFocus();
					}
				}
			}
		});

		ActionListener enterWordSelect = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String word = wordInput.getText();
				if (word.equals("새로운 단어 입력")) {
					JOptionPane.showMessageDialog(extraFrame, "입력한 값이 없습니다");
				} else {
					word = wordInput.getText();
					int wordId = wordDao.findWordId(word); // result : 단어번호
					if (wordId == 0) { // 단어번호가 0이면 -> 단어가 없으면
						JOptionPane.showMessageDialog(extraFrame, "새로운 단어입니다");
						btnWordInsert.setEnabled(true);
					} else { // 단어가 있으면
						tableModel1.setRowCount(0);
						JOptionPane.showMessageDialog(extraFrame, "등록된 단어입니다");
						btnWordUpdate.setEnabled(true);
						btnWordDelete.setEnabled(true);
						records[0] = wordId;
						records[1] = word;
						records[2] = word.length();
						tableModel1.addRow(records);
						wordInput.setForeground(Color.GRAY);
						newWordInput.requestFocus();
					}
				}

			}
		};
		btnWordSelect.addActionListener(enterWordSelect);

		wordInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnWordSelect.doClick();
			}
		});

		btnWordUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				wordInfoArea.setText("\n" + "              해당 번호의 단어를 수정합니다");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				wordInfoArea.setText("\n" + "         버튼에 마우스를 올려 설명을 보세요");
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!btnWordUpdate.isEnabled()) {
					JOptionPane.showMessageDialog(extraFrame, "먼저 단어를 등록해주세요");
				} else { // 수정 버튼이 활성화되어있으면
					String word = wordInput.getText();
					int wordId = wordDao.findWordId(word);
					String newWord = newWordInput.getText();
					if (newWord.equals("대체할 단어 입력")) { // 아무것도 입력되지 않았으면
						JOptionPane.showMessageDialog(extraFrame, "입력한 값이 없습니다. 다시 단어를 검색하세요");
						btnWordInsert.setEnabled(false);
						btnWordUpdate.setEnabled(false);
						btnWordDelete.setEnabled(false);
					} else {
						int result = wordDao.findWordId(newWord);
						if (result == 0) { // 기존 단어에 없다면(수정 가능)
							int difficulty = newWord.length();
							WordVO vo = new WordVO(wordId, newWord, difficulty);
							wordDao.update(vo);
							records[0] = "아래의";
							records[1] = "단어로(↓)";
							records[2] = "변경됩니다";
							tableModel1.addRow(records);
							records[0] = wordId;
							records[1] = newWord;
							records[2] = difficulty;
							tableModel1.addRow(records);
							JOptionPane.showMessageDialog(extraFrame, "단어 수정 완료");
							btnWordInsert.setEnabled(false);
							btnWordUpdate.setEnabled(false);
							btnWordDelete.setEnabled(false);
							wordInput.setForeground(Color.GRAY);
							newWordInput.setForeground(Color.GRAY);
							newWordInput.setText("대체할 단어 입력");
							wordInput.setText("새로운 단어 입력");
							wordInput.requestFocus();
							wordInfoArea.setText("\n" + "              해당 번호의 단어를 수정합니다");
						} else {
							JOptionPane.showMessageDialog(extraFrame, "이미 DB에 있는 단어입니다. 다른 새로운 단어를 입력하세요");
							btnWordInsert.setEnabled(false);
						}
					}

				}
			}

		});

		ActionListener enterWordUpdate = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!btnWordUpdate.isEnabled()) {
					JOptionPane.showMessageDialog(extraFrame, "먼저 단어를 등록해주세요");
				} else { // 수정 버튼이 활성화되어있으면
					String word = wordInput.getText();
					int wordId = wordDao.findWordId(word);
					String newWord = newWordInput.getText();
					if (newWord.equals("대체할 단어 입력")) { // 아무것도 입력되지 않았으면
						JOptionPane.showMessageDialog(extraFrame, "입력한 값이 없습니다. 다시 단어를 검색하세요");
						btnWordInsert.setEnabled(false);
						btnWordUpdate.setEnabled(false);
						btnWordDelete.setEnabled(false);
					} else {
						int result = wordDao.findWordId(newWord);
						if (result == 0) { // 기존 단어에 없다면(수정 가능)
							int difficulty = newWord.length();
							WordVO vo = new WordVO(wordId, newWord, difficulty);
							wordDao.update(vo);
							records[0] = "아래의";
							records[1] = "단어로(↓)";
							records[2] = "변경됩니다";
							tableModel1.addRow(records);
							records[0] = wordId;
							records[1] = newWord;
							records[2] = difficulty;
							tableModel1.addRow(records);
							JOptionPane.showMessageDialog(extraFrame, "단어 수정 완료");
							btnWordInsert.setEnabled(false);
							btnWordUpdate.setEnabled(false);
							btnWordDelete.setEnabled(false);
							wordInput.setForeground(Color.GRAY);
							newWordInput.setForeground(Color.GRAY);
							newWordInput.setText("대체할 단어 입력");
							wordInput.setText("새로운 단어 입력");
							wordInput.requestFocus();
							wordInfoArea.setText("\n" + "              해당 번호의 단어를 수정합니다");
						} else {
							JOptionPane.showMessageDialog(extraFrame, "이미 DB에 있는 단어입니다. 다른 새로운 단어를 입력하세요");
							btnWordInsert.setEnabled(false);
						}
					}

				}

			}
		};
		btnWordUpdate.addActionListener(enterWordUpdate);

		newWordInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnWordUpdate.doClick();
			}
		});

		JButton btnWordEsc = new JButton("종료");
		btnWordEsc.setFont(taskBarFont);
		btnWordEsc.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnWordEsc.setContentAreaFilled(false);
		btnWordEsc.setFocusPainted(false);
		btnWordEsc.setBounds(345, 130, 50, 30);
		btnWordEsc.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				wordInfoArea.setText("\n" + "                    단어 에디터를 종료합니다");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				wordInfoArea.setText("\n" + "         버튼에 마우스를 올려 설명을 보세요");
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				tableModel1.setRowCount(0);
				wordEditorPanel.setVisible(false);
				extraFrame.setVisible(false);
				wordInput.setForeground(Color.GRAY);
				wordInput.setText("새로운 단어 입력");
				btnWordInsert.setEnabled(false);
				btnWordUpdate.setEnabled(false);
				btnWordDelete.setEnabled(false);
			}
		});

		btnWordInsert.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				wordInfoArea.setText("\n" + "                          단어를 등록합니다");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				wordInfoArea.setText("\n" + "         버튼에 마우스를 올려 설명을 보세요");
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!btnWordInsert.isEnabled()) {
					JOptionPane.showMessageDialog(extraFrame, "먼저 단어를 검사해주세요");
				} else { // 버튼이 활성화되어있으면
					String word = wordInput.getText();
					WordVO vo = new WordVO(word);
					wordDao.insert(vo);
					if (word.equals("새로운 단어 입력")) {
						JOptionPane.showMessageDialog(extraFrame, "입력한 값이 없습니다");
					} else {
						JOptionPane.showMessageDialog(extraFrame, "DB에 저장되었습니다");
						records[0] = "아래의";
						records[1] = "단어가(↓)";
						records[2] = "등록되었습니다";
						tableModel1.addRow(records);
						records[0] = wordDao.findWordId(word);
						records[1] = word;
						records[2] = word.length();
						tableModel1.addRow(records);

						wordInput.setForeground(Color.GRAY);
						btnWordInsert.setEnabled(false);
						btnWordUpdate.setEnabled(true);
						btnWordDelete.setEnabled(true);
						newWordInput.requestFocus();
					}

				}
			}
		});

		btnWordSelect.setVisible(false);
		btnWordInsert.setVisible(false);
		btnWordUpdate.setVisible(false);
		btnWordDelete.setVisible(false);
		btnWordSelectByDiff.setVisible(false);
		wordEditorPanel.add(btnWordSelect);
		wordEditorPanel.add(wordInput);
		wordEditorPanel.add(btnWordInsert);
		wordEditorPanel.add(btnWordSelectByDiff);
		wordEditorPanel.add(btnselectAll);
		wordEditorPanel.add(btnWordUpdate);
		wordEditorPanel.add(btnWordDelete);
		wordEditorPanel.add(btnWordEsc);
		wordEditorPanel.add(wordScrollPane);

		editMode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				wordInput.setEnabled(true);
				newWordInput.setEnabled(true);
				wordInput.setForeground(Color.GRAY);
				wordInput.setText("새로운 단어 입력");
				wordInput.addFocusListener(new FocusListener() {

					@Override
					public void focusGained(FocusEvent e) {
						if (wordInput.getText().equals("새로운 단어 입력")) {
							wordInput.setText("");
							wordInput.setForeground(Color.black);
						} else if (!wordInput.getText().equals("")) {
							wordInput.setForeground(Color.black);
						}
					}

					@Override
					public void focusLost(FocusEvent e) {
						if (wordInput.getText().equals("")) {
							wordInput.setForeground(Color.gray);
							wordInput.setText("새로운 단어 입력");
						}
					}
				});
				btnWordSelect.setVisible(true);
				btnWordInsert.setVisible(true);
				btnWordUpdate.setVisible(true);
				btnWordDelete.setVisible(true);
				wordInput.setBounds(40, 40, 180, 70);
				newWordInput.setVisible(true);
				btnWordSelectByDiff.setVisible(false);
				btnselectAll.setVisible(false);
			}
		});
		selectMode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				wordInput.setEnabled(true);
				wordInput.setForeground(Color.GRAY);
				wordInput.setText("난이도 입력");
				wordInput.addFocusListener(new FocusListener() {

					@Override
					public void focusGained(FocusEvent e) {
						if (wordInput.getText().equals("난이도 입력")) {
							wordInput.setText("");
							wordInput.setForeground(Color.black);
						} else if (!wordInput.getText().equals("")) {
							wordInput.setForeground(Color.black);
						}
					}

					@Override
					public void focusLost(FocusEvent e) {
						if (wordInput.getText().equals("")) {
							wordInput.setForeground(Color.gray);
							wordInput.setText("난이도 입력");
						}
					}
				});
				btnWordSelect.setVisible(false);
				btnWordInsert.setVisible(false);
				btnWordUpdate.setVisible(false);
				btnWordDelete.setVisible(false);
				newWordInput.setVisible(false);
				wordInput.setBounds(40, 40, 360, 70);
				btnWordSelectByDiff.setVisible(true);
				btnselectAll.setVisible(true);
			}
		});

		JButton btnMyPage = new JButton("MyPage");
		btnMyPage.setFont(btnFont);
		btnMyPage.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnMyPage.setContentAreaFilled(false);
		btnMyPage.setFocusPainted(false);
		btnMyPage.setBounds(124, 280, 100, 100);

		extraPanel.add(btnMyPage);

		JButton goToTitle = new JButton();
		goToTitle.setBounds(180, 127, 110, 133);
		goToTitle.setContentAreaFilled(false);
		goToTitle.setBorderPainted(false);
		goToTitle.setFocusPainted(false);
		goToTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				fadeOut(gameTitle2, 200);
				backToTitle.setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				backToTitle.setVisible(false);
				gameTitle2.setForeground(Color.BLACK);
				gameTitle2.setVisible(true);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				extraPanel.setVisible(false);
				titlePanel.setVisible(true);
			}
		});
		extraPanel.add(goToTitle);

		// 3) 게임패널 구성
		int textFieldWidth = 250;
		int textFieldHeight = 40;

		// 마이페이지 패널 구성
		JLabel myPageTitle = new JLabel("MyPage");
		myPageTitle.setHorizontalAlignment(JLabel.CENTER);
		myPageTitle.setBounds(95, 70, 250, 50);
		myPageTitle.setFont(titleFont);
		myPagePanel.add(myPageTitle);

		JButton btnInsert = new JButton("회원 가입");
		btnInsert.setFont(btnFont);
		btnInsert.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnInsert.setContentAreaFilled(false);
		btnInsert.setFocusPainted(false);
		btnInsert.setBounds(120, 160, buttonWidth, buttonHeight);
		btnInsert.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnInsert.isEnabled()) {
					myPagePanel.setVisible(false);
					insertPanel.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(extraFrame, "다른 계정을 만들려면, 먼저 로그아웃해주세요");
				}
			}
		});
		myPagePanel.add(btnInsert);

		JButton btnForgotIdPw = new JButton("ID/PW 찾기");
		btnForgotIdPw.setFont(btnFont);
		btnForgotIdPw.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnForgotIdPw.setContentAreaFilled(false);
		btnForgotIdPw.setFocusPainted(false);
		btnForgotIdPw.setBounds(120, 220, buttonWidth, buttonHeight);
		btnForgotIdPw.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnForgotIdPw.isEnabled()) {
					myPagePanel.setVisible(false);
					forgotPanel.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(extraFrame, "이미 로그인되었습니다");
				}
			}
		});
		myPagePanel.add(btnForgotIdPw);

		JButton btnUpdate = new JButton("내 정보 수정");
		btnUpdate.setFont(btnFont);
		btnUpdate.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnUpdate.setContentAreaFilled(false);
		btnUpdate.setFocusPainted(false);
		btnUpdate.setBounds(120, 280, buttonWidth, buttonHeight);

		myPagePanel.add(btnUpdate);

		JButton btnDelete = new JButton("회원 탈퇴");
		btnDelete.setFont(btnFont);
		btnDelete.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnDelete.setContentAreaFilled(false);
		btnDelete.setFocusPainted(false);
		btnDelete.setBounds(120, 340, buttonWidth, buttonHeight);
		btnDelete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!btnDelete.isEnabled()) {
					JOptionPane.showMessageDialog(extraFrame, "먼저 로그인해주세요");
				} else {
					myPagePanel.setVisible(false);
					deletePanel.setVisible(true);
				}
			}
		});
		myPagePanel.add(btnDelete);

		btnMyPage.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				fadeOut(gameTitle2, 200);
				myPageInfo.setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				myPageInfo.setVisible(false);
				gameTitle2.setForeground(Color.BLACK);
				gameTitle2.setVisible(true);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				String id = idInput.getText();
				char[] pw = pwInput.getPassword();
				String revealedPw = new String(pw);
				wordgame.member.MemberVO vo = new wordgame.member.MemberVO(id, revealedPw);
				String isAdmin = memberDao.adminCheck(vo);
				if (isLogIn == true && isAdmin.equals("admin")) { // 관리자가 로그인했으면
					btnAdmin.setVisible(true);
					btnInsert.setEnabled(!isLogIn);
					btnForgotIdPw.setEnabled(!isLogIn);
					btnUpdate.setEnabled(isLogIn);
					btnDelete.setEnabled(isLogIn);
				} else if (isLogIn == true && !isAdmin.equals("admin")) { // 일반 사용자가 로그인했으면
					btnAdmin.setVisible(false);
					btnInsert.setEnabled(!isLogIn);
					btnForgotIdPw.setEnabled(!isLogIn);
					btnUpdate.setEnabled(isLogIn);
					btnDelete.setEnabled(isLogIn);
				} else if (isLogIn == false) { // 비회원이면(로그인하지 않았으면)
					btnAdmin.setVisible(false);
					btnInsert.setEnabled(!isLogIn);
					btnForgotIdPw.setEnabled(!isLogIn);
					btnUpdate.setEnabled(isLogIn);
					btnDelete.setEnabled(isLogIn);
				}
				int gap = 10; // 프레임 간격
				int x = mainFrame.getX() - extraFrame.getWidth() - gap; // 메인 프레임 왼쪽 + 간격
				int y = mainFrame.getY();
				extraFrame.setLocation(x, y);
				myPagePanel.setVisible(true);
				extraFrame.setVisible(true);
			}
		});

		// 인서트 패널 구성
		JLabel insertTitle = new JLabel("회원 가입");
		insertTitle.setHorizontalAlignment(JLabel.CENTER);
		insertTitle.setBounds(95, 30, 250, 50);
		insertTitle.setFont(titleFont);
		insertPanel.add(insertTitle);

		JTextField insertIdInput = new JTextField("ID");
		insertIdInput.setFont(btnFont);
		insertIdInput.setForeground(Color.gray);
		insertIdInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		insertIdInput.setBounds(95, 125, textFieldWidth, textFieldHeight);
		insertIdInput.setFont(btnFont);
		insertIdInput.setColumns(10);
		insertIdInput.setHorizontalAlignment(JLabel.CENTER);
		insertIdInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (insertIdInput.getText().equals("ID")) {
					insertIdInput.setText("");
					insertIdInput.setForeground(Color.BLACK);
				} else if (!insertIdInput.getText().equals("")) {
					insertIdInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (insertIdInput.getText().equals("")) {
					insertIdInput.setForeground(Color.GRAY);
					insertIdInput.setText("ID");
				}
			}
		});
		insertPanel.add(insertIdInput);

		JPasswordField insertPwInput = new JPasswordField("Password");
		insertPwInput.setForeground(Color.gray);
		insertPwInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		insertPwInput.setEchoChar((char) 0);
		insertPanel.add(insertPwInput);
		insertPwInput.setColumns(10);
		insertPwInput.setBounds(95, 195, textFieldWidth, textFieldHeight);
		insertPwInput.setFont(btnFont);
		insertPwInput.setHorizontalAlignment(JLabel.CENTER);
		insertPwInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				char[] hiddenPw = insertPwInput.getPassword();
				String revealedPw = new String(hiddenPw);
				if (revealedPw.equals("Password")) {
					insertPwInput.setText("");
					insertPwInput.setForeground(Color.BLACK);
					insertPwInput.setEchoChar('●');
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (insertPwInput.getPassword().length == 0) {
					insertPwInput.setForeground(Color.GRAY);
					insertPwInput.setText("Password");
					insertPwInput.setEchoChar((char) 0);
				}
			}
		});

		JTextField insertNicknameInput = new JTextField("Nickname");
		insertNicknameInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		insertNicknameInput.setFont(btnFont);
		insertNicknameInput.setForeground(Color.gray);
		insertNicknameInput.setBounds(95, 265, textFieldWidth, textFieldHeight);
		insertNicknameInput.setFont(btnFont);
		insertNicknameInput.setColumns(10);
		insertNicknameInput.setHorizontalAlignment(JLabel.CENTER);
		insertNicknameInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (insertNicknameInput.getText().equals("Nickname")) {
					insertNicknameInput.setText("");
					insertNicknameInput.setForeground(Color.BLACK);
				} else if (!insertNicknameInput.getText().equals("")) {
					insertNicknameInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (insertNicknameInput.getText().equals("")) {
					insertNicknameInput.setForeground(Color.GRAY);
					insertNicknameInput.setText("Nickname");
				}
			}
		});
		insertPanel.add(insertNicknameInput);

		JTextField insertEmailInput = new JTextField("Email");
		insertEmailInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		insertEmailInput.setFont(btnFont);
		insertEmailInput.setForeground(Color.gray);
		insertEmailInput.setBounds(95, 335, textFieldWidth, textFieldHeight);
		insertEmailInput.setFont(btnFont);
		insertEmailInput.setColumns(10);
		insertEmailInput.setHorizontalAlignment(JLabel.CENTER);
		insertEmailInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (insertEmailInput.getText().equals("Email")) {
					insertEmailInput.setText("");
					insertEmailInput.setForeground(Color.BLACK);
				} else if (!insertEmailInput.getText().equals("")) {
					insertEmailInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (insertEmailInput.getText().equals("")) {
					insertEmailInput.setForeground(Color.GRAY);
					insertEmailInput.setText("Email");
				}
			}
		});
		insertPanel.add(insertEmailInput);

		JButton btnDupCheckId = new JButton("중복체크");
		btnDupCheckId.setFont(taskBarFont);
		btnDupCheckId.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnDupCheckId.setContentAreaFilled(false);
		btnDupCheckId.setFocusPainted(false);
		btnDupCheckId.setBounds(275, 165, 70, buttonHeight / 2);

		JButton btnDupCheckEmail = new JButton("중복체크");
		btnDupCheckEmail.setFont(taskBarFont);
		btnDupCheckEmail.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnDupCheckEmail.setContentAreaFilled(false);
		btnDupCheckEmail.setFocusPainted(false);
		btnDupCheckEmail.setEnabled(false);
		btnDupCheckEmail.setBounds(275, 375, 70, buttonHeight / 2);
		insertPanel.add(btnDupCheckEmail);

		btnDupCheckId.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String id = insertIdInput.getText();
				wordgame.member.MemberVO vo = new wordgame.member.MemberVO();
				vo.setMemberId(id);
				int result = memberDao.dupCheckId(vo);
				if (insertIdInput.getText().equals("ID")) {
					JOptionPane.showMessageDialog(extraFrame, "ID를 기입해주세요");
				} else if (result == 1) {
					JOptionPane.showMessageDialog(extraFrame, "중복된 ID가 있습니다");
					insertIdInput.setForeground(Color.GRAY);
					insertIdInput.setText("ID");
				} else {
					JOptionPane.showMessageDialog(extraFrame, "이 ID는 사용이 가능합니다");
					btnDupCheckEmail.setEnabled(true);
				}
			}
		});
		insertPanel.add(btnDupCheckId);

		JButton inBtnInsert = new JButton("등록");
		inBtnInsert.setFont(btnFont);
		inBtnInsert.setBorder(new BevelBorder(BevelBorder.RAISED));
		inBtnInsert.setContentAreaFilled(false);
		inBtnInsert.setFocusPainted(false);
		inBtnInsert.setEnabled(false);
		inBtnInsert.setBounds(80, 450, buttonWidth / 2, buttonHeight);
		inBtnInsert.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String id = insertIdInput.getText();
				char[] pw = insertPwInput.getPassword();
				String revealedPw = new String(pw);
				String nickname = insertNicknameInput.getText();
				String email = insertEmailInput.getText();
				wordgame.member.MemberVO vo = new wordgame.member.MemberVO(id, revealedPw, nickname, email);
				int result = memberDao.insert(vo);
				if (id.equals("ID") || revealedPw.equals("Password") || nickname.equals("Nickname")
						|| email.equals("Email")) {
					JOptionPane.showMessageDialog(extraFrame, "누락된 정보가 있습니다");
				} else if (result == 0) {
					JOptionPane.showMessageDialog(extraFrame, "ID/Email 중복을 확인하세요");
				} else {
					JOptionPane.showMessageDialog(extraFrame, "회원가입 완료");
					insertPanel.setVisible(false);
					myPagePanel.setVisible(true);
					extraFrame.setVisible(false);
					insertIdInput.setForeground(Color.GRAY);
					insertPwInput.setForeground(Color.GRAY);
					insertNicknameInput.setForeground(Color.GRAY);
					insertEmailInput.setForeground(Color.GRAY);
					insertPwInput.setEchoChar((char) 0);
					insertIdInput.setText("ID");
					insertPwInput.setText("Password");
					insertNicknameInput.setText("Nickname");
					insertEmailInput.setText("Email");
				}

			}
		});
		inBtnInsert.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(extraFrame, "ID/Email 중복을 확인하세요");
				super.mouseClicked(e);
			}
		});
		btnDupCheckEmail.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnDupCheckEmail.isEnabled()) {

					String email = insertEmailInput.getText();
					wordgame.member.MemberVO vo = new wordgame.member.MemberVO();
					vo.setEmail(email);
					int result = memberDao.dupCheckEmail(vo);
					if (insertEmailInput.getText().equals("Email")) {
						JOptionPane.showMessageDialog(extraFrame, "이메일을 기입해주세요");
					} else if (result == 1) {
						JOptionPane.showMessageDialog(extraFrame, "중복된 이메일이 있습니다");
						insertEmailInput.setForeground(Color.GRAY);
						insertEmailInput.setText("Email");
					} else {
						JOptionPane.showMessageDialog(extraFrame, "이 이메일은 사용이 가능합니다");
						inBtnInsert.setEnabled(true);
					}
				} else {
					JOptionPane.showMessageDialog(extraFrame, "먼저 ID 중복체크를 해 주세요");
				}
			}
		});
		insertPanel.add(inBtnInsert);

		JButton btnCancelInsert = new JButton("취소");
		btnCancelInsert.setFont(btnFont);
		btnCancelInsert.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnCancelInsert.setContentAreaFilled(false);
		btnCancelInsert.setFocusPainted(false);
		btnCancelInsert.setBounds(250, 450, buttonWidth / 2, buttonHeight);
		btnCancelInsert.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				insertPanel.setVisible(false);
				String id = insertIdInput.getText();
				char[] pw = insertPwInput.getPassword();
				String revealedPw = new String(pw);
				String nickname = insertNicknameInput.getText();
				String email = insertEmailInput.getText();
				if (!id.equals("ID") || !revealedPw.equals("Password") || !nickname.equals("Nickname")
						|| !email.equals("Email")) {
					insertIdInput.setForeground(Color.GRAY);
					insertPwInput.setForeground(Color.GRAY);
					insertNicknameInput.setForeground(Color.GRAY);
					insertEmailInput.setForeground(Color.GRAY);
					insertIdInput.setText("ID");
					insertPwInput.setEchoChar((char) 0);
					insertPwInput.setText("Password");
					insertNicknameInput.setText("Nickname");
					insertEmailInput.setText("Email");
					btnDupCheckEmail.setEnabled(false);
					inBtnInsert.setEnabled(false);

				}
				extraFrame.setVisible(false);
			}
		});
		insertPanel.add(btnCancelInsert);

		// 아이디/비밀번호 찾기 패널 구성
		JLabel forgotTitle = new JLabel("ID/PW 찾기");
		forgotTitle.setHorizontalAlignment(JLabel.CENTER);
		forgotTitle.setBounds(90, 70, 250, 50);
		forgotTitle.setFont(titleFont);
		forgotPanel.add(forgotTitle);

		JTextField forgotEmailInput = new JTextField("이메일을 입력해주세요");
		forgotEmailInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		forgotEmailInput.setFont(btnFont);
		forgotEmailInput.setForeground(Color.gray);
		forgotEmailInput.setBounds(25, 335, 380, textFieldHeight);
		forgotEmailInput.setFont(btnFont);
		forgotEmailInput.setColumns(10);
		forgotEmailInput.setHorizontalAlignment(JLabel.CENTER);
		forgotEmailInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (forgotEmailInput.getText().equals("이메일을 입력해주세요")) {
					forgotEmailInput.setText("");
					forgotEmailInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (forgotEmailInput.getText().equals("")) {
					forgotEmailInput.setForeground(Color.GRAY);
					forgotEmailInput.setText("이메일을 입력해주세요");
				}
			}
		});
		forgotPanel.add(forgotEmailInput);

		JScrollPane searchResultpane = new JScrollPane();
		searchResultpane.setBounds(25, 140, 380, 180);
		searchResultpane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		forgotPanel.add(searchResultpane);

		JTextArea searchResultField = new JTextArea("     이메일로 아이디와 비밀번호를 검색합니다");
		searchResultField.setFont(btnFont);
		searchResultField.setForeground(Color.GRAY);
		searchResultpane.setViewportView(searchResultField);
		searchResultField.setEditable(false);

		JButton btnSearchByEmail = new JButton("찾기");
		btnSearchByEmail.setFont(btnFont);
		btnSearchByEmail.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnSearchByEmail.setContentAreaFilled(false);
		btnSearchByEmail.setFocusPainted(false);
		btnSearchByEmail.setBounds(80, 450, buttonWidth / 2, buttonHeight);
		btnSearchByEmail.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String email = forgotEmailInput.getText();
				wordgame.member.MemberVO vo = memberDao.select(email);
				String id = vo.getMemberId();
				String pw = vo.getPw();
				int isAdmin = vo.getIsAdmin();
				if (id.equals("") && pw.equals("")) {
					JOptionPane.showMessageDialog(extraFrame, "등록된 Email이 아닙니다");
				} else if (isAdmin == 1) {
					JOptionPane.showMessageDialog(extraFrame, "관리자 회원정보입니다");
				} else {
					searchResultField
							.setText("\n" + "===== 해당 이메일로 등록된 회원정보 =====" + "\n" + "\n" + "I                  D: " + id
									+ "\n" + "Password: " + pw + "\n" + "\n" + "===============================");
				}
			}
		});
		forgotPanel.add(btnSearchByEmail);

		ActionListener enterSearchByEmail = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String email = forgotEmailInput.getText();
				wordgame.member.MemberVO vo = memberDao.select(email);
				String id = vo.getMemberId();
				String pw = vo.getPw();
				int isAdmin = vo.getIsAdmin();
				if (id.equals("") && pw.equals("")) {
					JOptionPane.showMessageDialog(extraFrame, "등록된 Email이 아닙니다");
				} else if (isAdmin == 1) {
					JOptionPane.showMessageDialog(extraFrame, "관리자 회원정보입니다");
				} else {
					searchResultField
							.setText("\n" + "===== 해당 이메일로 등록된 회원정보 =====" + "\n" + "\n" + "I                  D: " + id
									+ "\n" + "Password: " + pw + "\n" + "\n" + "===============================");
				}

			}
		};
		btnSearchByEmail.addActionListener(enterSearchByEmail);

		forgotEmailInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnSearchByEmail.doClick();

			}
		});

		JButton btnCancelforgot = new JButton("취소");
		btnCancelforgot.setFont(btnFont);
		btnCancelforgot.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnCancelforgot.setContentAreaFilled(false);
		btnCancelforgot.setFocusPainted(false);
		btnCancelforgot.setBounds(250, 450, buttonWidth / 2, buttonHeight);
		btnCancelforgot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				forgotPanel.setVisible(false);
				extraFrame.setVisible(false);
				forgotEmailInput.setForeground(Color.GRAY);
				searchResultField.setText("        이메일로 아이디와 비밀번호를 검색합니다");
				forgotEmailInput.setText("이메일을 입력해주세요");
				myPagePanel.setVisible(true);
			}
		});
		forgotPanel.add(btnCancelforgot);

		// 업데이트 패널 구성
		JLabel updateTitle = new JLabel("내 정보 수정");
		updateTitle.setHorizontalAlignment(JLabel.CENTER);
		updateTitle.setBounds(100, 70, 250, 50);
		updateTitle.setFont(titleFont);
		updatePanel.add(updateTitle);

		JLabel oldIdArea = new JLabel();
		oldIdArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
		oldIdArea.setOpaque(true);
		oldIdArea.setBackground(Color.WHITE); // 배경색 설정
		oldIdArea.setForeground(Color.GRAY); // 텍스트 색상 설정
		oldIdArea.setFont(btnFont);
		oldIdArea.setBounds(100, 165, textFieldWidth, textFieldHeight);
		oldIdArea.setFont(btnFont);
		oldIdArea.setHorizontalAlignment(JLabel.CENTER);
		btnUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnUpdate.isEnabled()) {
					JOptionPane.showMessageDialog(extraFrame, "정보 수정을 위해, ID와 비밀번호를 한번 더 확인합니다");
					myPagePanel.setVisible(false);
					oldIdArea.setText("ID: " + tempId);
					updatePanel.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(extraFrame, "먼저 로그인해주세요");
				}
			}
		});
		updatePanel.add(oldIdArea);

		JPasswordField updatePwInput = new JPasswordField("Password");
		updatePwInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		updatePwInput.setFont(btnFont);
		updatePwInput.setForeground(Color.gray);
		updatePwInput.setEchoChar((char) 0);
		updatePanel.add(updatePwInput);
		updatePwInput.setBounds(100, 255, textFieldWidth, textFieldHeight);
		updatePwInput.setFont(btnFont);
		updatePwInput.setColumns(10);
		updatePwInput.setHorizontalAlignment(JLabel.CENTER);
		updatePwInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				char[] hiddenPw = updatePwInput.getPassword();
				String revealedPw = new String(hiddenPw);
				if (revealedPw.equals("Password")) {
					updatePwInput.setText("");
					updatePwInput.setForeground(Color.BLACK);
					updatePwInput.setEchoChar('●');
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (updatePwInput.getPassword().length == 0) {
					updatePwInput.setForeground(Color.GRAY);
					updatePwInput.setText("Password");
					updatePwInput.setEchoChar((char) 0);
				}
			}
		});

		// 인-업데이트 패널 구성
		JLabel inUpdateTitle = new JLabel("수정할 정보 입력");
		inUpdateTitle.setHorizontalAlignment(JLabel.CENTER);
		inUpdateTitle.setBounds(80, 70, 280, 50);
		inUpdateTitle.setFont(titleFont);
		inUpdatePanel.add(inUpdateTitle);

		JPasswordField inUpdatePwInput = new JPasswordField("Password");
		inUpdatePwInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		inUpdatePwInput.setForeground(Color.gray);
		inUpdatePwInput.setEchoChar((char) 0);
		inUpdatePanel.add(inUpdatePwInput);
		inUpdatePwInput.setColumns(10);
		inUpdatePwInput.setBounds(100, 165, textFieldWidth, textFieldHeight);
		inUpdatePwInput.setFont(btnFont);
		inUpdatePwInput.setHorizontalAlignment(JLabel.CENTER);
		inUpdatePwInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				char[] hiddenPw = inUpdatePwInput.getPassword();
				String revealedPw = new String(hiddenPw);
				if (revealedPw.equals("Password")) {
					inUpdatePwInput.setText("");
					inUpdatePwInput.setForeground(Color.BLACK);
					inUpdatePwInput.setEchoChar('●');
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inUpdatePwInput.getPassword().length == 0) {
					inUpdatePwInput.setForeground(Color.GRAY);
					inUpdatePwInput.setText("Password");
					inUpdatePwInput.setEchoChar((char) 0);
				}
			}
		});

		JTextField inUpdateNicknameInput = new JTextField("Nickname");
		inUpdateNicknameInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		inUpdateNicknameInput.setFont(btnFont);
		inUpdateNicknameInput.setForeground(Color.gray);
		inUpdateNicknameInput.setBounds(100, 225, textFieldWidth, textFieldHeight);
		inUpdateNicknameInput.setFont(btnFont);
		inUpdateNicknameInput.setColumns(10);
		inUpdateNicknameInput.setHorizontalAlignment(JLabel.CENTER);
		inUpdateNicknameInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (inUpdateNicknameInput.getText().equals("Nickname")) {
					inUpdateNicknameInput.setText("");
					inUpdateNicknameInput.setForeground(Color.BLACK);
				} else if (!inUpdateNicknameInput.getText().equals("")) {
					inUpdateNicknameInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inUpdateNicknameInput.getText().equals("")) {
					inUpdateNicknameInput.setForeground(Color.GRAY);
					inUpdateNicknameInput.setText("Nickname");
				}
			}
		});
		inUpdatePanel.add(inUpdateNicknameInput);

		JTextField inUpdateEmailInput = new JTextField("Email");
		inUpdateEmailInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		inUpdateEmailInput.setFont(btnFont);
		inUpdateEmailInput.setForeground(Color.gray);
		inUpdateEmailInput.setBounds(100, 285, textFieldWidth, textFieldHeight);
		inUpdateEmailInput.setFont(btnFont);
		inUpdateEmailInput.setColumns(10);
		inUpdateEmailInput.setHorizontalAlignment(JLabel.CENTER);
		inUpdateEmailInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (inUpdateEmailInput.getText().equals("Email")) {
					inUpdateEmailInput.setText("");
					inUpdateEmailInput.setForeground(Color.BLACK);
				} else if (!inUpdateEmailInput.getText().equals("")) {
					inUpdateEmailInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (inUpdateEmailInput.getText().equals("")) {
					inUpdateEmailInput.setForeground(Color.GRAY);
					inUpdateEmailInput.setText("Email");
				}
			}
		});
		inUpdatePanel.add(inUpdateEmailInput);

		JButton inBtnUpdate = new JButton("수정");
		inBtnUpdate.setFont(btnFont);
		inBtnUpdate.setBorder(new BevelBorder(BevelBorder.RAISED));
		inBtnUpdate.setContentAreaFilled(false);
		inBtnUpdate.setFocusPainted(false);
		inBtnUpdate.setBounds(80, 450, buttonWidth / 2, buttonHeight);
		inBtnUpdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String id = tempId;
				char[] pw = inUpdatePwInput.getPassword();
				String revealedPw = new String(pw);
				String nickname = inUpdateNicknameInput.getText();
				String email = inUpdateEmailInput.getText();
				wordgame.member.MemberVO vo = new wordgame.member.MemberVO(id, revealedPw, nickname, email);
				int result = memberDao.update(vo);
				if (revealedPw.equals("Password") || nickname.equals("Nickname") || email.equals("Email")) {
					JOptionPane.showMessageDialog(extraFrame, "누락된 정보가 있습니다");
				} else if (result == 0) {
					JOptionPane.showMessageDialog(extraFrame, "잘못된 ID/PW입니다");
				} else if (result == 2) {
					JOptionPane.showMessageDialog(extraFrame, "등록된 이메일입니다");
				} else {
					JOptionPane.showMessageDialog(extraFrame, "정보 수정 완료");
					oldIdArea.setForeground(Color.GRAY);
					inUpdatePwInput.setForeground(Color.GRAY);
					inUpdateNicknameInput.setForeground(Color.GRAY);
					inUpdateEmailInput.setForeground(Color.GRAY);
					inUpdatePwInput.setText("Password");
					inUpdatePwInput.setEchoChar((char) 0);
					inUpdateNicknameInput.setText("Nickname");
					inUpdateEmailInput.setText("Email");
					extraFrame.setVisible(false);
				}
			}
		});
		inUpdatePanel.add(inBtnUpdate);

		ActionListener enterInUpdate = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String id = tempId;
				char[] pw = inUpdatePwInput.getPassword();
				String revealedPw = new String(pw);
				String nickname = inUpdateNicknameInput.getText();
				String email = inUpdateEmailInput.getText();
				wordgame.member.MemberVO vo = new wordgame.member.MemberVO(id, revealedPw, nickname, email);
				int result = memberDao.update(vo);
				if (revealedPw.equals("Password") || nickname.equals("Nickname") || email.equals("Email")) {
					JOptionPane.showMessageDialog(extraFrame, "누락된 정보가 있습니다");
				} else if (result == 0) {
					JOptionPane.showMessageDialog(extraFrame, "잘못된 ID/PW입니다");
				} else if (result == 2) {
					JOptionPane.showMessageDialog(extraFrame, "등록된 이메일입니다");
				} else {
					JOptionPane.showMessageDialog(extraFrame, "정보 수정 완료");
					oldIdArea.setForeground(Color.GRAY);
					inUpdatePwInput.setForeground(Color.GRAY);
					inUpdateNicknameInput.setForeground(Color.GRAY);
					inUpdateEmailInput.setForeground(Color.GRAY);
					inUpdatePwInput.setText("Password");
					inUpdatePwInput.setEchoChar((char) 0);
					inUpdateNicknameInput.setText("Nickname");
					inUpdateEmailInput.setText("Email");
				}
			}
		};
		inBtnUpdate.addActionListener(enterInUpdate);

		inUpdateEmailInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				inBtnUpdate.doClick();
			}
		});

		JButton inBtnCancelUpdate = new JButton("취소");
		inBtnCancelUpdate.setFont(btnFont);
		inBtnCancelUpdate.setBorder(new BevelBorder(BevelBorder.RAISED));
		inBtnCancelUpdate.setContentAreaFilled(false);
		inBtnCancelUpdate.setFocusPainted(false);
		inBtnCancelUpdate.setBounds(250, 450, buttonWidth / 2, buttonHeight);
		deletePanel.add(inBtnCancelUpdate);
		inBtnCancelUpdate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				char[] pw = updatePwInput.getPassword();
				String revealedPw = new String(pw);
				String nickname = inUpdateNicknameInput.getText();
				String email = inUpdateEmailInput.getText();
				if (!revealedPw.equals("Password") || !nickname.equals("Nickname") || !email.equals("Email")) {
					// 기본 기입값을 건드렸으면
					oldIdArea.setForeground(Color.GRAY);
					inUpdatePwInput.setForeground(Color.GRAY);
					inUpdateNicknameInput.setForeground(Color.GRAY);
					inUpdateEmailInput.setForeground(Color.GRAY);
					updatePwInput.setForeground(Color.GRAY);
					updatePwInput.setEchoChar((char) 0);
					updatePwInput.setText("Password");
					inUpdatePwInput.setEchoChar((char) 0);
					inUpdatePwInput.setText("Password");
					inUpdateNicknameInput.setText("Nickname");
					inUpdateEmailInput.setText("Email");
				}
				inUpdatePanel.setVisible(false);
				updatePanel.setVisible(true);
			}
		});
		inUpdatePanel.add(inBtnCancelUpdate);

		// 삭제 패널 구성
		JLabel deleteTitle = new JLabel("탈퇴하기");
		deleteTitle.setHorizontalAlignment(JLabel.CENTER);
		deleteTitle.setBounds(95, 70, 250, 50);
		deleteTitle.setFont(titleFont);
		deletePanel.add(deleteTitle);

		JScrollPane deleteWarningPane = new JScrollPane();
		deleteWarningPane.setBounds(25, 140, 380, 180);
		deleteWarningPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		deletePanel.add(deleteWarningPane);

		JTextArea deleteWarning = new JTextArea("\n" + "     이 결정은 되돌릴 수 없습니다." + "\n" + "\n" + "     정말 탈퇴할까요? :(");
		deleteWarning.setFont(btnFont);
		deleteWarning.setForeground(Color.GRAY);
		deleteWarningPane.setViewportView(deleteWarning);
		deleteWarning.setEditable(false);

		JButton inBtnDelete = new JButton("삭제");
		inBtnDelete.setBounds(80, 450, buttonWidth / 2, buttonHeight);
		inBtnDelete.setFont(btnFont);
		inBtnDelete.setBorder(new BevelBorder(BevelBorder.RAISED));
		inBtnDelete.setContentAreaFilled(false);
		inBtnDelete.setFocusPainted(false);
		inBtnDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isLogIn == false) {
					JOptionPane.showMessageDialog(extraFrame, "먼저 로그인을 해주세요");
				} else {
					String id = idInput.getText();
					char[] pw = pwInput.getPassword();
					String revealedPw = new String(pw);
					memberDao.delete(id, revealedPw);
					isLogIn = false;
					JOptionPane.showMessageDialog(extraFrame, "삭제되었습니다");
					JOptionPane.showMessageDialog(extraFrame, "로그아웃되었습니다");
					deletePanel.setVisible(false);
					myPagePanel.setVisible(true);
					extraFrame.setVisible(false);
					taskBarPanel.setBorder(null);
					idInput.setText("ID");
					pwInput.setText("Password");
					pwInput.setEchoChar((char) 0);
					idInput.setForeground(Color.GRAY);
					pwInput.setForeground(Color.GRAY);
					taskBarPanel.remove(time);
					taskBarPanel.remove(logOutMsg);
					taskBarPanel.add(idInput);
					taskBarPanel.add(pwInput);
					taskBarPanel.add(btnLogIn);
					taskBarPanel.add(logInMsg);
					btnLogIn.setBorder(new BevelBorder(BevelBorder.RAISED));
				}
			}
		});
		deletePanel.add(inBtnDelete);

		JButton btnCancelDelete = new JButton("취소");
		btnCancelDelete.setFont(btnFont);
		btnCancelDelete.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnCancelDelete.setContentAreaFilled(false);
		btnCancelDelete.setFocusPainted(false);
		btnCancelDelete.setBounds(250, 450, buttonWidth / 2, buttonHeight);
		deletePanel.add(btnCancelDelete);
		btnCancelDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deletePanel.setVisible(false);
				extraFrame.setVisible(false);
			}
		});

		JButton inBtnUpdateVerify = new JButton("확인");
		inBtnUpdateVerify.setFont(btnFont);
		inBtnUpdateVerify.setBorder(new BevelBorder(BevelBorder.RAISED));
		inBtnUpdateVerify.setContentAreaFilled(false);
		inBtnUpdateVerify.setFocusPainted(false);
		inBtnUpdateVerify.setBounds(80, 450, buttonWidth / 2, buttonHeight);
		inBtnUpdateVerify.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				String oldId = idInput.getText();
				System.out.println("oldid: " + oldId);
				char[] oldPw = pwInput.getPassword();
				String revealedOldPw = new String(oldPw);
				System.out.println("oldPw: " + revealedOldPw);
				char[] pw = updatePwInput.getPassword();
				String revealedPw = new String(pw);
				System.out.println("newPw: " + revealedPw);
				if (revealedPw.equals("Password")) { // 아무것도 기입하지 않았으면
					JOptionPane.showMessageDialog(extraFrame, "PW를 입력해주세요");
				} else if (!revealedOldPw.equals(revealedPw)) { // 기입한 패스워드가 다르면
					JOptionPane.showMessageDialog(extraFrame, "잘못된 PW입니다");
				} else {
					String nickname = memberDao.login(oldId, revealedPw);
					JOptionPane.showMessageDialog(extraFrame, nickname + "님의 정보를 수정합니다");
					updatePanel.setVisible(false);
					inUpdatePanel.setVisible(true);
					oldIdArea.setForeground(Color.GRAY);
					inUpdatePwInput.setForeground(Color.GRAY);
				}

			}
		});
		updatePanel.add(inBtnUpdateVerify);

		ActionListener enterInUpdateVerify = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String oldId = idInput.getText();
				System.out.println("oldid: " + oldId);
				char[] oldPw = pwInput.getPassword();
				String revealedOldPw = new String(oldPw);
				System.out.println("oldPw: " + revealedOldPw);
				char[] pw = updatePwInput.getPassword();
				String revealedPw = new String(pw);
				System.out.println("newPw: " + revealedPw);
				if (revealedPw.equals("Password")) { // 아무것도 기입하지 않았으면
					JOptionPane.showMessageDialog(extraFrame, "PW를 입력해주세요");
				} else if (!revealedOldPw.equals(revealedPw)) { // 기입한 패스워드가 다르면
					JOptionPane.showMessageDialog(extraFrame, "잘못된 PW입니다");
				} else {
					String nickname = memberDao.login(oldId, revealedPw);
					JOptionPane.showMessageDialog(extraFrame, nickname + "님의 정보를 수정합니다");
					updatePanel.setVisible(false);
					inUpdatePanel.setVisible(true);
					oldIdArea.setForeground(Color.GRAY);
					inUpdatePwInput.setForeground(Color.GRAY);
				}
			}
		};
		inBtnUpdateVerify.addActionListener(enterInUpdateVerify);

		updatePwInput.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				inBtnUpdateVerify.doClick();
			}
		});

		JButton btnCancelUpdate = new JButton("취소");
		btnCancelUpdate.setFont(btnFont);
		btnCancelUpdate.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnCancelUpdate.setContentAreaFilled(false);
		btnCancelUpdate.setFocusPainted(false);
		btnCancelUpdate.setBounds(250, 450, buttonWidth / 2, buttonHeight);
		deletePanel.add(btnCancelUpdate);
		btnCancelUpdate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updatePanel.setVisible(false);
				extraFrame.setVisible(false);
				String id = oldIdArea.getText();
				char[] pw = updatePwInput.getPassword();
				String revealedPw = new String(pw);
				if (!id.equals("ID") || !revealedPw.equals("Password")) {
					oldIdArea.setForeground(Color.GRAY);
					updatePwInput.setForeground(Color.GRAY);
					updatePwInput.setEchoChar((char) 0);
					updatePwInput.setText("Password");
				}
			}
		});
		updatePanel.add(btnCancelUpdate);

		// 게임패널 구성
		gameTextInput = new JTextField("ENTER HERE");
		gameTextInput.setForeground(Color.gray);
		gameTextInput.setBounds(120, 340, textFieldWidth, textFieldHeight);
		gameTextInput.setFont(btnFont);
		gameTextInput.setBorder(new BevelBorder(BevelBorder.LOWERED));
		gameTextInput.setColumns(10);
		gameTextInput.setHorizontalAlignment(JLabel.CENTER);
		gameTextInput.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (gameTextInput.getText().equals("ENTER HERE")) {
					gameTextInput.setText("");
					gameTextInput.setForeground(Color.BLACK);
				} else if (!gameTextInput.getText().equals("")) {
					gameTextInput.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (gameTextInput.getText().equals("")) {
					gameTextInput.setText("ENTER HERE");
				}
			}
		});
		gamePanel.add(gameTextInput);

		displayWord = new JLabel();
		displayWord.setBorder(new BevelBorder(BevelBorder.LOWERED));
		displayWord.setOpaque(true);
		displayWord.setHorizontalAlignment(JLabel.CENTER);
		displayWord.setBackground(Color.WHITE);
		displayWord.setFont(titleFont);
		displayWord.setBounds(45, 100, 400, 100);
		gamePanel.add(displayWord);

		btnGameStart.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				gamePanel.add(btnAfterGame);
				mainFrame.add(gamePanel);
				btnAfterGame.setEnabled(false);
				btnGameStart.setBorder(new BevelBorder(BevelBorder.LOWERED));
				if (isLogIn == true) { // 로그인되어 있으면
					btnGameStart.setBorder(new BevelBorder(BevelBorder.RAISED));
					titlePanel.setVisible(false);
					missedWord.setVisible(true);
					skippedWord.setVisible(true);
					passedWord.setVisible(true);
					displayWord.setVisible(true);
					currentWord.setVisible(true);
					gameTextInput.setEnabled(true);
					gameTextInput.setVisible(true);
					gamePanel.setVisible(true);
					scorePanel.setVisible(true);
					gameTextInput.requestFocusInWindow();
					startGame();
				} else { // 안 되어 있으면
					logInMsg.setForeground(new Color(255, 102, 0));
					logInMsg.setText("비회원입니다");
					JOptionPane.showMessageDialog(mainFrame, "비회원은 점수가 기록되지 않습니다", "경고",
							JOptionPane.INFORMATION_MESSAGE);
					btnGameStart.setBorder(new BevelBorder(BevelBorder.RAISED));
					btnLogIn.setEnabled(isLogIn);
					titlePanel.setVisible(false);
					missedWord.setVisible(true);
					skippedWord.setVisible(true);
					passedWord.setVisible(true);
					displayWord.setVisible(true);
					currentWord.setVisible(true);
					gameTextInput.setEnabled(true);
					gameTextInput.setVisible(true);
					gamePanel.setVisible(true);
					scorePanel.setVisible(true);
					idInput.setEnabled(false);
					pwInput.setEnabled(false);
					startGame();
				}
			}
		});

		// 게임 점수 패널 구성
		JLabel scoreMsg = new JLabel("단어 점수");
		scoreMsg.setFont(taskBarFont);
		scoreMsg.setHorizontalAlignment(JLabel.CENTER);
		scoreMsg.setBorder(new BevelBorder(BevelBorder.RAISED));
		scorePanel.add(scoreMsg);

		JLabel scoreMsg2 = new JLabel(wordScore + "  점  ");
		scoreMsg2.setFont(taskBarFont);
		scoreMsg2.setHorizontalAlignment(JLabel.RIGHT);
		scoreMsg2.setBorder(new BevelBorder(BevelBorder.RAISED));
		scorePanel.add(scoreMsg2);

		JLabel scoreMsg3 = new JLabel("시간 점수");
		scoreMsg3.setFont(taskBarFont);
		scoreMsg3.setHorizontalAlignment(JLabel.CENTER);
		scoreMsg3.setBorder(new BevelBorder(BevelBorder.RAISED));
		scorePanel.add(scoreMsg3);

		scoreMsg4 = new JLabel("  점  ");
		scoreMsg4.setFont(taskBarFont);
		scoreMsg4.setHorizontalAlignment(JLabel.RIGHT);
		scoreMsg4.setBorder(new BevelBorder(BevelBorder.RAISED));
		scorePanel.add(scoreMsg4);

		currentWord = new JLabel("No.  " + currentWordIndex);
		currentWord.setForeground(Color.GRAY);
		currentWord.setFont(taskBarFont);
		currentWord.setBounds(60, 70, 100, 30);
		gamePanel.add(currentWord);

		passedWord = new JLabel();
		passedWord.setForeground(Color.BLUE);
		passedWord.setHorizontalAlignment(JLabel.CENTER);
		passedWord.setFont(taskBarFont);
		passedWord.setBounds(120, 300, textFieldWidth, 30);
		gamePanel.add(passedWord);

		missedWord = new JLabel("  틀린 횟수  " + stackedMissedWords + "  번");
		missedWord.setFont(taskBarFont);
		missedWord.setBounds(0, 30, 100, 30);
		gamePanel.add(missedWord);

		skippedWord = new JLabel("지나간 단어  " + stackedSkippedWords + "  개  ");
		skippedWord.setFont(taskBarFont);
		skippedWord.setHorizontalAlignment(JLabel.RIGHT);
		skippedWord.setBounds(334, 30, 150, 30);
		gamePanel.add(skippedWord);

		gameTextInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String answer = gameTextInput.getText();
				WordVO currentWord = wordsList.get(currentWordIndex);
				if (answer.equals(currentWord.getWord())) { // 만약 현재 표시된 단어와 입력한 단어가 같으면
					System.out.println("정답입니다!");
					wordScore += answer.length(); // 점수 계산(증가시키기)
					scoreMsg2.setText(wordScore + "  점  ");
					stackedPassedWords++;
					if (stackedPassedWords % 10 == 0) {
						passedWord.setText("  정답  " + stackedPassedWords + "  개 돌파!");
						fadeOut(passedWord, 1000);
					}
					System.out.println("패스 단어: " + stackedPassedWords);
					changeScoreColor(scoreMsg2, Color.GREEN);
					Timer nextWordTimer = new Timer(500, new ActionListener() { // 0.5초 후에 다음 단어 넘어가기
						@Override
						public void actionPerformed(ActionEvent e) {
							currentWordIndex++; // 다음 단어로 이동
							GameMain.this.currentWord.setText("No.  " + currentWordIndex);
							nextWord(displayWord); // 다음 단어로 넘어가기
						}
					});
					nextWordTimer.setRepeats(false); // 한 번만 실행되도록 설정
					nextWordTimer.start(); // 타이머 시작

					// 타이머 정지
					if (wordTimer != null && wordTimer.isRunning()) { // isRunning() 메소드는 timer가 동작하고 있는지 확인
						wordTimer.stop();
					}
				} else {
					System.out.println("오답입니다.");
					wordScore -= answer.length();
					scoreMsg2.setText(wordScore + "  점  ");
					changeScoreColor(scoreMsg2, Color.RED);
					stackedMissedWords++; // 오답 쌓기
					missedWord.setText("  틀린 횟수  " + stackedMissedWords + "  번");
				}
				GameMain.this.currentWord.setText("No.  " + currentWordIndex);
				gameTextInput.setText(""); // 입력 필드 비우기
			}
		});

		btnAfterGame = new JButton();
		btnAfterGame.setContentAreaFilled(false);
		btnAfterGame.setBorderPainted(false);
		btnAfterGame.setFocusPainted(false);
		btnAfterGame.setEnabled(false);
		btnAfterGame.setBounds(45, 100, 400, 100);
		String originalWord = displayWord.getText();
		btnAfterGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (btnAfterGame.isEnabled()) {
					displayWord.setFont(new Font("나눔바른고딕", Font.BOLD, 30));
					displayWord.setText("클릭하면 타이틀로 돌아갑니다");
				}

			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (btnAfterGame.isEnabled()) {
					displayWord.setFont(new Font("나눔바른고딕", Font.BOLD, 40));
					displayWord.setText(originalWord);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnAfterGame.isEnabled()) {
					gamePanel.remove(btnAfterGame);
					wordScore = 0;
					scoreMsg2.setText(wordScore + "  점  ");
					scorePanel.setVisible(false);
					stackedMissedWords = 0;
					missedWord.setText("  틀린 횟수  " + stackedMissedWords + "  번");
					missedWord.setVisible(false);
					stackedSkippedWords = 0;
					skippedWord.setText("지나간 단어  " + stackedSkippedWords + "  개  ");
					skippedWord.setVisible(false);
					stackedPassedWords = 0;
					displayWord.setFont(new Font("나눔바른고딕", Font.BOLD, 40));
					displayWord.setText("");
					displayWord.setVisible(false);
					gameTextInput.setText("ENTER HERE");
					gameTextInput.setVisible(false);
					currentWordIndex = 1;
					currentWord.setText("No.  " + currentWordIndex);
					currentWord.setVisible(false);
					scoreMsg4.setText("  점  ");
					logInMsg.setForeground(Color.BLACK);
					logInMsg.setText("환영합니다!");
					idInput.setEnabled(true);
					pwInput.setEnabled(true);
					gameTitle.setVisible(true);
					btnGameStart.setVisible(true);
					btnExtra.setVisible(true);
					btnLogIn.setEnabled(true);
					titlePanel.setVisible(true);
				}
			}
		});
		gamePanel.add(btnAfterGame);

		// 기록실패널 구성
		tableModel2 = new DefaultTableModel(colNames2, 0);
		recordTable = new JTable(tableModel2);
		recordTable.setDefaultEditor(Object.class, null);

		JScrollPane archiveScrollPane = new JScrollPane(recordTable);
		archiveScrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		archiveScrollPane.setBounds(40, 30, 360, 390);
		recordPanel.add(archiveScrollPane);

		DefaultTableCellRenderer centerRenderer2 = new DefaultTableCellRenderer();
		centerRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
		recordTable.setDefaultRenderer(Object.class, centerRenderer2);
		TableColumn fistColumn = recordTable.getColumnModel().getColumn(0); // 첫째 칼럼 선택
		fistColumn.setPreferredWidth(100); // 그 칼럼의 크기를 100으로 설정

		JTextField nickSearch = new JTextField("닉 네 임");
		nickSearch.setForeground(Color.GRAY);
		nickSearch.setFont(taskBarFont);
		nickSearch.setHorizontalAlignment(JLabel.CENTER);
		nickSearch.setBorder(new BevelBorder(BevelBorder.LOWERED));
		nickSearch.setBounds(45, 450, 150, 30);

		nickSearch.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				if (nickSearch.getText().equals("닉 네 임")) {
					nickSearch.setText("");
					nickSearch.setForeground(Color.black);
				} else if (!nickSearch.getText().equals("")) {
					idInput.setForeground(Color.black);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (nickSearch.getText().equals("")) {
					nickSearch.setForeground(Color.gray);
					nickSearch.setText("닉 네 임");
				}
			}
		});
		recordPanel.add(nickSearch);

		JButton btnNickRecord = new JButton("검색");
		btnNickRecord.setFont(taskBarFont);
		btnNickRecord.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnNickRecord.setContentAreaFilled(false);
		btnNickRecord.setFocusPainted(false);
		btnNickRecord.setBounds(195, 450, 50, 30);
		btnNickRecord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tableModel2.setRowCount(0);
				String nickname = nickSearch.getText();
				ArrayList<RecordVO> list = recordDao.select(nickname);
				if (nickname.equals("닉 네 임")) {
					JOptionPane.showMessageDialog(recordFrame, "닉네임을 입력해주세요");
				}
				if (list.size() == 0) {
					JOptionPane.showMessageDialog(recordFrame, "해당 닉네임의 기록이 없습니다");
				} else {
					tableModel2.setRowCount(0);
					for (int i = 0; i < list.size(); i++) {
						records2 = new Object[] { list.get(i).getNickname(), list.get(i).getTotalWords(),
								list.get(i).getWordScore(), list.get(i).getSurviveScore(), list.get(i).getAccuracy() };
						tableModel2.addRow(records2);
					}
				}
				nickSearch.setText("");
			}
		});
		recordPanel.add(btnNickRecord);

		ActionListener enterNickSearch = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel2.setRowCount(0);
				String nickname = nickSearch.getText();
				ArrayList<RecordVO> list = recordDao.select(nickname);
				if (nickname.equals("닉 네 임")) {
					JOptionPane.showMessageDialog(recordFrame, "닉네임을 입력해주세요");
				}
				if (list.size() == 0) {
					JOptionPane.showMessageDialog(recordFrame, "해당 닉네임의 기록이 없습니다");
				} else {
					tableModel2.setRowCount(0);
					for (int i = 0; i < list.size(); i++) {
						records2 = new Object[] { list.get(i).getNickname(), list.get(i).getTotalWords(),
								list.get(i).getWordScore(), list.get(i).getSurviveScore(), list.get(i).getAccuracy() };
						tableModel2.addRow(records2);
					}
				}
				nickSearch.setText("");
			}
		};
		btnNickRecord.addActionListener(enterNickSearch);

		nickSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnNickRecord.doClick();
			}
		});

		JButton btnAllRecord = new JButton("전체 검색");
		btnAllRecord.setFont(taskBarFont);
		btnAllRecord.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnAllRecord.setContentAreaFilled(false);
		btnAllRecord.setFocusPainted(false);
		btnAllRecord.setBounds(245, 450, 100, 30);
		btnAllRecord.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel2.setRowCount(0);
				ArrayList<RecordVO> list = recordDao.select();
				for (int i = 0; i < list.size(); i++) {
					records2 = new Object[] { list.get(i).getNickname(), list.get(i).getTotalWords(),
							list.get(i).getWordScore(), list.get(i).getSurviveScore(), list.get(i).getAccuracy() };
					tableModel2.addRow(records2);
				}
			}
		});
		recordPanel.add(btnAllRecord);

		JButton btnRecordEsc = new JButton("종료");
		btnRecordEsc.setFont(taskBarFont);
		btnRecordEsc.setBorder(new BevelBorder(BevelBorder.RAISED));
		btnRecordEsc.setContentAreaFilled(false);
		btnRecordEsc.setFocusPainted(false);
		btnRecordEsc.setBounds(345, 450, 50, 30);
		btnRecordEsc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				recordFrame.setVisible(false);
			}
		});
		recordPanel.add(btnRecordEsc);

	} // end initialize()

	private void startGame() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				shuffleAndShowWord(displayWord);
			}
		}).start();

	}

	private void shuffleAndShowWord(JLabel displayWord) {
		displayWord.setFont(new Font("나눔바른고딕", Font.BOLD, 90));

		try { // 초기 카운트다운 표시
			displayWord.setText("③");
			Thread.sleep(1000);
			displayWord.setText("②");
			Thread.sleep(1000);
			displayWord.setText("①");
			Thread.sleep(1000);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		displayWord.setText("");
		displayWord.setFont(new Font("나눔바른고딕", Font.BOLD, 40));

		// 단어 목록 불러오고 섞기
		wordsList = wordDao.select();
		Collections.shuffle(wordsList);
		System.out.println("총 단어 개수는: " + wordsList.size());
		gameStartTime = LocalDateTime.now(); // 지금부터 시간 재자
		// 첫 번째 단어 표시
		nextWord(displayWord);
	}

	private void nextWord(JLabel displayWord) {
		if (currentWordIndex < wordsList.size()) {
			WordVO currentWord = wordsList.get(currentWordIndex);
			System.out.println("현재 단어는: " + currentWord.getWord());
			displayWord.setText(currentWord.getWord());
			gameTextInput.setText("");
			startTimer(); // 타이머 시작
		} else {
			displayWord.setText("단어 끝!");
			gameTextInput.setEnabled(false); // 더 이상 입력 못하게
		}
	}

	private void startTimer() {
		wordTimer = new Timer(timeLimit, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stackedSkippedWords++;
				skippedWord.setText("지나간 단어  " + stackedSkippedWords + "  개  ");
				if (stackedSkippedWords + stackedMissedWords >= 5) { // 합쳐서 5개면
					displayWord.setText("GAME OVER!");
					gameTextInput.setEnabled(false); // 더 이상 입력 못하게
					wordTimer.stop();
					setGameResult();
				} else {
					currentWordIndex++;
					currentWord.setText("No.  " + currentWordIndex);
					nextWord(displayWord); // 다음 단어로 넘어가기
				}
			}

		});
		wordTimer.setRepeats(false); // 타이머가 한 번만 실행되도록 설정
		wordTimer.start();
	}

	private void setGameResult() { // 점수 산정하는 메소드
		if (isLogIn) {
			JOptionPane.showMessageDialog(mainFrame, tempNick + "님의 단어 점수는 " + wordScore + "점입니다");
			btnAfterGame.setEnabled(true);
			recordNickname = tempNick;
			recordWordScore = wordScore;
			recordTotalWords = stackedPassedWords + stackedMissedWords;
			accuracy = (int) (((float) stackedPassedWords / (recordTotalWords)) * 100);
			System.out.println("총 단어 수는 " + stackedMissedWords + "통과한건 " + stackedPassedWords + " 오답인건 "
					+ stackedMissedWords + "정답률은 " + accuracy);
			gameEndTime = LocalDateTime.now();
			surviveTime = Duration.between(gameStartTime, gameEndTime);
			recordSurviveScore = (int) surviveTime.toSeconds(); // toSeconds는 long으로 나오므로 int로 강제 형변환
			System.out.println("시간 점수는 " + recordSurviveScore);
			scoreMsg4.setText(recordSurviveScore + "  점  ");
			RecordVO vo = new RecordVO(recordNickname, recordTotalWords, recordWordScore, recordSurviveScore, accuracy);
			int result = recordDao.recordInsert(vo);
			tableModel2.setRowCount(0);
			ArrayList<RecordVO> list = recordDao.select();
			for (int i = 0; i < list.size(); i++) {
				records2 = new Object[] { list.get(i).getNickname(), list.get(i).getTotalWords(),
						list.get(i).getWordScore(), list.get(i).getSurviveScore(), list.get(i).getAccuracy() };
				tableModel2.addRow(records2);
			}
			recordTotalWords = 0;
			accuracy = 0;
			if (result == 1) {
				passedWord.setText("게임 저장 완료");
				fadeOut(passedWord, 500);
				passedWord.setText("");
			}
			recordNickname = "";
			recordWordScore = 0;
		} else {
			JOptionPane.showMessageDialog(mainFrame, "비회원님의 단어 점수는 " + wordScore + "점입니다");
			btnAfterGame.setEnabled(true);
			btnAfterGame.setEnabled(true);
			gameEndTime = LocalDateTime.now();
			surviveTime = Duration.between(gameStartTime, gameEndTime);
			System.out.println("시간 점수는 " + (int) surviveTime.toSeconds());
			scoreMsg4.setText((int) surviveTime.toSeconds() + "  점  ");
		}

	}

	private void fadeOut(JLabel label, int duration) {
		Timer timer = new Timer(20, null); // 20ms 간격으로 타이머 설정
		int steps = duration / 30; // 전체 단계 수
		step = 0; // 현재 단계

		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (step < steps) {
					float alpha = 1.0f - (float) step / steps; // 투명도 계산
					label.setForeground(new Color(0, 0, 0, (int) (alpha * 255))); // 투명도 적용
					step++;
				} else {
					label.setVisible(false); // 완료되면 JLabel을 보이지 않게 설정
					timer.stop(); // 타이머 중지
				}
			}
		});
		timer.start(); // 타이머 시작
	}

	private void changeScoreColor(JLabel label, Color color) {
		Color originalColor = label.getBackground(); // 원래 배경 색 저장
		label.setBackground(color);

		Timer timer = new Timer(500, new ActionListener() { // 0.5초 후에 배경색을 원래 색으로 되돌리는 타이머 설정
			@Override
			public void actionPerformed(ActionEvent e) {
				label.setBackground(originalColor); // 원래 색으로 복원
				((Timer) e.getSource()).stop(); // 타이머 중지
			}
		});
		timer.setRepeats(false); // 한 번만 실행되도록 설정
		timer.start();

	}
}
