package kr.or.ddit.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.mapper.ILoginMapper;
import kr.or.ddit.vo.MemberVO;

@Service
public class LoginServiceImpl implements ILoginService {

	@Inject
	private ILoginMapper loginMapper;

	@Override
	public MemberVO loginCheck(MemberVO member) {
		return loginMapper.loginCheck(member);
	}

}
	