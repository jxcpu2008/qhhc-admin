package com.hc9.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.constant.enums.ENUM_COLUMN_ISFIXED;
import com.hc9.common.constant.enums.ENUM_SHOW_STATE;
import com.hc9.common.redis.IndexDataCache;
import com.hc9.common.redis.sys.vo.ArticleFullVo;
import com.hc9.common.redis.sys.vo.ArticleVo;
import com.hc9.common.redis.sys.vo.DeputysectionVo;
import com.hc9.common.redis.sys.vo.TopicVo;
import com.hc9.common.redis.sys.web.WebCacheManagerUtil;
import com.hc9.commons.normal.Validate;
import com.hc9.dao.entity.Article;
import com.hc9.dao.entity.Deputysection;
import com.hc9.dao.entity.Topic;
import com.hc9.dao.entity.Uploadfile;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * 栏目管理
 * 
 * @author My_Ascii
 * 
 */
@Service
@SuppressWarnings(value = { "columnservice" })
public class ColumnManageService {

	/*** 注入HibernateSupport */
	@Resource
	HibernateSupport commondao;

	/**
	 * 查询所有一级栏目
	 * 
	 * @return List
	 */
	public List queryAllTopics() {
		return commondao.find("select new Topic(id, name, url) from Topic ");
	}

	/**
	 * 查询所有二级栏目
	 * 
	 * @return List
	 */
	public List queryAllDeputysections() {
		return commondao
				.find("select new Deputysection(id, name) from Deputysection");
	}

