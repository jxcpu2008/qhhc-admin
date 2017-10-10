package com.hc9.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hc9.dao.HibernateSupportTemplate;
import com.hc9.dao.entity.City;
import com.hc9.dao.entity.Province;


/**
 * 省 市查询通用service
 * @author Administrator
 *
 */
@Service
public class CityInfoService {
    /** dao*/
    @Resource
    private HibernateSupportTemplate dao;

    /**
     * 查询所有的省
     * @return  所有的省的集合
     */
    public List<Province> queryAllProvince(){
        List<Province> provinceList= dao.find("From Province");
        return provinceList;  
    }
    
    /**
    * <p>Title: queryCityByProvince</p>
    * <p>Description: 通过省找到市</p>
    * @param provinceId  省编号
    * @return list
    */
    public List<City> queryCityByProvince(long provinceId){
        StringBuffer sb=new StringBuffer("SELECT * from city where province_id=").append(provinceId);
        List<City> cityList= dao.findBySql(sb.toString(),City.class);
        return cityList;  
    }

	public List<City> findCityOneByName(String name) {
		String sql = "select * from province p,city c where p.id =c.province_id and c.`name`like'%"+name+"%'";
		List <City> city =  dao.findBySql(sql, City.class);
		return city;
	}
    
}
