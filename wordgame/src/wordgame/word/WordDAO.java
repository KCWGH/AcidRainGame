package wordgame.word;

import java.util.ArrayList;

public interface WordDAO {

	// 단어 삽입
	public abstract int insert(WordVO vo);

	// 단어 넣으면 -> 단어번호 출력
	public abstract int findWordId(String word);

	// 단어번호 넣으면 -> 단어 출력
	public abstract String findWord(int wordId);

	// 난이도 넣으면 존재하는지 검색
	public abstract int isExist(int difficulty);

	// 난이도별 단어 검색
	public abstract ArrayList<WordVO> select(int difficulty);
	
	// 전체 단어 검색 - 단어만 빼내기
	public abstract ArrayList<WordVO> select();
	
	// 전체 칼럼 검색
	public abstract ArrayList<WordVO> selectAll();

	// 단어 수정
	public abstract int update(WordVO vo);

	// 단어 삭제
	public abstract void delete(String word);
}
