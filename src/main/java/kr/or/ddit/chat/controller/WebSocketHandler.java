package kr.or.ddit.chat.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.ddit.service.IChatService;
import kr.or.ddit.vo.ChatRoomVO;
import kr.or.ddit.vo.ChatVO;
import kr.or.ddit.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebSocketHandler extends TextWebSocketHandler {

	@Inject
	IChatService chatService;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	// 로그인한 전체 session 목록 담는 List
	private List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();

	// 세션과 유저 정보를 연결하기 위한 Map
	private Map<WebSocketSession, MemberVO> userSessionMap = new ConcurrentHashMap<WebSocketSession, MemberVO>();

	// 채팅방번호, 해당채팅방에 들어와있는 세션들의 List
	private Map<Integer, ArrayList<WebSocketSession>> roomList = new ConcurrentHashMap<Integer, ArrayList<WebSocketSession>>();

	
	/**
	 * websocket 연결 요청 시
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("# 채팅 접속");
		
		// memberVO 가져오기
		Map<String, Object> httpSession = session.getAttributes();
		MemberVO memberVO = (MemberVO) httpSession.get("SessionInfo");
		
		// 채팅에 새로 접속한 사람의 세션을 memberVO 정보와 함께 Map에 추가하기
		userSessionMap.put(session, memberVO);
		
		// 채팅에 새로 접속한 사람 세션 리스트에 추가하기
		sessionList.add(session);
		
		// 해당 유저가 속해있는 채팅방 리스트 모두 가져오기
		List<ChatRoomVO> list = chatService.getRoomList(memberVO.getUserNo());
		
		// 해당 유저가 속한 채팅방이 있는 경우에만 실행 (null 체크)
		if (list != null) {		// 속한 채팅방이 있다면
			for (ChatRoomVO chatRoom : list) {
				// roomList에 현재 내가 속한 채팅방 리스트 넣기
				// 접속한 유저가 속해있는 채팅방 번호를 key로 하여 비어있는 ArrayList를 value로 채워 넣는다.
				// 채팅방을 눌러 입장하기 전까지는 roomList의 value에는 빈 List가 들어가 있으며 
				// 채팅방을 눌러서 입장하면 해당 채팅방에 접속한 session이 List에 추가된다. (Map(채팅방번호, 방접속세션))
				ArrayList<WebSocketSession> userSessionList = new ArrayList<>();
				roomList.put(chatRoom.getRoomNo(), userSessionList);
			}
		}
		
		for (ChatRoomVO chatRoom : list) {
			System.out.println(memberVO.getUserName() + "님이 속해 있는 방 : " + chatRoom.getRoomNo());
		}
		System.out.println();
		System.out.println("연결 후 roomList 상태 : " + roomList);
		System.out.println("연결 후 userSessionMap 상태 :" + userSessionMap);
	}


	/**
	 * websocket 연결 종료 시
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
		// memberVO 가져오기
		Map<String, Object> httpSession = session.getAttributes();
		MemberVO memberVO = (MemberVO) httpSession.get("SessionInfo");
		
		// 해당 유저가 속해있는 채팅방 리스트 모두 가져오기
		List<ChatRoomVO> list = chatService.getRoomList(memberVO.getUserNo());
		
		// 연결끊은 해당 유저 맵에 삭제
		userSessionMap.remove(session);
		
		// 해당 유저가 속한 채팅방이 있는 경우에만 실행 (null 체크)
		if (list != null) {		// 속한 채팅방이 있다면
			for (ChatRoomVO chatRoom : list) {
				// roomList에 방에 해당하는 List에 누군가 있는 경우
				if(roomList.get(chatRoom.getRoomNo()) != null) {
					// 해당 List에서 연결끊은 session 삭제
					roomList.get(chatRoom.getRoomNo()).remove(session);
				}
			}
		}
		
		// 채팅에서 나간 사람 세션 리스트에서 삭제하기
		for(int i = 0; i < sessionList.size(); i++) {
			if(sessionList.get(i).equals(session)) {
				sessionList.remove(sessionList.get(i));
			}
		}

		// 연결끊은 해당 유저 맵에 삭제
		userSessionMap.remove(session);
	}

	/**
	 * websocket 메세지 수신 및 송신
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 메시지에서 페이로드(본문)를 가져오는 메소드로 전달받은 메시지를 문자열 형태의 페이로드로 반환
		String msg = message.getPayload();

		// Json객체 → Java객체로 변환하기
		// 출력값 : [chatNo=0, chatMsg=안녕, chatDate=2024/09/19 20:02, chatFile=null, userNo=1, 
		//			roomNo=20, userName=일식이, unReadCount=0, type=msg]
		ChatVO chatMessage = objectMapper.readValue(msg, ChatVO.class);
		
		int roomNo = chatMessage.getRoomNo(); // 채팅방 번호
		int userNo = getUserNo(session); // userNo
		
		// 해당 유저의 채팅방 리스트 가져오기
		List<ChatRoomVO> list = chatService.getRoomList(userNo);

		
		// ********** 방에 입장할 때 **********
		// 선택한 채팅방 입장하기
		if(chatMessage.getType().equals("enter-room")) {
			
			//------------------방에 세션 추가하기 전에 기존에 들어가 있던 방에서 세션 삭제---------------

			// 해당 유저가 속해있는 방이 존재하는 경우만 실행
			if(list != null) {
				for (ChatRoomVO roomVO : list) {
						// roomList에 내가 속해있는 모든방에 나의 session 삭제
						roomList.get(roomVO.getRoomNo()).remove(session);
				}
				roomList.get(roomNo).add(session); // 방에 들어온 유저 세션리스트에 세션 추가
			}
			//------------------------------------------------------------------------------
			
			// 해당 RoomList(채팅방)에 들어온 사람들을 전부 하나씩 돌려서 본인의 안읽음 메시지를 읽음으로 처리하기
			chatMessage.setUserNo(userNo);
			
			// 채팅방 입장시, 해당 방에 있는 본인의 안읽은 채팅 메시지 목록 가져오기
			List<Integer> chatNoList = chatService.getUnreadCntByUser(chatMessage);
			
			for (Integer chatNo : chatNoList) {
				// chatMessage에 userNo와 읽지 않은 chatNo를 세팅해서 넘긴 후, 
				// unreadchat 테이블에서 해당 userNo와 chatNo 삭제 (방에 들어왔으니 읽음 처리하기)
				chatMessage.setChatNo(chatNo);
				
				// 채팅방 입장시, 해당 방에 있는 본인의 안읽은 채팅 메시지 목록을 테이블에서 삭제하기
				chatService.readMessageInRoom(chatMessage);
			}
			
			// * 2 메세지 수신 (웹소켓 서버로부터 메시지를 수신하는 함수)
			// 현재 들어와 있는 모든 세션에게 reload 메시지 전송 (메시지 채팅방목록 ajax 다시 뿌리기 위함)
			TextMessage tMsg = new TextMessage("reload");
			for(WebSocketSession sess : sessionList) {
				sess.sendMessage(tMsg);
			}
			
			
		// ********** 채팅 메세지 입력 시 **********
		} else if (roomList.get(roomNo) != null && chatMessage.getType().equals("msg")) {
	        LocalDateTime currentDateTime = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"); 
	        String formattedDateTime = currentDateTime.format(formatter);
	        chatMessage.setChatDate(formattedDateTime);
	        
			// DB에 메시지 등록
	        log.info("chatMessage >>>>>" + chatMessage.toString());
			chatService.insertMessage(chatMessage);
		
			// 현재 session 수
			int sessionCount = 0;
	
			for (WebSocketSession sess : roomList.get(roomNo)) {
				sessionCount++;
			}
			
			// 해당 채팅방 인원수 가져오기
			int memCnt = chatService.getMemberCount(chatMessage.getRoomNo());
			chatMessage.setUnReadCount(memCnt);
			
			// 메세지에 이름, 아이디, 내용을 담는다.
			TextMessage textMessage = new TextMessage(chatMessage.getUserName() + "," + chatMessage.getUserNo() + ","
					+ chatMessage.getChatMsg() + "," + formattedDateTime + "," + (chatMessage.getUnReadCount()-sessionCount));
				
			// 해당 채팅방에 속한 모든 세션에게 메시지 전송함
			// 해당 RoomList에 들어온 사람이 2명이면 sessionCount = 2;
			for (WebSocketSession sess : roomList.get(roomNo)) {
				MemberVO memVO = userSessionMap.get(sess);
				chatMessage.setUserNo(userNo);
				
				// 해당 방에 채팅 메시지 안읽은 개수 가져오기
				List<Integer> chatNoList = chatService.getUnreadCntByUser(chatMessage);
				for (Integer chatNo : chatNoList) {
					// chatMessage에 userNo와 읽지 않은 chatNo를 세팅해서 넘긴 후, 
					// unreadchat 테이블에서 해당 userNo와 chatNo 삭제 (방에 들어왔으니 읽음 처리하기)
					chatMessage.setChatNo(chatNo);
					chatService.readMessageInRoom(chatMessage);
				}
				sess.sendMessage(textMessage);
			}
			
			System.out.println(roomNo + "방에 들어온 세션 수 >>> " + sessionCount);
	
			// 메시지 전송 시 마다 현재 들어와 있는 모든 세션에게 reload 메시지 전송 (메시지 채팅방목록 ajax 다시 뿌리기 위함)
			TextMessage tMsg = new TextMessage("reload");
			for(WebSocketSession sess : sessionList) {
				sess.sendMessage(tMsg);
			}
		}
		
		// 채팅방 닫기 버튼 누를 시
		else if(chatMessage.getType().equals("close-room")) {
			// roomList에 내가 속해있던 방에 나의 session 삭제
			roomList.get(roomNo).remove(session);
		} 
		
		// 채팅방 나가기 버튼 누를 시
		else if(chatMessage.getType().equals("exit-room")) {
			// roomList에 내가 속해있던 방에 나의 session 삭제
			roomList.get(roomNo).remove(session);
			
			// 해당 채팅방의 참여 멤버에서 본인 지우기
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("roomNo", roomNo);
			map.put("userNo", userNo);
			chatService.deleteRoomMemberMe(map);
		} 
		
	}

	// 웹소켓으로 HttpSession에 있는 userNo 가져오기
	private int getUserNo(WebSocketSession session) {
		Map<String, Object> httpSession = session.getAttributes();
		MemberVO memberVO = (MemberVO) httpSession.get("SessionInfo");

		return memberVO.getUserNo();
	}
		
}
