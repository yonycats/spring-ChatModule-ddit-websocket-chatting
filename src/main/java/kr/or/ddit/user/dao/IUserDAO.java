package kr.or.ddit.user.dao;

import java.util.Map;

import kr.or.ddit.vo.MemberVO;

public interface IUserDAO {

	public MemberVO login(Map<String, Object> map);

}
