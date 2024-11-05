package kr.or.ddit.service;

import java.util.List;
import java.util.Map;

import kr.or.ddit.vo.ChatRoomVO;
import kr.or.ddit.vo.ChatVO;
import kr.or.ddit.vo.MemberVO;

public interface IChatService {
	
	public List<MemberVO> getmemberList();

	public List<ChatRoomVO> getRoomList(int userId);

	public List<ChatVO> getChatMessage(int roomNo);

	public MemberVO getMember(int userNo);

	public int checkOneToOneChatRoom(Map<String, MemberVO> userNoMap);

	public int addOneToOneChatRoom(Map<String, MemberVO> userNoMap);

	public ChatRoomVO getChatRoomOne(int chatRoomNo);

	public List<Integer> getUnreadCntByUser(ChatVO chatMessage);

	public void readMessageInRoom(ChatVO chatMessage);

	public void insertMessage(ChatVO chatMessage);

	public int getMemberCount(int roomNo);

	public void deleteRoomMemberMe(Map<String, Object> map);

}
