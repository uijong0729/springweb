package global.sesoc.web5.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 웹 애플리케이션의 공통 예외 처리 클래스
 */

@ControllerAdvice
public class Web5ExceptionHandler {

	/**
	 * 모든 종류의 예외를 처리
	 * @return JSP파일 경로
	 */
	@ExceptionHandler (Exception.class)
	public String errorHandler() {
		return "/error";
	}
}
