package wordgame.word;

public interface OracleQuery_WORD {
	public static final String URL = "jdbc:oracle:thin:@192.168.0.136:1521:xe";
	public static final String USER = "scott";
	public static final String PASSWORD = "tiger";

	public static final String WORD_TABLE = "GAME_WORD";
	public static final String COL_WORD_ID = "WORD_ID";
	public static final String COL_WORD = "WORD";
	public static final String COL_DIFFICULTY = "DIFFICULTY";

	// 단어 삽입
	// INSERT INTO WORD_TABLE
	// VALUES (WORD_SEQ.NEXTVAL, ?, ?);
	public static final String SQL_INSERT_WORD = "INSERT INTO " + WORD_TABLE + " (" + COL_WORD_ID + ", " + COL_WORD
			+ ", " + COL_DIFFICULTY + ") VALUES (WORD_SEQ.NEXTVAL, " + "?, ?)";

	// 단어로 단어번호 검색
	// SELECT WORD_ID
	// FROM WORD_TABLE
	// WHERE WORD = ?;
	public static final String SQL_SELECT_WORD_ID_BY_WORD = "SELECT " + COL_WORD_ID + " FROM " + WORD_TABLE + " WHERE "
			+ COL_WORD + " = ?";

	// 단어번호로 단어 검색
	// SELECT WORD
	// FROM WORD_TABLE
	// WHERE WORD_ID = ?;
	public static final String SQL_SELECT_WORD_BY_WORD_ID = "SELECT " + COL_WORD + " FROM " + WORD_TABLE + " WHERE "
			+ COL_WORD_ID + " = ?";

	// 난이도를 입력하면, 해당 난이도의 단어가 있는지 검색
	// SELECT COUNT(*) AS count
	// FROM WORD_TABLE
	// WHERE DIFFICULTY = ?;
	public static final String SQL_WORD_IS_EXIST_BY_DIFFICULTY = "SELECT COUNT(*) AS count FROM " + WORD_TABLE
			+ " WHERE " + COL_DIFFICULTY + " = ?";

	// 난이도별 단어 검색
	// SELECT *
	// FROM WORD_TABLE
	// WHERE DIFFICULTY = ?;
	public static final String SQL_SELECT_WORDS_BY_DIFFICULTY = "SELECT * FROM " + WORD_TABLE + " WHERE "
			+ COL_DIFFICULTY + " = ?";

	// 모든 칼럼 검색
	// SELECT *
	// FROM WORD_TABLE;
	public static final String SQL_SELECT_ALL = "SELECT * FROM " + WORD_TABLE;

	// 모든 단어 검색
	// SELECT WORD
	// FROM WORD_TABLE;
	public static final String SQL_SELECT_WORDS = "SELECT " + COL_WORD + " FROM " + WORD_TABLE;

	// 단어 수정
	// UPDATE WORD_TABLE
	// SET WORD = ?, DIFFICULTY = ?
	// WHERE WORD_ID = ?;
	public static final String SQL_UPDATE_WORD = "UPDATE " + WORD_TABLE + " SET " + COL_WORD + " = ?, " + COL_DIFFICULTY
			+ " = ? WHERE " + COL_WORD_ID + " = ?";

	// 단어 삭제 (단어로)
	// DELETE FROM WORD_TABLE
	// WHERE WORD_ID = 1;
	public static final String SQL_DELETE_WORD = "DELETE FROM " + WORD_TABLE + " WHERE " + COL_WORD + " = ?";

}
