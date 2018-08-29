package global.sesoc.web5.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import global.sesoc.web5.dao.CustomerDAO;
import global.sesoc.web5.vo.Customer;

/**
 * 개인정보 수정 콘트롤러
 * 모델에 "customer"라는 이름의 값이 저장될 때, 세션에도 저장
 * 수정단계의 시작부터 마지막단계까지 세션의 값 유지
 */
@Controller
@RequestMapping("customer")
@SessionAttributes("customer")
public class CustomerUpdateController {
	private static final Logger logger = LoggerFactory.getLogger(CustomerUpdateController.class);
	
	@Autowired
	CustomerDAO dao;		//회원 관련 데이터 처리 객체
	
	/**
	 * 회원 정보 수정 폼 보기
	 */
	@RequestMapping (value="update", method=RequestMethod.GET)
	public String updateForm(HttpSession session, Model model) {
		//세션의 로그인ID로 개인정보를 검색하여 모델에 저장
		String id = (String) session.getAttribute("loginId");
		Customer customer = dao.get(id);
	
		model.addAttribute("customer", customer);
		return "customer/updateForm";
	}

	/**
	 * 회원 정보 수정 처리
	 * @param customer updateForm()에서 생성한 VO객체에 사용자가 입력한 수정 정보가 추가된 객체.
	 * 					세션에 해당 값이 없으면 에러.
	 */
	@RequestMapping (value="update", method=RequestMethod.POST)
	public String update(
			@ModelAttribute("customer") Customer customer,
			Model model) {
		
		int result = dao.update(customer);
		if (result != 1) {
			//DB update에 실패한 경우 alert() 출력용 메시지를 모델에 저장
			model.addAttribute("errorMsg", "수정 실패");
			return "customer/updateForm";
		}
		
		return "redirect:updateComplete";
	}

	/**
	 * 회원 정보 수정 처리 완료
	 * @param customer DB에 최종 저장된 정보
	 * @param sessionStatus @SessionAttributes에서 지정한 이름을 세션에서 삭제하기 위해 사용
	 */
	@RequestMapping(value="updateComplete", method=RequestMethod.GET)
	public String updateComplete(
			@ModelAttribute("customer") Customer customer, 
			SessionStatus sessionStatus,
			HttpSession session,
			Model model) {
		
		//수정된 정보를 세션에도 반영 (로그인한 사용자 이름)
		session.setAttribute("loginName", customer.getName());
		
		//수정 처리된 정보를 모델에 저장
		model.addAttribute("result", customer);
		sessionStatus.setComplete();
		
		return "customer/updateComplete";
	}
}
