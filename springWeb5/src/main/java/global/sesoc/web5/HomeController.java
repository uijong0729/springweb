package global.sesoc.web5;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 메인 콘트롤러
 */
@Controller
public class HomeController {
	/**
	 * 메인 화면으로 이동
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		return "home";
	}
	
}
