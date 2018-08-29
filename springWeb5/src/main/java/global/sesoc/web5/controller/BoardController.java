package global.sesoc.web5.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import global.sesoc.web5.dao.BoardDAO;
import global.sesoc.web5.util.FileService;
import global.sesoc.web5.util.PageNavigator;
import global.sesoc.web5.vo.Board;
import global.sesoc.web5.vo.Reply;

/**
 * 게시판 콘트롤러
 * 글쓰기, 읽기, 목록, 삭제, 수정
 */
@Controller
@RequestMapping("board")
public class BoardController {
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@Autowired
	BoardDAO dao;		//회원 관련 데이터 처리 객체
	
	//게시판 관련 상수값들
	final int countPerPage = 10;			//페이지 당 글 수
	final int pagePerGroup = 5;				//페이지 이동 그룹 당 표시할 페이지 수
	final String uploadPath = "/boardfile";	//파일 업로드 경로
	
	/**
	 * 글쓰기 폼 보기
	 */
	@RequestMapping (value="write", method=RequestMethod.GET)
	public String writeForm() {
		return "board/writeForm";
	}

	/**
	 * 글 저장 처리
	 * @param board 사용자가 입력한 글 내용
	 */
	@RequestMapping (value="write", method=RequestMethod.POST)
	public String write(
			Board board, 
			MultipartFile upload,
			HttpSession session, 
			Model model) {
		
		//세션에서 로그인한 사용자의 아이디를 읽어서 Board객체의 작성자 정보에 세팅
		String id = (String) session.getAttribute("loginId");
		board.setId(id);
		
		//첨부파일이 있는 경우 지정된 경로에 저장하고, 원본 파일명과 저장된 파일명을 Board객체에 세팅
		if (!upload.isEmpty()) {
			String savedfile = FileService.saveFile(upload, uploadPath);
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
		}
		
		dao.insert(board);
		return "redirect:list";
	}
	
	/**
	 * 글 목록
	 * @param page 현재 페이지. 없으면 1로 처리
	 * @param searchText 검색어. 없으면 ""로 처리
	 */
	@RequestMapping (value="list", method=RequestMethod.GET)
	public String list(
			@RequestParam(value="page", defaultValue="1") int page,
			@RequestParam(value="searchText", defaultValue="") String searchText,
			Model model) {
		logger.debug("page: {}, searchText: {}", page, searchText);
		
		int total = dao.getTotal(searchText);			//전체 글 개수
		
		//페이지 계산을 위한 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total); 
		
		//검색어와 시작 위치, 페이지당 글 수를 전달하여 목록 읽기
		ArrayList<Board> boardlist = dao.listBoard(searchText, navi.getStartRecord(), navi.getCountPerPage());	
		
		//페이지 정보 객체와 글 목록, 검색어를 모델에 저장
		model.addAttribute("boardlist", boardlist);
		model.addAttribute("navi", navi);
		model.addAttribute("searchText", searchText);
		return "board/list";
	}
	
	/**
	 * 글 읽기
	 * @param boardnum 읽을 글번호
	 * @return 해당 글 정보
	 */
	@RequestMapping (value="read", method=RequestMethod.GET)
	public String read(int boardnum, Model model) {
		//전달된 글 번호로 해당 글정보 읽기
		Board board = dao.get(boardnum);
		if (board == null) {
			return "redirect:list";
		}
		
		//해당 글에 달린 리플목록 읽기
		ArrayList<Reply> replylist = dao.listReply(boardnum);
		
		//본문글정보와 리플목록을 모델에 저장
		model.addAttribute("board", board);
		model.addAttribute("replylist", replylist);
		
		return "board/read";
	}
	
