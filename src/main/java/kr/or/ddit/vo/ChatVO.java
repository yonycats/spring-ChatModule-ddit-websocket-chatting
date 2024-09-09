package kr.or.ddit.vo;

import lombok.Data;

@Data
public class ChatVO {
	private int chatNo;
	private String chatDate;
	private String chatFile;
	private int userNo;
	private int roomNo;
}
