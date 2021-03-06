package com.forum.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.forum.dao.ContentDao;
import com.forum.dao.SectionDao;
import com.forum.model.Content;
import com.forum.util.ConvertEncoding;

public class ListContentController implements Controller{
	
	private ContentDao cd;
	private SectionDao sd;

	public void setCd(ContentDao cd) {
		this.cd = cd;
	}
	
	public void setSd(SectionDao sd){
		this.sd = sd;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		request.setCharacterEncoding("utf-8");
		HttpSession session = request.getSession();
		
		String strValue = request.getParameter("value");
		int value = Integer.valueOf(strValue);

		int pageNo = 1;// int pageSize=5;
		String strPageNo = request.getParameter("pageNo");
		if (strPageNo != null) {
			pageNo = Integer.parseInt(strPageNo); // 把字符串转换成数字
		}

		ModelAndView mav = new ModelAndView();

		try {
			switch(value){
			case 1:
				String sectionName = ConvertEncoding.changeToUtf(request.getParameter("sectionName"));
				System.out.println("首页传递过来的模块名："+sectionName);
				session.setAttribute("sectionName", sectionName);
				int sectionId = sd.findId(sectionName);
				System.out.println("查询到的模块编号："+sectionId);
				List<Content> contentlist = cd.listContent(pageNo,sectionId);
				for (Content content : contentlist) {
					System.out.println("content return :"+content.getId());
				}
				session.setAttribute("contentlist", contentlist);
				Integer pageCount = new Integer(cd.getPageCount(sectionId));
				request.setAttribute("pageCount", pageCount);
				request.setAttribute("pageNo", pageNo);
				mav.setViewName("user/content");
				break;
			case 2:
				String sectionContent = request.getParameter("search");
				System.out.println("首页传递过来的搜索值："+sectionContent);
				session.setAttribute("sectionContent", sectionContent);
				
				List<Content> contentlist1 = cd.listContent(pageNo,sectionContent);
				for (Content content : contentlist1) {
					System.out.println("content return :"+content.getId());
				}
				session.setAttribute("contentlist", contentlist1);
				Integer pageCount1 = new Integer(cd.getPageCount(sectionContent));
				request.setAttribute("pageCount", pageCount1);
				request.setAttribute("pageNo", pageNo);
				mav.setViewName("user/searchContent");
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}

}
