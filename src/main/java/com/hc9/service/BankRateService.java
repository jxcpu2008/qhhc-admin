package com.hc9.service;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.common.constant.Constant;
import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.BankRate;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
 * <p>
 * Title:MenuService</p>
 * <p>
 * Description: 银行利率服务层</p>
 * <p>
 * Company: 前海红筹</p>
 * 
 * @author frank date 2014年2月13日
 */
@Service
public class BankRateService {

    /** 注入数据库服务层*/
    @Resource
    private HibernateSupport dao;

    
    /**
    * <p>Title: querymemberPage</p>
    * <p>Description: 银行利率分页 </p>
    * @param page 分页
    * @return list
    */
    @SuppressWarnings("rawtypes")
    public List querymemberPage(PageModel page) {
        List datalist = new ArrayList();
        StringBuffer countsql=new StringBuffer("SELECT count(1) from bank_rate");
        StringBuffer listsql=new StringBuffer("SELECT id,during,rate*100,time_update from bank_rate");
        datalist = dao.pageListBySql(page, countsql.toString(), listsql.toString(), null);
        return datalist;
    }
    
    /**
     * 编辑
     * @param bankRate 对象
     * @return 成功失败
     */
    public boolean editBankrate(BankRate bankRate){
            bankRate.setTimeUpdate(DateUtils.format(Constant.DEFAULT_DATE_FORMAT));
            bankRate.setRate(bankRate.getRate()/100);
            dao.update(bankRate);
            return true;
      
    }
    
    /**
    * <p>Title: addBankrate</p>
    * <p>Description:  add</p>
    * @param bankRate 对象
    * @return  是否成功
    */ 
    public boolean addBankrate(BankRate bankRate){
            bankRate.setTimeUpdate(DateUtils.format(Constant.DEFAULT_DATE_FORMAT));
            bankRate.setRate(bankRate.getRate()/100);
            dao.save(bankRate);
            return true;
    }
    /**
    * <p>Title: deleteBankrate</p>
    * <p>Description: delete</p>
    * @param ids 删除的集合
    * @return 是否成功
     */ 
    public boolean deleteBankrate(String ids){
            if(ids.length()>1){
                ids=ids.substring(0, ids.length()-1);
            }
            StringBuffer sb=new StringBuffer("delete from bank_rate where id in (").append(ids).append(")");
            int result=dao.executeSql(sb.toString());
            return result>0?true:false;
    }
}
