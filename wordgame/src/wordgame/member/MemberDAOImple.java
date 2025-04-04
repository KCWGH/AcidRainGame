package wordgame.member;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleDriver;

public class MemberDAOImple implements OracleQuery_MEMBER, MemberDAO {

	// 싱글톤 만들기
	private static MemberDAOImple instance = null;

	private MemberDAOImple() {
	}

	public static MemberDAOImple getInstance() {
		if (instance == null) {
			instance = new MemberDAOImple();
		}
		return instance;
	}

	// 로그인
	public String login(String memberId, String pw) {
		String nickname = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_LOGIN);
			pstmt.setString(1, memberId); // 첫 ?니까 1
			pstmt.setString(2, pw); // 둘째 ?니까 2

			System.out.println("데이터베이스에 전송된 id : " + memberId + " pw : " + pw);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				nickname = rs.getString(1);
				System.out.println("찾은 닉넴: " + nickname);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				conn.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return nickname;
	}

	// 회원가입
	public int insert(MemberVO vo) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_INSERT_MEMBER);

			String memberId = vo.getMemberId();
			String pw = vo.getPw();
			String nickname = vo.getNickname();
			String email = vo.getEmail();

			System.out.println(memberId + " " + pw + " " + nickname + " " + email);

			pstmt.setString(1, memberId); // 첫 ?니까 1
			pstmt.setString(2, pw); // 둘째 ?니까 2
			pstmt.setString(3, nickname); // 셋째 ?니까 3
			pstmt.setString(4, email);

			result = pstmt.executeUpdate();

			if (result == 1) {
				System.out.println("회원가입 완료");
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
	}

	// 아이디 중복 체크
	public int dupCheckId(MemberVO vo) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_DUPCHECK_ID);

			pstmt.setString(1, vo.getMemberId());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
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

	// 이메일 중복 체크
	public int dupCheckEmail(MemberVO vo) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_DUPCHECK_EMAIL);

			pstmt.setString(1, vo.getEmail());

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
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

	// 관리자인지 체크
	public String adminCheck(MemberVO vo) {
		String result = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_ADMIN_CHECK);
			String id = vo.getMemberId();
			String pw = vo.getPw();

			pstmt.setString(1, id);
			pstmt.setString(2, pw);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getString(1);
			}
			if (result.equals("admin")) {
				System.out.println(result + "입니다");
			} else {
				System.out.println("admin이 아닙니다");
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

	// 이메일로 아이디, 비밀번호 찾기
	public MemberVO select(String Email) {
		MemberVO vo = null;
		String id = "";
		String pw = "";
		int isAdmin = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");

			pstmt = conn.prepareStatement(SQL_FINDACCOUNT);

			pstmt.setString(1, Email);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				id = rs.getString(1);
				pw = rs.getString(2);
				isAdmin = rs.getInt(3);
			}

			vo = new MemberVO(id, pw, isAdmin);

			if (id.equals("") || pw.equals("")) { // 찾은 값이 없으면(회원이 아니면)
				System.out.println("해당 이메일로 등록된 회원이 없습니다");
			} else if (isAdmin == 0) {
				System.out.println("일반 사용자입니다");
			} else {
				System.out.println("관리자입니다");
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
		return vo;
	};

	// 회원정보 수정(ID는 PK이므로 수정 불가)
	public int update(MemberVO vo) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");
			pstmt = conn.prepareStatement(SQL_UPDATE);

			String pw = vo.getPw();
			String nickname = vo.getNickname();
			String email = vo.getEmail();
			String id = vo.getMemberId();

			pstmt.setString(1, pw);
			pstmt.setString(2, nickname);
			int dupcheck = dupCheckEmail(vo);
			if (dupcheck == 0) {
				pstmt.setString(3, email);
				pstmt.setString(4, id);

				System.out.println(
						"데이터베이스에 전송된 pw : " + pw + " nickname : " + nickname + " email : " + email + " id: " + id);
				result = pstmt.executeUpdate();

				if (result == 1) {
					System.out.println("수정 완료");
				}
			} else {
				result = 2;
				System.out.println("중복된 이메일입니다");
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

	// 회원정보 삭제
	public int delete(String memberId, String pw) {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			DriverManager.registerDriver(new OracleDriver());
			System.out.println("드라이버 로드 성공");

			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("DB 연결 성공");
			pstmt = conn.prepareStatement(SQL_DELETE);

			String id = memberId;
			String password = pw;

			pstmt.setString(1, id);
			pstmt.setString(2, password);

			result = pstmt.executeUpdate();
			if (result == 1) {
				System.out.println("삭제 완료");
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
	}

}
