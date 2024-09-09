package kr.or.ddit.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

// 공통모듈 : 실시간 채팅을 위한 웹소켓
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {
	
	@GetMapping("/chathome")
	public String chathome() {
		log.info("chathome 실행()!");
		return "chat";
	}
	
}