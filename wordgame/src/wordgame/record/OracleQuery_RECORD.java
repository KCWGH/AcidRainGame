package wordgame.record;

public interface OracleQuery_RECORD {
	public static final String URL = "jdbc:oracle:thin:@192.168.0.136:1521:xe";
	public static final String USER = "scott";
	public static final String PASSWORD = "tiger";

	public static final String RECORD_TABLE = "GAME_RECORD";
	public static final String COL_NICKNAME = "NICKNAME";
	public static final String COL_TOTAL_WORDS = "TOTAL_WORDS";
	public static final String COL_WORD_SCORE = "WORD_SCORE";
	public static final String COL_TIME_SCORE = "TIME_SCORE";

	// 게임 기록 삽입
	// INSERT INTO GAME_RECORD
	// VALUES (?, ?, ?);
	public static final String SQL_INSERT_RECORD = "INSERT INTO " + RECORD_TABLE + " VALUES (?, ?, ?, ?, ?)";

	// 게임 기록 전체 검색(기록실 켤 때 사용)
	// SELECT * FROM GAME_RECORD;
	public static final String SQL_SELECT_RECORD = "SELECT * FROM " + RECORD_TABLE;

	// 닉네임별 기록 검색
	// SELECT * FROM GAME_RECORD
	// WHERE NICKNAME = ?;
	public static final String SQL_SELECT_RECORDS_BY_NICKNAME = "SELECT * FROM " + RECORD_TABLE + " WHERE "
			+ COL_NICKNAME + " = ?";
}
