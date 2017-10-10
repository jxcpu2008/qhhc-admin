package com.hc9.service;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.Adminuser;
import com.hc9.dao.entity.LoansignType;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;


/**
* <p>Title:LoansignTypeService</p>
* <p>Description: 标的类型服务层</p>
* <p>Company: 前海红筹</p>
*/
@Service
public class LoansignTypeService {

    /** 注入数据库服务层*/
    @Resource
    private HibernateSupport dao;

    
    /**
    * <p>Title: queryPage</p>
    * <p>Description: 分页查询 </p>
    * @param page 分页
    * @param loansignType 查询条件
    * @return   标的类型的集合
    */
    @SuppressWarnings("rawtypes")
    public List queryPage(PageModel page,LoansignType loansignType) {
        List datalist = new ArrayList();
        StringBuffer countsql=new StringBuffer("SELECT count(1) from loansign_type  where 1=1");
        StringBuffer listsql=new StringBuffer("SELECT id,typeName,typeKey,typeTime,typeValue FROM loansign_type   where 1=1");
        if (loansignType.getTypeKey() != null &&loansignType.getTypeKey()!= "") {
        	countsql.append(" AND typeKey  like '"+ loansignType.getTypeKey().toUpperCase().trim()+"%'" );
        	listsql.append(" AND typeKey  like '"+ loansignType.getTypeKey().toUpperCase().trim()+"%'");
		}
        datalist = dao.pageListBySql(page, countsql.toString(), listsql.toString(), null);
        return datalist;
    }
    
    /**
    * <p>Title: addoredit</p>
    * <p>Description: 添加或修改</p>
    * @param loansignType  标的类型
    * @return 是否成功
    */
    public boolean addoredit(LoansignType loansignType,Adminuser loginuser){
            if(null!=loansignType.getId()&&!"".equals(loansignType.getId())){
            	loansignType.setTypeValue(loginuser.getId());
            	loansignType.setTypeTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
                dao.update(loansignType);
            }else{
            	loansignType.setTypeValue(loginuser.getId());
            	loansignType.setTypeTime(DateUtils.format("yyyy-MM-dd HH:mm:ss"));
                dao.save(loansignType);
            }
            return true;
    }
    
    /**
    * <p>Title: delete</p>
    * <p>Description: 删除多个标的类型</p>
    * @param ids  标的编号
    * @return 是否成功
    */
    public boolean delete(String ids){
        if(ids.length()>1){
            ids=ids.substring(0, ids.length()-1);
        }
        StringBuffer sb=new StringBuffer("delete from loansign_type where id in (").append(ids).append(")");
        int result=dao.executeSql(sb.toString());
        return result>0?true:false;
    }
    
    /**
    * <p>Title: queryOne</p>
    * <p>Description: 通过编号查询 </p>
    * @param id 编号
    * @return  标的类型
    */
    public LoansignType queryOne(String id){
        LoansignType loansignType=dao.get(LoansignType.class, Long.valueOf(id));
        return loansignType;
    }
    /**
     * 查询所有的借款标类型
     * @return 借款标集合
     */
    public List<LoansignType> queryLoanType(){
    	List<LoansignType> list = dao.find("from LoansignType");
    	return list;
    }
}
