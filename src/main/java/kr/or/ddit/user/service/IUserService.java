package kr.or.ddit.user.service;

import java.util.Map;

import kr.or.ddit.vo.MemberVO;

public interface IUserService {

	public MemberVO login(Map<String, Object> map);

}
