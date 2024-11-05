package kr.or.ddit.mapper;

import java.util.List;
import java.util.Map;

import kr.or.ddit.vo.ChatRoomVO;
import kr.or.ddit.vo.ChatVO;
import kr.or.ddit.vo.MemberVO;

public interface IChatMapper {

	public List<MemberVO> getmemberList();

	public List<ChatRoomVO> getRoomList(int userNo);

	public List<ChatVO> getChatMessage(int roomNo);

	public MemberVO getMember(int userNo);

	public Integer checkOneToOneChatRoom(Map<String, MemberVO> userNoMap);

	public int addOneToOneChatRoom(Map<String, Object> roomData);

	public void addRoomMemberOwner(Map<String, Integer> roomMember);

	public void addRoomMemberNormal(Map<String, Integer> roomMember);

	public ChatRoomVO getChatRoomOne(int chatRoomNo);

	public List<Integer> getUnreadCntByUser(ChatVO chatMessage);

	public void readMessageInRoom(ChatVO chatMessage);

	public void insertMessage(ChatVO chatMessage);

	public List<MemberVO> getRoomMemberList(int roomNo);

	public void insertUnreadMember(Map<String, Object> map);

	public int getMemberCount(int roomNo);

	public void deleteRoomMemberMe(Map<String, Object> map);

	public void insertChatRoomFirstMsg(Map<String, Object> chatData);

	public int getRoomMember(int roomNo);

	public void deleteChatList(int roomNo);

	public void deleteChatRoom(int roomNo);


}
