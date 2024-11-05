package kr.or.ddit.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.service.IChatService;
import kr.or.ddit.vo.ChatRoomVO;
import kr.or.ddit.vo.ChatVO;
import kr.or.ddit.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

// 공통모듈 : 실시간 채팅을 위한 웹소켓
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {
	
	@Inject
	private IChatService chatService;	
	
	@GetMapping("/chathome.do")
	public String chathome() {
		log.info("chathome 실행()!");
		return "login";
	}
	
	@GetMapping("/chatList.do")
	public String chatList(HttpSession httpSession, Model model) {
		// 회원 정보 가져오기
		MemberVO memberVO = (MemberVO)httpSession.getAttribute("SessionInfo");
		
		// 로그인 정보가 비어있으면 로그인 페이지로 다시 이동
		if (memberVO == null) {
			return "redirect:/login.do";
		}
		
		model.addAttribute("memberVO", memberVO);
		
		// 사원 목록 가져오기
		List<MemberVO> memberList = chatService.getmemberList();
		model.addAttribute("memberList", memberList);
		
		// 로그인한 유저의 채팅방 리스트 가져오기
		List<ChatRoomVO> roomList = chatService.getRoomList(memberVO.getUserNo());
		
		for (ChatRoomVO chatRoomVO : roomList) {
			log.info("chatRoomVO >>> " + chatRoomVO.toString());
		}
		model.addAttribute("roomList", roomList);
		
		return "/list";
	}
	
	// 채팅방 리스트 다시 가져오기 ajax
	@ResponseBody
	@PostMapping("/chatList.do")
	public ResponseEntity<List<ChatRoomVO>> getChatList(HttpSession session) {
		MemberVO memberVO = (MemberVO)session.getAttribute("SessionInfo");
		
		// 해당 유저가 속해있는 채팅방 가져오기
		List<ChatRoomVO> roomList = chatService.getRoomList(memberVO.getUserNo());
		for (ChatRoomVO chatRoomVO : roomList) {
			log.info("chatRoomVO >>>>>>>>>>>>> " + chatRoomVO.toString());
		}
		
		return new ResponseEntity<List<ChatRoomVO>>(roomList, HttpStatus.OK);
	}
	
	@PostMapping(value = "{roomNo}/chat.do")
	public ResponseEntity<List<ChatVO>> getChatMessage(@PathVariable int roomNo)  {
		List<ChatVO> chatMessage = chatService.getChatMessage(roomNo);
		
		// 유저 번호 넣어서 유저 이름 가져온 다음 vo에 세팅하기
		for (ChatVO chatVO : chatMessage) {
			MemberVO memberVO = chatService.getMember(chatVO.getUserNo()); 
			log.info(memberVO.getUserName());
			
			chatVO.setUserName(memberVO.getUserName());
		}
		
		return new ResponseEntity<List<ChatVO>>(chatMessage, HttpStatus.OK);
	}
	
	@PostMapping(value = "{userNo}/roomAdd.do")
	public ResponseEntity<Map<String, Integer>> addChatRoom(HttpSession httpSession, @PathVariable int userNo)  {
		Map<String, MemberVO> userNoMap = new HashMap<String, MemberVO>();
		
		// 내 user_no와 내가 선택한 user_no 객체 세팅하기
		MemberVO memberVO = (MemberVO)httpSession.getAttribute("SessionInfo");
		userNoMap.put("myUser", memberVO);
		userNoMap.put("selectUser", chatService.getMember(userNo));
		
		// 내가 선택한 멤버와 1대 1 채팅방이 이미 있는지 확인하기
		int roomNo = chatService.checkOneToOneChatRoom(userNoMap);
		
		// 새로운 채팅방인지, 기존의 채팅방인지 flag와 함께 채팅방 번호를 반환할 map
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		if (roomNo == 0) {	// 없다면 1대 1 채팅방 추가하기
			int newRoomNo = chatService.addOneToOneChatRoom(userNoMap);
			map.put("roomNo", newRoomNo);
			map.put("flag", 0);		// 없던 채팅방
			
			return new ResponseEntity<Map<String, Integer>>(map, HttpStatus.OK);
		} else {			// 이미 기존의 1대 1 채팅방이 있다면 해당 채팅방 번호 반환하기
			map.put("roomNo", roomNo);
			map.put("flag", 1);		// 있던 채팅방
			
			return new ResponseEntity<Map<String, Integer>>(map, HttpStatus.OK);
		}
	}
	
	@PostMapping(value = "{chatRoomNo}/room.do")
	public ResponseEntity<ChatRoomVO> getChatRoomOne(@PathVariable int chatRoomNo)  {
		ChatRoomVO ChatRoomVO = chatService.getChatRoomOne(chatRoomNo);
		
		return new ResponseEntity<ChatRoomVO>(ChatRoomVO, HttpStatus.OK);
	}
	
}