package mvc.controller.bbs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import mvc.model.HBox;
import mvc.service.article.ArticleService;

public class ArticleReplyDeleteController implements CommandHandler{
	ArticleService articleService = new ArticleService();
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {		
		HBox hBox = (HBox)req.getAttribute("hBox");
		
		//삭제 권한 확인 해야함 ( 미구현 )
		
		Boolean result = articleService.deleteArticleReply(hBox);
		if(!result) {
			
		}else {
			
		}
		return null;
	}

}
