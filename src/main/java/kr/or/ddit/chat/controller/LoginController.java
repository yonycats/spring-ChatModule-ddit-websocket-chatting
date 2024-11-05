package kr.or.ddit.chat.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.or.ddit.service.ILoginService;
import kr.or.ddit.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
	
	@Inject
	ILoginService loginService;

	@RequestMapping(value = "/login.do", method = RequestMethod.GET)
	public String noticeLogin(Model model) {
		model.addAttribute("bodyText", "login-page");
		return "login";
	}
	
	@PostMapping("/loginCheck.do")
	public String loginCheck(HttpServletRequest req, MemberVO member, Model model) {
		String goPage = "";
		MemberVO memberVO = loginService.loginCheck(member);
		
		if (memberVO != null) {
			log.info("memberVO >>> " + memberVO.toString());
			HttpSession session = req.getSession();
			session.setAttribute("SessionInfo", memberVO);
			
			goPage = "redirect:/chat/chatList.do";	// 채팅방 목록으로 이동
		} else {
			model.addAttribute("message", "로그인 정보를 정확하게 입력해주세요.");
			model.addAttribute("member", member);
			model.addAttribute("bodyText", "login-page");
			goPage = "login";
		}
		return goPage;
	}
	
	@GetMapping("/logout.do")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login.do";
	}
	
}
