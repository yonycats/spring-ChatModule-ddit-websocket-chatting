package kr.or.ddit.vo;

import lombok.Data;

@Data
public class ChatRoomVO {
	private int roomNo;
	private String roomName;
	private String roomImg;		// 방 대표 이미지
	private String roomDate;	// 빙 생성일
	private int roomOwnerNo;	// 방장
	
	private String chatDate;	// 가장 마지막 채팅 날짜
	private String chatMsg;		// 가장 마지막 채팅 메시지
	private int roomPeople;		// 채팅방 참여인원
	private int unReadCount;		// 내가 읽지 않은 채팅 메시지 갯수
}
