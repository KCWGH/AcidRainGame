package wordgame.word;

public class WordVO {

	int wordId;
	String word;
	int difficulty;

	public WordVO() {
	}

	public WordVO(String word) {
		this.word = word;
	}

	public WordVO(int wordId, String word, int difficulty) {
		this.wordId = wordId;
		this.word = word;
		this.difficulty = difficulty;
	}

	public int getWordId() {
		return wordId;
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public String toString() {
		return "WordVO [wordId=" + wordId + ", word=" + word + ", difficulty=" + difficulty + "]";
	}

}
