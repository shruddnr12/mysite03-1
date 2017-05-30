package com.jx372.mysite.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jx372.mysite.service.BoardService;
import com.jx372.mysite.vo.BoardVo;
import com.jx372.mysite.vo.UserVo;
import com.jx372.web.util.WebUtil;

@Controller
@RequestMapping( "/board" )
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	@RequestMapping( "" )
	public String index(
		@RequestParam( value="p", required=true, defaultValue="1") Integer page,
		@RequestParam( value="kwd", required=true, defaultValue="") String keyword,
		Model model ) {
		
		Map<String, Object> map = boardService.getMessageList( page, keyword );
		model.addAttribute( "map", map );
		
		return "board/index";
	}
	
	@RequestMapping( "/view/{no}" )
	public String view( @PathVariable( "no" ) Long no, Model model ) {
		BoardVo boardVo = boardService.getMessage( no );
		model.addAttribute( "boardVo", boardVo );
		
		return "board/view";
	}
	
	@RequestMapping( "/delete/{no}" )
	public String delete(
		HttpSession session, 
		@PathVariable( "no" ) Long boardNo,
		@RequestParam( value="p", required=true, defaultValue="1") Integer page,
		@RequestParam( value="kwd", required=true, defaultValue="") String keyword ) {
		//인증 체크
		UserVo authUser = (UserVo)session.getAttribute( "authUser" ); 
		if(  authUser == null ) {
			return "redirect:/user/login";
		}
		
		boardService.deleteMessage( boardNo, authUser.getNo() );
		
		return "redirect:/board?p=" + page + "&kwd=" + WebUtil.encodeURL( keyword, "UTF-8" );
	}
	
	@RequestMapping( value="/modify/{no}" )	
	public String modify( HttpSession session, @PathVariable( "no" ) Long no, Model model) {
		//인증 체크
		UserVo authUser = (UserVo)session.getAttribute( "authUser" ); 
		if(  authUser == null ) {
			return "redirect:/user/login";
		}

		BoardVo boardVo = boardService.getMessage(no, authUser.getNo() );
		model.addAttribute( "boardVo", boardVo );
		
		return "board/modify";
	}

	@RequestMapping( value="/modify", method=RequestMethod.POST )	
	public String modify(
		HttpSession session,
		@ModelAttribute BoardVo boardVo,
		@RequestParam( value="p", required=true, defaultValue="1") Integer page,
		@RequestParam( value="kwd", required=true, defaultValue="") String keyword ) {
		
		//인증 체크
		UserVo authUser = (UserVo)session.getAttribute( "authUser" ); 
		if(  authUser == null ) {
			return "redirect:/user/login";
		}
		
		boardVo.setUserNo( authUser.getNo() );
		boardService.modifyMessage( boardVo );
		
		return "redirect:/board/view/" + boardVo.getNo() + 
				"?p=" + page + 
				"&kwd=" + WebUtil.encodeURL( keyword, "UTF-8" );
	}
	
	@RequestMapping( value="/write", method=RequestMethod.GET )	
	public String write( HttpSession session ) {
		//인증 체크
		if( session.getAttribute( "authUser" ) == null ) {
			return "redirect:/user/login";
		}
		
		return "board/write";
	}
	
	@RequestMapping( value="/write", method=RequestMethod.POST )	
	public String write(
		HttpSession session,
		@ModelAttribute BoardVo boardVo,
		@RequestParam( value="p", required=true, defaultValue="1") Integer page,
		@RequestParam( value="kwd", required=true, defaultValue="") String keyword ) {
		
		//인증 체크
		UserVo authUser = (UserVo)session.getAttribute( "authUser" ); 
		if(  authUser == null ) {
			return "redirect:/user/login";
		}
		
		boardVo.setUserNo( authUser.getNo() );
		boardService.addMessage( boardVo );
		
		return "redirect:/board";
	}
	
	@RequestMapping( value="/reply/{no}" )	
	public String reply( HttpSession session, @PathVariable( "no" ) Long no, Model model) {
		//인증 체크
		UserVo authUser = (UserVo)session.getAttribute( "authUser" ); 
		if(  authUser == null ) {
			return "redirect:/user/login";
		}

		BoardVo boardVo = boardService.getMessage( no );
		boardVo.setOrderNo( boardVo.getOrderNo() + 1 );
		boardVo.setDepth( boardVo.getDepth() + 1 );
		
		model.addAttribute( "boardVo", boardVo );
		
		return "board/reply";
	}	

}