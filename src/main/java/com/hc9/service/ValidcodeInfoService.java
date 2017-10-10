package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.entity.Validcodeinfo;
import com.hc9.dao.impl.HibernateSupport;

/**
 * Validcodeinfo 的CRUD操作
 * @author frank
 *
 */
@Service
public class ValidcodeInfoService {

    /**
     * 数据库操作通用接口
     */
    @Resource
    private HibernateSupport commonDao;


    /**
    * <p>Title: getValidcodeinfoByUid</p>
    * <p>Description: 得到用户的短信限制信息</p>
    * @param id  用户编号
    * @return 限制信息对象
    */
    public Validcodeinfo getValidcodeinfoByUid(Long id){
        StringBuffer sb=new StringBuffer("select * from validcodeinfo where user_id=").append(id);
        List<Validcodeinfo> validList= commonDao.findBySql(sb.toString(), Validcodeinfo.class);
        return validList.size()>0?validList.get(0):null;
    }
    
    /**
    * <p>Title: update</p>
    * <p>Description: 修改对象信息</p>
    * @param validcodeinfo  对象
    */
    public void update(Validcodeinfo validcodeinfo){
        commonDao.update(validcodeinfo);
    }
    /**
     * 新增一条记录
     * @param vali
     */
	public void save(Validcodeinfo vali) {
		commonDao.save(vali);
		
	}
    
}
