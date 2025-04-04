package wordgame.member;

public interface MemberDAO {

	// 로그인, 회원정보 수정 전작업(아이디와 비밀번호 선입력해서 확인)
	public abstract String login(String memberId, String pw);

	// 회원 가입
	public abstract int insert(MemberVO vo);

	// 아이디 중복 체크
	public abstract int dupCheckId(MemberVO vo);

	// 이메일 중복 체크
	public abstract int dupCheckEmail(MemberVO vo);

	// 관리자인지 체크
	public abstract String adminCheck(MemberVO vo);

	// 회원 이메일로 아이디와 비밀번호 찾기
	public abstract MemberVO select(String email);

	// 회원정보 수정
	public abstract int update(MemberVO vo);

	// 연락처 삭제
	public abstract int delete(String memberId, String pw);
}
