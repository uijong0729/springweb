package global.sesoc.web5.controller;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import global.sesoc.web5.dao.CustomerDAO;
import global.sesoc.web5.dao.CustomerMapper;
import global.sesoc.web5.vo.Customer;

/**
 * 회원 로그인, 로그아웃 처리 콘트롤러
 */
@RequestMapping("customer")
@Controller
public class CustomerLoginController {
	private static final Logger logger = LoggerFactory.getLogger(CustomerLoginController.class);

	@Autowired
	CustomerDAO dao;

	/**
	 * 로그인 폼 보기
	 */
	@RequestMapping (value="login", method=RequestMethod.GET)
	public String loginForm() {
		return "customer/loginForm";
	}

	/**
	 * 로그인 처리
	 * @param id 사용자가 입력한 아이디
	 * @param password 사용자가 입력한 비밀번호
	 * @param model Model객체
	 * @param session HttpSession객체
	 */
	@RequestMapping (value="login", method=RequestMethod.POST)
	public String login(
			String id, String password, 
			Model model, HttpSession session) {
		
		Customer customer = dao.get(id);
		
		if (customer != null && customer.getPassword().equals(password)) {
			session.setAttribute("loginId", customer.getCustid());
			session.setAttribute("loginName", customer.getName());
			return "redirect:/";
		}
		else {
			model.addAttribute("errorMsg", "ID 또는 비밀번호가 틀립니다.");
			return "customer/loginForm";
		}
	}
	
	/**
	 * 로그아웃 처리
	 * @param session HttpSession객체
	 */
	@RequestMapping (value="logout", method=RequestMethod.GET)
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}
