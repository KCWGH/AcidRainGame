package wordgame.word;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import oracle.jdbc.OracleDriver;

public class WordDAOImple implements WordDAO, OracleQuery_WORD {

	// 싱글톤 만들기
	private static WordDAOImple instance = null;

	private WordDAOImple() {
	}

	public static WordDAOImple getInstance() {
		if (instance == null) {
			instance = new WordDAOImple();
		}
		return instance;
	}

	// 단어 삽입
	public int insert(WordVO vo) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_INSERT_WORD);
			String word = vo.getWord();
			int difficulty = word.length();

			System.out.println("데이터에 전송된 단어 : " + word + " 난이도 : " + difficulty);

			pstmt.setString(1, word);
			pstmt.setInt(2, difficulty);

			if (!(word.equals("해당하는 값을 입력해주세요"))) {
				result = pstmt.executeUpdate();
			}

			if (result == 1) {
				System.out.println("단어 저장 완료");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	};

	// 단어 넣으면 단어번호 출력
	public int findWordId(String word) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_SELECT_WORD_ID_BY_WORD);
			pstmt.setString(1, word);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}
			if (result == 0) {
				System.out.println("등록되지 않은 단어입니다");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	};

	public int isExist(int difficulty) {
		int count = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_WORD_IS_EXIST_BY_DIFFICULTY);

			pstmt.setInt(1, difficulty);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				count = rs.getInt(1);
			}
			if (count != 0) {
				System.out.println("해당 난이도의 단어가 " + count + "개 존재합니다");
			} else {
				System.out.println("해당 난이도의 단어가 없습니다");
			}

		} catch (SQLException e) {
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	};

	// 난이도별 단어 모두 검색
	public ArrayList<WordVO> select(int difficulty) {
		ArrayList<WordVO> list = null;
		int wordId = 0;
		String word = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_SELECT_WORDS_BY_DIFFICULTY);
			pstmt.setInt(1, difficulty);

			rs = pstmt.executeQuery();

			list = new ArrayList<>();
			while (rs.next()) {
				wordId = rs.getInt(1);
				word = rs.getString(2);
				difficulty = rs.getInt(3);
				WordVO vo = new WordVO(wordId, word, difficulty);
				list.add(vo);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	};

	// 전체 단어 검색 - 단어만 빼내기
	public ArrayList<WordVO> select() {
		ArrayList<WordVO> list = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_SELECT_WORDS);

			rs = pstmt.executeQuery();

			list = new ArrayList<>();

			while (rs.next()) {
				String word = rs.getString(1);
				WordVO vo = new WordVO(word);
				list.add(vo);
			}

		} catch (SQLException e) {
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	};

	// 전체 칼럼 검색
	public ArrayList<WordVO> selectAll() {
		ArrayList<WordVO> list = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_SELECT_ALL);

			rs = pstmt.executeQuery();

			list = new ArrayList<>();

			while (rs.next()) {
				int wordId = rs.getInt(1);
				String word = rs.getString(2);
				int difficulty = rs.getInt(3);
				WordVO vo = new WordVO(wordId, word, difficulty);
				list.add(vo);
			}
			if (list.size() == 0) {
				System.out.println("등록된 단어가 없습니다");
			} else {
				System.out.println("총 " + list.size() + "개의 단어가 등록되어 있습니다");
			}

		} catch (SQLException e) {
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	};

	// 단어번호 넣으면, 단어 출력
	public String findWord(int wordId) {
		String result = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_SELECT_WORD_BY_WORD_ID);
			pstmt.setInt(1, wordId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return result;
	};

	// 해당 단어번호의 단어 수정
	public int update(WordVO vo) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_UPDATE_WORD);

			pstmt.setString(1, vo.getWord());
			pstmt.setInt(2, vo.getDifficulty());
			pstmt.setInt(3, vo.getWordId());

			System.out.println("단어 : " + vo.getWord() + " 난이도 : " + vo.getDifficulty() + " 단어번호 : " + vo.getWordId());

			result = pstmt.executeUpdate();

			if (result == 1) {
				System.out.println("수정 완료");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	};

	// 단어 삭제
	public void delete(String word) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_DELETE_WORD);

			pstmt.setString(1, word);
			pstmt.executeUpdate();

			System.out.println("단어 삭제 완료");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	};

}
