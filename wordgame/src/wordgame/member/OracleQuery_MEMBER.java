package wordgame.member;

public interface OracleQuery_MEMBER {
	public static final String URL = "jdbc:oracle:thin:@192.168.0.137:1521:xe";
	public static final String USER = "scott";
	public static final String PASSWORD = "tiger";

	public static final String MEMBER_TABLE = "GAME_MEMBER";
	public static final String COL_MEMBER_ID = "MEMBER_ID";
	public static final String COL_PW = "PW";
	public static final String COL_NICKNAME = "NICKNAME";
	public static final String COL_EMAIL = "EMAIL";
	public static final String COL_IS_ADMIN = "IS_ADMIN";

	// 로그인
	// SELECT NICKNAME
	// FROM GAME_MEMBER
	// WHERE MEMBER_ID = ? AND PW = ?;
	public static final String SQL_LOGIN = "SELECT " + COL_NICKNAME + " FROM " + MEMBER_TABLE + " WHERE "
			+ COL_MEMBER_ID + " = ? AND " + COL_PW + " = ?";

	// 회원 가입
	// INSERT INTO GAME_MEMBER
	// VALUES (?, ?, ?, ?);
	public static final String SQL_INSERT_MEMBER = "INSERT INTO " + MEMBER_TABLE + " VALUES (?, ?, ?, ?, 0)";

	// 회원 가입시 중복된 ID가 있는지 검사
	// SELECT COUNT(*)
	// FROM GAME_MEMBER
	// WHERE MEMBER_ID = ?;
	public static final String SQL_DUPCHECK_ID = "SELECT COUNT(*) FROM " + MEMBER_TABLE + " WHERE " + COL_MEMBER_ID
			+ " = ?";

	// 회원 가입시 중복된 이메일이 있는지 검사
	// SELECT COUNT(*)
	// FROM GAME_MEMBER
	// WHERE EMAIL = ?;
	public static final String SQL_DUPCHECK_EMAIL = "SELECT COUNT(*) FROM " + MEMBER_TABLE + " WHERE " + COL_EMAIL
			+ " = ?";

	// 관리자인지 아닌지 여부 확인
	// SELECT CASE WHEN IS_ADMIN = 1 THEN admin
	// ELSE user
	// END AS LOGIN_STATUS
	// FROM GAME_MEMBER
	// WHERE MEMBER_ID = ? AND PW = ?;
	public static final String SQL_ADMIN_CHECK = "SELECT CASE WHEN " + COL_IS_ADMIN
			+ " = 1 THEN 'admin' ELSE 'user' END AS LOGIN_STATUS FROM " + MEMBER_TABLE + " WHERE " + COL_MEMBER_ID
			+ " = ? AND " + COL_PW + " = ?";

	// 아이디와 비밀번호 찾기(가입 시 기입한 이메일 이용)
	// SELECT MEMBER_ID, PW, IS_ADMIN
	// FROM GAME_MEMBER
	// WHERE EMAIL = ?;
	public static final String SQL_FINDACCOUNT = "SELECT " + COL_MEMBER_ID + ", " + COL_PW + ", " + COL_IS_ADMIN
			+ " FROM " + MEMBER_TABLE + " WHERE " + COL_EMAIL + " = ?";

	// 회원 정보 수정
	// UPDATE GAME_MEMBER
	// SET PW = ?,
	// NICKNAME = ?,
	// EMAIL = ?,
	// WHERE ID = ?;
	public static final String SQL_UPDATE = "UPDATE " + MEMBER_TABLE + " SET " + COL_PW + " = ?, " + COL_NICKNAME
			+ " = ?,  " + COL_EMAIL + " = ? " + "WHERE " + COL_MEMBER_ID + " = ?";

	// 회원 삭제
	// DELETE T_MEMBER WHERE ID = ?
	public static final String SQL_DELETE = "DELETE " + MEMBER_TABLE + " WHERE " + COL_MEMBER_ID + " = ? AND " + COL_PW
			+ " = ?";

}