	/**
	 * 批量删除
	 * 
	 * @param cls
	 *            要删除的对象
	 * @param ids
	 *            要删除的对象id
	 */
	public void deleteMany(Class cls, String ids) {
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			Long did = Long.valueOf(id[i]);
			commondao.delete(commondao.get(cls, did));
		}
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 *            选中的id
	 * @return boolean
	 */
	public boolean deleteMany(String ids) {
		Deputysection deputy = null;
		boolean isSuccess = true;
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			Long did = Long.valueOf(id[i]);
			deputy = commondao.get(Deputysection.class, did);
			if (deputy.getIsfixed() == ENUM_COLUMN_ISFIXED.UNDELETE.ordinal()
					|| deputy.getIsfixed() == ENUM_COLUMN_ISFIXED.UNUPDATE
							.ordinal()) {
				isSuccess = false;
				break;
			}
			commondao.delete(deputy);
		}
		return isSuccess;
	}

	/**
	 * 查询所有状态为显示的文章
	 * 
	 * @return List
	 */
	public List queryAllArticle(String did, PageModel page) {
		StringBuffer buf = new StringBuffer();
		StringBuffer bufcount = new StringBuffer();
		buf.append(" FROM article a,deputysection d WHERE a.deputysection_id=d.id and a.isShow = 1 "
				+ "and d.id = ? ORDER BY a.createTime DESC");
		bufcount.append("SELECT count(1) " + buf);
		buf.insert(0, "SELECT a.url,a.title,a.context,a.createTime ");

		page.setTotalCount(commondao.queryNumberSql(bufcount.toString(), did)
				.intValue());

		buf.append(" LIMIT ");
		buf.append(
				(page.getPageNum() - Constant.STATUES_ONE)
						* page.getNumPerPage()).append(",");
		buf.append(page.getNumPerPage());
		return commondao.findBySql(buf.toString(), Long.parseLong(did));
	}

	public List queryDecou(Long topId) {

		String sql = " FROM Deputysection a where a.isShow = 1 and a.topic.id=? order by a.topic.id,a.id asc";
		List list = commondao.find(sql, topId);

		return list;
	}

	/**
	 * 查询所有状态为显示并推荐的文章的文章
	 * 
	 * @return List
	 */
	public List queryArticle(String deputyName) {
		StringBuffer sb = new StringBuffer(
				"FROM Article WHERE isShow = 1 and isRecommend = 1 and deputysection.name = ?  ORDER BY createTime DESC ");
		return commondao.find(sb.toString(), deputyName);
	}
	

	/**
	 * 公告详情
	 */
	public Article loadArticle(Long articleId) {
		StringBuffer sb = new StringBuffer(
				"FROM Article WHERE isShow = 1 and isRecommend = 1 and id = ?");
		return (Article) commondao.find(sb.toString(), articleId).get(0);
	}

	/**
	 * 查询媒体报道6条文章
	 * 
	 * @return
	 */
	public List<Object[]> queryArticle() {
		String sql = "SELECT a.title,a.url, a.createTime,d.name "
				+ "from article a INNER JOIN deputysection d on a.deputysection_id=d.id "
				+ "where a.isShow=1 And a.isRecommend=1 And a.deputysection_id=3 ORDER BY a.createTime  DESC LIMIT 0, 7";
		return commondao.findBySql(sql);
	}

	/**
	 * 查询指定一级栏目下面的栏目类型为‘列表’的二级栏目
	 * 
	 * @param id
	 *            (一级栏目的id)
	 * @return List
	 */
	public List<Deputysection> queryDeputyByTid(long id) {
		String sql = "FROM Deputysection d inner join fetch  d.sectiontype inner join fetch d.topic WHERE d.sectiontype.id = 2 and d.topic.id = "
				+ id;
		List<Deputysection> list = commondao.find(sql);
		return list;
	}

	/**
	 * 查询所有栏目类型为‘列表’的二级栏目
	 * 
	 * @return List
	 */
	public List queryAllArticles() {
		String sql = "SELECT id, name FROM deputysection WHERE sectiontype_id = 2";
		return commondao.findBySql(sql);
	}

	/**
	 * 删除文章
	 * 
	 * @param id
	 *            文章id
	 */
	public void deleteArticle(long id) {
		commondao.delete(commondao.get(Article.class, id));
	}

	// //////////////////////////////////////////////////////////**邮件反馈**//////////////////////////////////////////////////////////////////////
	/**
	 * 查询所有反馈邮件
	 * 
	 * @param page
	 *            PageModel
	 * @return List
	 */
	public List queryAllFeedback(PageModel page) {
		List list = new ArrayList();
		list = commondao.pageListByHql(page, "from Feedbackinfo", false);
		return list;
	}

	/**
	 * 查询所有反馈邮件
	 * 
	 * @return List
	 */
	public List queryAllFeedback() {
		List list = new ArrayList();
		StringBuffer sb = new StringBuffer(
				"SELECT a.`name`, a.email, a.Phone, a.context, (SELECT b.typeName FROM feedbacktype b WHERE a.feedbacktype_id = b.id), a.addTime FROM feedbackinfo a");
		list = commondao.findBySql(sb.toString());
		return list;
	}

	/**
	 * 根据id查询上传的文件
	 * 
	 * @param id
	 *            (文件编号)
	 * @return Uploadfile
	 */
	public Uploadfile queryUploadFileById(long id) {
		return commondao.get(Uploadfile.class, id);
	}

	/**
	 * 返回数据时设置需要返回的json对象的属性
	 * 
	 * @param json
	 *            用来存放返回的json数据
	 * @param statusCode
	 *            状态码
	 * @param message
	 *            返回的提示信息
	 * @param navTabId
	 *            页面上弹窗的id
	 * @param callbackType
	 *            回调函数
	 * @return 返回JSONObject对象
	 */
	public JSONObject setJson(JSONObject json, String statusCode,
			String message, String navTabId, String callbackType) {
		json.element("statusCode", statusCode);
		json.element("message", message);
		json.element("navTabId", navTabId);
		if (!callbackType.equals("")) {
			json.element("callbackType", callbackType);
		}
		return json;
	}

	/**
	 * 查询所有的一级栏目，并将其存放到application中
	 * 
	 * @param application
	 *            ServletContext
	 */
	@SuppressWarnings("unchecked")
	public void queryAllTopics(ServletContext application) {

		List<Topic> listTopics = (List<Topic>) commondao.find("FROM Topic a order by a.orderNum asc");

		List<Deputysection> listDeputys = commondao
				.find("FROM Deputysection a where a.isShow = ? and a.sectiontype =2 order by a.topic.id,a.id asc",
						ENUM_SHOW_STATE.TRUE.ordinal());
		application.setAttribute("artList01", getArticleList());
		application.setAttribute("topics", listTopics);
		application.setAttribute("appDeputys", listDeputys);
	}
	/**
	 * 获取文章列表
	 * @return
	 */
	public List<Article> getArticleList(){
		String sql="SELECT * FROM article WHERE isShow=1 AND isRecommend=1 AND deputysection_id=? ORDER BY createTime DESC LIMIT 0,5";
		List<Article> articles = commondao.findBySql(sql, Article.class, 4);
		if(articles != null && articles.size() > 0) {
			List<ArticleVo> articleVoList = new ArrayList<ArticleVo>();
			for(Article article : articles) {
				ArticleVo vo = new ArticleVo();
				vo.setUrl(article.getUrl());
				vo.setTitle(article.getTitle());
				vo.setCreateTime(article.getCreateTime());
				articleVoList.add(vo);
			}
			WebCacheManagerUtil.setWebArticleListToRedis(articleVoList);
		}
		return articles;
	}
	/**
	 * 获取新闻公告2及目录
	 * @return
	 */
	public List<Deputysection> getDeputysectionList(){
		List<Deputysection> listDeputys = commondao
				.findBySql("SELECT * FROM deputysection  where isShow = ? and sectiontype_id =2 ORDER BY topic_id,id",Deputysection.class,1);
		String key="LIST:HC9:NEWS:NAVICATER:DEPUTY";
		IndexDataCache.set(key, listDeputys);
		return listDeputys;
	}

	/**
	 * 根据id查询二级栏目详情
	 * 
	 * @param id
	 *            二级栏目id (二级栏目编号)
	 * @return 返回二级栏目
	 */
	public Deputysection queryDeputyById(long id) {
		return commondao.get(Deputysection.class, id);
	}

	/**
	 * 根据id查询文章详情
	 * 
	 * @param id
	 *            (文章编号)
	 * @return 返回文章
	 */
	public Article queryArticleById(long id) {
		return commondao.get(Article.class, id);
	}

	/**
	 * 查询所有被推荐的文章
	 * 
	 * @param application
	 *            ServletContext
	 */
	public void queryAllArticles(ServletContext application) {
		List<Article> list = commondao
				.find("FROM Article a where a.isRecommend = 1 order by a.id desc");
		application.setAttribute("appArticles", list);
	}

	/**
	 * 重置 application
	 * 
	 * @param application
	 *            ServletContext
	 * @param request
	 *            HttpServletRequest
	 */
	public void resetApplaction(HttpServletRequest request) {
//		queryAllTopics(request.getSession().getServletContext());
//		queryAllArticles(request.getSession().getServletContext());
		//TODO 保存文章的时候更新到缓存
		resetTopicDeputysectionArticle();
	}
	
	public void resetTopicDeputysectionArticle(){
		getArticleList();
		resetTopicToRedis();
		resetDeputysectionToRedis();
		resetArticleToRedis();
	}
	
	public void resetTopicToRedis(){
		String sql="SELECT id,name,orderNum,url,pageTitle FROM topic WHERE isShow=?";
		List<Object[]> topics=commondao.findBySql(sql, 1);
		List<TopicVo> topicVos=new ArrayList<>();
		for(Object[] items:topics){
			TopicVo vo=new TopicVo();
			vo.setId(Long.parseLong(items[0].toString()));
			vo.setName(items[1].toString());
			vo.setOrderNum(items[2]==null?0:Integer.parseInt(items[2].toString()));
			vo.setUrl(items[3]==null?"":items[3].toString());
			vo.setPageTitle(items[4].toString());
			topicVos.add(vo);
		}
		
		WebCacheManagerUtil.setTopicsToRedis(topicVos);
	}
	
	public void resetDeputysectionToRedis(){
		String sql="SELECT id,isRecommend,name,orderNum,pageHTML,pageTitile,url,sectiontype_id,topic_id from deputysection WHERE isShow=?";
		List<Object[]> vos=commondao.findBySql(sql, 1);
		List<DeputysectionVo> deputysectionVos=new ArrayList<>();
		for(Object[] items:vos){
			DeputysectionVo vo=new DeputysectionVo();
			vo.setId(Long.parseLong(items[0].toString()));
			vo.setIsRecommend(Integer.parseInt(items[1].toString()));
			vo.setName(items[2].toString());
			vo.setOrderNum(Integer.parseInt(items[3].toString()));
			vo.setPageHtml(items[4]==null?"":items[4].toString());
			vo.setPageTitile(items[5]==null?"":items[5].toString());
			vo.setUrl(items[6]==null?"":items[6].toString());
			vo.setSectiontype(Integer.parseInt(items[7].toString()));
			vo.setTopic(Long.parseLong(items[8].toString()));
			deputysectionVos.add(vo);
		}
		WebCacheManagerUtil.setDeputysectionToRedis(deputysectionVos);
	}
	
	public void resetArticleToRedis(){
		String sql="SELECT id,deputysection_id,title,isRecommend,context,createTime,url FROM article WHERE isShow=? ORDER BY createTime DESC";
		List<Object[]> vos=commondao.findBySql(sql, 1);
		List<ArticleFullVo> articles=new ArrayList<>();
		for(Object[] items:vos){
			ArticleFullVo vo=new ArticleFullVo();
			vo.setId(Long.parseLong(items[0].toString()));
			vo.setDeputysection_id(Long.parseLong(items[1].toString()));
			vo.setTitle(items[2].toString());
			vo.setIsRecommend(Integer.parseInt(items[3].toString()));
			vo.setContext(items[4].toString());
			vo.setCreateTime(items[5].toString());
			vo.setUrl(items[6].toString());
			articles.add(vo);
		}
		WebCacheManagerUtil.setArticlesToRedis(articles);
	}
	
	/**
	 * 添加对象
	 * 
	 * @param obj
	 *            Object
	 */
	public void addObj(Object obj) {
		commondao.save(obj);
	}

	/**
	 * 添加对象
	 * 
	 * @param obj
	 *            Object
	 */
	public void updateObj(Object obj) {
		commondao.update(obj);
	}

	/**
	 * 查询所有反馈邮件
	 * 
	 * @param page
	 *            PageModel
	 * @return List
	 */
	public List queryAllFeedback(PageModel page, String replyType,
			String replyStatus) {
		List list = new ArrayList();
		List<Object> param = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer("from Feedbackinfo where 1 = 1");
		if (Validate.emptyStringValidate(replyType)) {
			hql.append(" and feedbacktype.id = ?");
			param.add(Integer.valueOf(replyType));
		}
		if (Validate.emptyStringValidate(replyStatus)
				&& !replyStatus.equals("-1")) {
			hql.append(" and replyStatus = ?");
			param.add(Integer.valueOf(replyStatus));
		}
		Object[] params = null;
		if (param.size() > 0) {
			params = param.toArray();
		}
		list = commondao.pageListByHql(page, hql.toString(), false, params);
		return list;
	}
}
