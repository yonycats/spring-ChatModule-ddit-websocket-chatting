package kr.or.ddit.chat.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import kr.or.ddit.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

	// *필수* 로그인한 전체 session 목록 담는 List
	private List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();

	// 해당 userId 와 그에 따르는 session 관리 Map
	private Map<WebSocketSession, String> userSessionMap = new ConcurrentHashMap<WebSocketSession, String>();

	
	/**
	 * websocket 연결 성공 시
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("# 채팅 접속");
		
		String userNo = getUserNo(session);
		userSessionMap.put(session, userNo);
		
		// 채팅에 새로 접속한 사람 세션 리스트에 추가하기
		sessionList.add(session);
		
		// 해당 유저가 속해있는 채팅방 가져오기
		// List<ChatRoomVO> list = getRoomList(userNo);
	
	}

	/**
	 * websocket 연결 종료 시
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
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

		
		// 해당 유저가 속해있는 채팅방 가져오기
		String userId = getUserNo(session); // userId		
		// List<MemberVO> list = getRoomList(userId);
				

		// 현재 들어와 있는 모든 세션에게 브로드 캐스트하는 부분 (메시지 보내기)
		for (WebSocketSession webSocketSession : sessionList) {
			webSocketSession.sendMessage(message);
		}
		
	}

	// 웹소켓으로 HttpSession에 있는 userId 가져오기
	private String getUserNo(WebSocketSession session) {
		Map<String, Object> httpSession = session.getAttributes();
		
		// 로그인했을 때 세션에 세팅해놨던 userVO 정보 가져오기
		MemberVO loginUser = (MemberVO) httpSession.get("user");

		if (loginUser == null) {
			// 사용자가 로그인하지 않은 상태 : session.getId()를 반환하여 클라이언트에 대한 고유 식별자를 제공할 수 있음
			// 코드 내에서 사용 중인지는 모르겠으나 참고용으로 남겨둠
			return session.getId(); // WebSocketSession의 sessionid 반환
		} else {
			return loginUser.getUserNo();	// 로그인한 유저의 userNo 반환
		}
	}

}
