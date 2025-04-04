package wordgame.record;

public class RecordVO {

	String nickname;
	int totalWords;
	int wordScore;
	int surviveScore;
	int accuracy;

	public RecordVO() {
	}

	public RecordVO(String nickname, int totalWords, int wordScore, int surviveScore, int accuracy) {
		this.nickname = nickname;
		this.totalWords = totalWords;
		this.wordScore = wordScore;
		this.surviveScore = surviveScore;
		this.accuracy = accuracy;
	}

	public String getNickname() {
		return nickname;
	}

	public int getTotalWords() {
		return totalWords;
	}

	public int getWordScore() {
		return wordScore;
	}

	public int getSurviveScore() {
		return surviveScore;
	}

	public int getAccuracy() {
		return accuracy;
	}

}
