package wordgame.member;

public class MemberVO {

	String memberId;
	String pw;
	String nickname;
	String email;
	int isAdmin;

	public MemberVO() {
	}

	public MemberVO(String nickname) {
		this.nickname = nickname;
	}

	public MemberVO(String memberId, String pw) {
		this.memberId = memberId;
		this.pw = pw;
	}

	public MemberVO(String memberId, String pw, int isAdmin) {
		this.memberId = memberId;
		this.pw = pw;
		this.isAdmin = isAdmin;
	}

	public MemberVO(String memberId, String pw, String nickname, String email) {
		this.memberId = memberId;
		this.pw = pw;
		this.nickname = nickname;
		this.email = email;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getIsAdmin() {
		return isAdmin;
	}

	@Override
	public String toString() {
		return "MemberVO [memberId=" + memberId + ", pw=" + pw + ", nickname=" + nickname + ", email=" + email + "]";
	}

}
