package wordgame.record;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import oracle.jdbc.OracleDriver;

public class RecordDAOImple implements OracleQuery_RECORD, RecordDAO {

	// 싱글톤 만들기
	private static RecordDAOImple instance = null;

	private RecordDAOImple() {
	}

	public static RecordDAOImple getInstance() {
		if (instance == null) {
			instance = new RecordDAOImple();
		}
		return instance;
	}

	// 게임 기록 삽입
	public int recordInsert(RecordVO vo) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_INSERT_RECORD);

			String nickname = vo.getNickname();
			int totalWords = vo.getTotalWords();
			int wordScore = vo.getWordScore();
			int surviveScore = vo.getSurviveScore();
			int accuracy = vo.getAccuracy();

			System.out.println(nickname + " " + totalWords + " " + wordScore + " " + surviveScore + " " + surviveScore);

			pstmt.setString(1, nickname); // 첫 ?니까 1
			pstmt.setInt(2, totalWords);
			pstmt.setInt(3, wordScore); // 둘째 ?니까 2
			pstmt.setInt(4, surviveScore); // 셋째 ?니까 3
			pstmt.setInt(5, accuracy);

			result = pstmt.executeUpdate();

			if (result == 1) {
				System.out.println("게임 기록 저장 완료");
			} else {
				System.out.println("게임 기록 저장 실패");
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

	// 전체 검색
	public ArrayList<RecordVO> select() {
		ArrayList<RecordVO> list = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_SELECT_RECORD);

			rs = pstmt.executeQuery();

			list = new ArrayList<>();

			while (rs.next()) {
				String nickname = rs.getString(1);
				int totalWords = rs.getInt(2);
				int wordScore = rs.getInt(3);
				int surviveScore = rs.getInt(4);
				int accuracy = rs.getInt(5);
				RecordVO vo = new RecordVO(nickname, totalWords, wordScore, surviveScore, accuracy);
				list.add(vo);
			}
			if (list.size() == 0) {
				System.out.println("검색된 기록이 없습니다");
			} else {
				System.out.println("총 " + list.size() + "개의 기록이 있습니다");
			}
		} catch (SQLException e) {
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return list;
	};

	// 닉네임별 검색
	public ArrayList<RecordVO> select(String nickname) {
		ArrayList<RecordVO> list = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_SELECT_RECORDS_BY_NICKNAME);
			pstmt.setString(1, nickname);

			rs = pstmt.executeQuery();
			list = new ArrayList<>();
			while (rs.next()) {
				int totalWords = rs.getInt(2);
				int wordScore = rs.getInt(3);
				int timeScore = rs.getInt(4);
				int accuracy = rs.getInt(5);

				RecordVO vo = new RecordVO(nickname, totalWords, wordScore, timeScore, accuracy);
				list.add(vo);
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
		return list;
	};
}
