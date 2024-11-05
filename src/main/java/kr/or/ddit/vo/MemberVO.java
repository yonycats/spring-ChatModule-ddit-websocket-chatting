package kr.or.ddit.vo;

import lombok.Data;

@Data
public class MemberVO {
	private int userNo;
	private String userId;
	private String userPw;
	private String userName;
	private String userImg;
	private String userDep;
}
