package kr.or.ddit.mapper;

import kr.or.ddit.vo.MemberVO;

public interface ILoginMapper {

	public MemberVO loginCheck(MemberVO member);

}