	/**
	 * 파일 다운로드
	 * @param boardnum 파일이 첨부된 글 번호
	 */
	@RequestMapping(value = "download", method = RequestMethod.GET)
	public String fileDownload(int boardnum, Model model, HttpServletResponse response) {
		Board board = dao.get(boardnum);
		
		//원래의 파일명
		String originalfile = new String(board.getOriginalfile());
		try {
			response.setHeader("Content-Disposition", " attachment;filename="+ URLEncoder.encode(originalfile, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//저장된 파일 경로
		String fullPath = uploadPath + "/" + board.getSavedfile();
		
		//서버의 파일을 읽을 입력 스트림과 클라이언트에게 전달할 출력스트림
		FileInputStream filein = null;
		ServletOutputStream fileout = null;
		
		try {
			filein = new FileInputStream(fullPath);
			fileout = response.getOutputStream();
			
			//Spring의 파일 관련 유틸
			FileCopyUtils.copy(filein, fileout);
			
			filein.close();
			fileout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 글 삭제
	 * @param boardnum 삭제할 글번호
	 */
	@RequestMapping (value="delete", method=RequestMethod.GET)
	public String delete(int boardnum, HttpSession session) {
		String id = (String) session.getAttribute("loginId");
		
		//삭제할 글 번호와 본인 글인지 확인할 로그인아이디
		Board board = new Board();
		board.setBoardnum(boardnum);
		board.setId(id);
		
		//첨부된 파일이 있는지 먼저 확인
		String savedfile = dao.get(boardnum).getSavedfile();
		
		//글 삭제
		int result = dao.deleteBoard(board);
		
		//글 삭제 성공 and 첨부된 파일이 있는 경우 파일도 삭제
		if (result == 1 && savedfile != null) {
			FileService.deleteFile(uploadPath + "/" + savedfile);
		}
		
		return "redirect:list";
	}
	
	/**
	 * 글 수정 폼으로 이동
	 * @param boardnum 수정할 글번호
	 * @return 해당 번호의 글 정보
	 */
	@RequestMapping (value="edit", method=RequestMethod.GET)
	public String editForm(int boardnum, HttpSession session, Model model) {
		
		Board board = dao.get(boardnum);
		model.addAttribute("board", board);
		return "board/editForm";
	}
	
	/**
	 * 글 수정 처리
	 * @param board 수정할 글 정보
	 */
	@RequestMapping (value="edit", method=RequestMethod.POST)
	public String edit(
			Board board, 
			MultipartFile upload,
			HttpSession session) {
		
		//수정할 글이 로그인한 본인 글인지 확인
		String id = (String) session.getAttribute("loginId");
		Board oldBoard = dao.get(board.getBoardnum());
		if (oldBoard == null || !oldBoard.getId().equals(id)) {
			return "redirect:list";
		}
		
		//수정할 정보에 로그인 아이디 저장
		board.setId(id);
		
		//수정 시 새로 첨부한 파일이 있으면 기존 파일을 삭제하고 새로 업로드
		if (!upload.isEmpty()) {
			//기존 글에 첨부된 파일의 실제 저장된 이름
			String savedfile = oldBoard.getSavedfile();
			//기존 파일이 있으면 삭제
			if (savedfile != null) {
				FileService.deleteFile(uploadPath + "/" + savedfile);
			}
			
			//새로 업로드한 파일 저장
			savedfile = FileService.saveFile(upload, uploadPath);
			
			//수정 정보에 새로 저장된 파일명과 원래의 파일명 저장
			board.setOriginalfile(upload.getOriginalFilename());
			board.setSavedfile(savedfile);
		}
		
		//글 수정 처리
		dao.updateBoard(board);
		//원래의 글읽기 화면으로 이동 
		return "redirect:read?boardnum=" + board.getBoardnum();
	}

	/**
	 * 리플 저장 처리
	 * @param reply 사용자가 입력한 글 내용
	 */
	@RequestMapping (value="replyWrite", method=RequestMethod.POST)
	public String replyWrite(
			Reply reply, 
			HttpSession session, 
			Model model) {
		
		//세션에서 로그인한 사용자의 아이디를 읽어서 Reply객체의 작성자 정보에 세팅
		String id = (String) session.getAttribute("loginId");
		reply.setId(id);
		
		//리플 정보를 DB에 저장
		dao.insertReply(reply);
		
		//읽던 게시글로 되돌아 감
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
	
	/**
	 * 리플 삭제
	 * @param reply 삭제할 리플 번호와 본문 글번호가 전달
	 */
	@RequestMapping (value="replyDelete", method=RequestMethod.GET)
	public String deleteReply(Reply reply, HttpSession session) {
		String id = (String) session.getAttribute("loginId");
		
		//삭제할 글 번호와 본인 글인지 확인할 로그인아이디
		reply.setId(id);
		
		dao.deleteReply(reply);
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
	
	/**
	 * 리플 수정 처리
	 * @param reply 수정할 리플 정보
	 */
	@RequestMapping (value="replyEdit", method=RequestMethod.POST)
	public String replyEdit(
			Reply reply, 
			HttpSession session) {
		
		//삭제할 리플 정보와 본인 글인지 확인할 로그인아이디
		String id = (String) session.getAttribute("loginId");
		reply.setId(id);
		
		//리플  수정 처리
		dao.updateReply(reply);
		//원래의 글읽기 화면으로 이동 
		return "redirect:read?boardnum=" + reply.getBoardnum();
	}
}
