package wordgame.record;

import java.util.ArrayList;

public interface RecordDAO {

	// 게임 기록 삽입
	public abstract int recordInsert(RecordVO vo);

	// 게임 기록 전체 보기(처음 기록실 열 때)
	public abstract ArrayList<RecordVO> select();
	
	// 닉네임별 기록 보기
	public abstract ArrayList<RecordVO> select(String nickname);
}
