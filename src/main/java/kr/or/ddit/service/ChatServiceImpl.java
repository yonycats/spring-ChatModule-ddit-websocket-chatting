package kr.or.ddit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import kr.or.ddit.mapper.IChatMapper;
import kr.or.ddit.vo.ChatRoomVO;
import kr.or.ddit.vo.ChatVO;
import kr.or.ddit.vo.MemberVO;

@Service
public class ChatServiceImpl implements IChatService {

	@Inject
	IChatMapper chatMapper;
	
	// 전체 member 리스트를 불러오는 메서드
	@Override
	public List<MemberVO> getmemberList() {
		return chatMapper.getmemberList();
	}
	
	// 해당 유저의 채팅방 목록 리스트를 가져오는 메서드
	@Override
	public List<ChatRoomVO> getRoomList(int userNo) {
		return chatMapper.getRoomList(userNo);
	}

	// 해당 채팅방의 메시지 리스트를 가져오는 메서드
	@Override
	public List<ChatVO> getChatMessage(int roomNo) {
		return chatMapper.getChatMessage(roomNo);
	}

	// member 정보 가져오는 메서드
	@Override
	public MemberVO getMember(int userNo) {
		return chatMapper.getMember(userNo);
	}
	
	// 1대 1 채팅방이 존재하는지 확인하는 메서드
	// 해당 유저와 1대 1 채팅방이 있으면 해당 번호 반환, 없으면 0 반환
	@Override
	public int checkOneToOneChatRoom(Map<String, MemberVO> userNoMap) {
		Integer roomNo = chatMapper.checkOneToOneChatRoom(userNoMap);
		
		if (roomNo == null) {
		    roomNo = 0;
		}
		return roomNo;
	}

	// 선택한 유저와의 1대 1 채팅방을 추가하는 메서드
	@Override
	public int addOneToOneChatRoom(Map<String, MemberVO> userNoMap) {
	    // 1. 채팅방 테이블 추가하기
	    // Map에 roomNo를 포함시키기 위해 새로운 Map 생성
	    Map<String, Object> roomData = new HashMap<>(userNoMap);
	    roomData.put("roomNo", 0); // 초기값 설정

	    chatMapper.addOneToOneChatRoom(roomData);
	    
	    // roomNo 가져오기 (selectKey로 세팅)
	    int roomNo = (Integer) roomData.get("roomNo");
	    
	    // 2. 채팅방에 생성 기본 메시지 삽입하기
	    Map<String, Object> chatData = new HashMap<>();
	    chatData.put("roomNo", roomNo);
	    chatData.put("userNo", 0);	// member 테이블에 user_no 0번으로 관리용 계정을 따로 만들어놓음
	    chatData.put("chatMsg", "채팅방이 생성되었습니다.");
	    
	    chatMapper.insertChatRoomFirstMsg(chatData);
	    
	    // 3. 채팅방 참여인원 테이블 추가하기
	    Map<String, Integer> roomMember = new HashMap<>();
	    roomMember.put("roomNo", roomNo);
	    roomMember.put("myUserNo", userNoMap.get("myUser").getUserNo());
	    roomMember.put("selectUserNo", userNoMap.get("selectUser").getUserNo());
	    
	    chatMapper.addRoomMemberOwner(roomMember);
	    chatMapper.addRoomMemberNormal(roomMember);
	    
	    return roomNo;
	}

	// 1대 1 채팅방 새로 만들기에서 새로 추가된 채팅방 정보 가져오기
	@Override
	public ChatRoomVO getChatRoomOne(int chatRoomNo) {
		return chatMapper.getChatRoomOne(chatRoomNo);
	}

	// 채팅방 입장시, 해당 방에 있는 본인의 안읽은 채팅 메시지 목록 가져오기
	@Override
	public List<Integer> getUnreadCntByUser(ChatVO chatMessage) {
		return chatMapper.getUnreadCntByUser(chatMessage);
	}

	// 채팅방 입장시, 해당 방에 있는 본인의 안읽은 채팅 메시지 목록을 테이블에서 삭제하기
	@Override
	public void readMessageInRoom(ChatVO chatMessage) {
		chatMapper.readMessageInRoom(chatMessage);
	}

	// 새로운 채팅 메시지를 테이블에 추가하는 메서드
	@Override
	public void insertMessage(ChatVO chatMessage) {
		// 1. 채팅 메시지 추가
		chatMapper.insertMessage(chatMessage);
		
		int roomNo = chatMessage.getRoomNo();
		// 해당 채팅방에 속한 멤버 리스트 가져오기
		List<MemberVO> memberList = chatMapper.getRoomMemberList(roomNo);
		
		// 2. 메시지 등록 시마다 해당 방에 속한 유저들 대상으로 안읽은 유저 테이블에 추가
		Map<String, Object> map = new HashMap<String, Object>();
		for (MemberVO member : memberList) {
			map.put("chatNo", chatMessage.getChatNo());
			map.put("roomNo", chatMessage.getRoomNo());
			map.put("userNo", member.getUserNo());
			
			chatMapper.insertUnreadMember(map);
		}
	}

	// 해당 채팅방 인원수 가져오는 메서드
	@Override
	public int getMemberCount(int roomNo) {
		return chatMapper.getMemberCount(roomNo);
	}

	// 해당 채팅방의 참여 멤버에서 본인을 지우는 메서드
	@Override
	public void deleteRoomMemberMe(Map<String, Object> map) {
		// 해당 채팅방에 참여 중인 인원수 가져오기
		int roomPeopleCount = chatMapper.getRoomMember((int)map.get("roomNo"));
		
		// 만약 마지막으로 남은 멤버라면
		if (roomPeopleCount == 1) {
			// 1. 해당 채팅방 채팅 메시지 지우기
			chatMapper.deleteChatList((int)map.get("roomNo"));
			
			// 2. 해당 채팅방 참여 멤버에서 본인 지우기
			chatMapper.deleteRoomMemberMe(map);
			
			// 3. 해당 채팅방 지우기
			chatMapper.deleteChatRoom((int)map.get("roomNo"));
		} else {
			chatMapper.deleteRoomMemberMe(map);
		}
	}
}
