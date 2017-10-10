package com.hc9.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.hc9.common.util.Arith;
import com.hc9.common.util.StringUtil;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.Marketing;

/***
 * 销售客户业绩查询
 * @author lkl
 *
 */
@Service
public class PerformanceService {
	
	@Resource
	private  HibernateSupport dao;
	
	/***
	 * 根据手机号码查询用户
	 * @param phone
	 * @return
	 */
	public  Userbasicsinfo getUserbasicsinfo(String phone){
		String sql="select * from userbasicsinfo where id =(select id from userrelationinfo where phone=?)";
		List<Userbasicsinfo> list=dao.findBySql(sql, Userbasicsinfo.class, phone);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/***
	 * 根据用户Id查询用户总投资额度/注册手机号认购单数
	 * @param userId
	 * @param beginTenderTime
	 * @param endTenderTime
	 * @return
	 */
	public   Object[] getSumTenderMoney(Long userId,String beginTenderTime,String endTenderTime){
		String sql="select sum(l.tenderMoney),count(id) from loanrecord l where l.userbasicinfo_id="+userId+" and l.isSucceed=1 and l.loanSign_id in (select id from loansign where `status`!=9) ";
		          sql+=getCommonTenderTime( beginTenderTime, endTenderTime);
		          Object[] obj = (Object[]) dao.findObjectBySql(sql);
		return   obj != null ? obj : null;
	}
	
	
	/***
	 * 根据用户Id查询其推荐人总投资额度/其推荐人认购单数
	 * @param userId
	 * @param beginTenderTime
	 * @param endTenderTime
	 * @return
	 */
	public  Object[] getSumGeneralizeTenderMoney(Long userId,String beginTenderTime,String endTenderTime){
		String sql="select sum(l.tenderMoney),count(id) from loanrecord l where l.userbasicinfo_id in (select uid from generalize where genuid="+userId+") and l.isSucceed=1 and l.loanSign_id in (select id from loansign where `status`!=9) ";
		          sql+=getCommonTenderTime( beginTenderTime, endTenderTime);
		          Object[] obj = (Object[]) dao.findObjectBySql(sql);
		return   obj != null ? obj : null;
	}
	
	/***
	 * 查询其推荐人数
	 * @param userId
	 * @return
	 */
	public Integer getGeneralizeCountUid(Long userId,String beginTenderTime,String endTenderTime){
		String sql="select COUNT(id) from generalize where genuid="+userId;
		if (StringUtil.isNotBlank(beginTenderTime)) {
			sql+=" and DATE_FORMAT(adddate,'%y-%m-%d') >= DATE_FORMAT('"+beginTenderTime+"','%y-%m-%d')";
		}
		if (StringUtil.isNotBlank(endTenderTime)) {
			sql+=" and DATE_FORMAT(adddate,'%y-%m-%d') <= DATE_FORMAT('"+endTenderTime+"','%y-%m-%d')";
		}
		Object obj = dao.findObjectBySql(sql);
		return   obj != null ? Integer.valueOf(obj.toString()) : 0;
	}
	
	/***
	 * 其推荐认购人数
	 * @param userId
	 * @param beginTenderTime
	 * @param endTenderTime
	 * @return
	 */
	public Integer getGeneralizeCount(Long userId,String beginTenderTime,String endTenderTime){
		String sql="select COUNT(DISTINCT(l.userbasicinfo_id)) from loanrecord l where l.userbasicinfo_id IN (select uid from generalize where genuid="+userId+") and l.isSucceed=1";
		          sql+=getCommonTenderTime( beginTenderTime, endTenderTime);
	      Object obj = dao.findObjectBySql(sql);
		return   obj != null ? Integer.valueOf(obj.toString()) : 0;
	}
	
	/***
	 * 查询注册手机号年化业绩(不包含天标)
	 * @param userId
	 * @return
	 */
	public Double getSumTenderMoneyTypeOne(Long userId,String beginTenderTime,String endTenderTime){
		String sql="select SUM(l.tenderMoney/12*s.remonth) from loanrecord l ,loansign s where l.loanSign_id=s.id and l.userbasicinfo_id="+userId+" and l.isSucceed=1  and s.`status`!=9 and s.type!=3";
		          sql+=getCommonTenderTime( beginTenderTime, endTenderTime);
		Object obj = dao.findObjectBySql(sql);
		return   obj != null ? Arith.round(Double.valueOf(obj.toString()), 2) : 0.00;
	}
	
	/***
	 * 查询注册手机号年化业绩(天标)
	 * @param userId
	 * @return
	 */
	public Double getSumTenderMoneyTypeTwo(Long userId,String beginTenderTime,String endTenderTime){
		String sql="select SUM(l.tenderMoney/360*s.remonth) from loanrecord l ,loansign s where l.loanSign_id=s.id and l.userbasicinfo_id="+userId+" and l.isSucceed=1  and s.`status`!=9 and s.type=3";
		          sql+=getCommonTenderTime( beginTenderTime, endTenderTime);
		Object obj = dao.findObjectBySql(sql);
		return   obj != null ? Arith.round(Double.valueOf(obj.toString()), 2) : 0.00;
	}
	
	/***
	 * 查询其推荐人年化业绩(不包含天标)
	 * @param userId
	 * @return
	 */
	public Double getSumGenTenderMoneyOne(Long userId,String beginTenderTime,String endTenderTime){
		String sql="select SUM(l.tenderMoney/12*s.remonth) from loanrecord l ,loansign s where l.loanSign_id=s.id and l.userbasicinfo_id in (select uid from generalize where genuid="+userId+") and l.isSucceed=1  and s.`status`!=9 and s.type!=3";
		          sql+=getCommonTenderTime( beginTenderTime, endTenderTime);
		Object obj = dao.findObjectBySql(sql);
		return   obj != null ? Arith.round(Double.valueOf(obj.toString()), 2) : 0.00;
	}
	
	/***
	 * 查询其推荐人年化业绩(天标)
	 * @param userId
	 * @return
	 */
	public Double getSumGenTenderMoneyTwo(Long userId,String beginTenderTime,String endTenderTime){
		String sql="select SUM(l.tenderMoney/360*s.remonth) from loanrecord l ,loansign s where l.loanSign_id=s.id and l.userbasicinfo_id in (select uid from generalize where genuid="+userId+" ) and l.isSucceed=1  and s.`status`!=9 and s.type=3";
		          sql+=getCommonTenderTime( beginTenderTime, endTenderTime);
		Object obj = dao.findObjectBySql(sql);
		return   obj != null ? Arith.round(Double.valueOf(obj.toString()), 2) : 0.00;
	}
	
	
	
	
	
	public static String getCommonTenderTime(String beginTenderTime,String endTenderTime){
		String sql="";
		if (StringUtil.isNotBlank(beginTenderTime)) {
			sql+=" and DATE_FORMAT(l.tenderTime,'%y-%m-%d') >= DATE_FORMAT('"+beginTenderTime+"','%y-%m-%d')";
		}
		if (StringUtil.isNotBlank(endTenderTime)) {
			sql+=" and DATE_FORMAT(l.tenderTime,'%y-%m-%d') <= DATE_FORMAT('"+endTenderTime+"','%y-%m-%d')";
		}
		return sql;
	}
	
	
	/**
     * 读取出filePath中的所有数据信息
     * @param filePath excel文件的绝对路径
     */
    public  List<Marketing> getDataFromExcel(HttpServletRequest request,String beginTenderTime,String endTenderTime) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile file = multipartRequest.getFile("uploadFile");
		if (file == null || file.getSize() == 0) {
			return null;
		}
		CommonsMultipartFile mf = (CommonsMultipartFile) file;
		// 取得后缀
		String postfix =  mf.getOriginalFilename().substring(mf.getOriginalFilename().lastIndexOf(".")).toUpperCase();
		// 如果不是图片格式
		if (!postfix.equals(".XLS") && !postfix.equals(".XLSX")) {
			   System.out.println("文件不是excel类型");
	            return null;
		}
		InputStream fis =null;
        Workbook wookbook = null;
        try {
            //获取一个绝对地址的流
              fis = file.getInputStream();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
       	 try {
            	//得到工作簿
	       		  wookbook = new XSSFWorkbook(fis); //2007版
		} catch (Exception e) {
			e.printStackTrace();
			 return null;
		}
     
        //得到一个工作表
        Sheet sheet = wookbook.getSheetAt(0);
        
        //获得表头
        Row rowHead = sheet.getRow(0);
        
        //判断表头是否正确
        if(rowHead.getPhysicalNumberOfCells() != 1){
            System.out.println("表头的数量不对!");
            return null;
        }
        
        //获得数据的总行数
        int totalRowNum = sheet.getLastRowNum();
        
        if(totalRowNum==0){
        	return null;
        }
        //要获得属性
        Object phone ;
        //定义销售业绩list
        List<Marketing> list=new ArrayList<Marketing>();
        //定义excel存取的phone，并去重复
        List<Object> listPhone=new ArrayList<Object>();
       //获得所有数据
        for(int i = 1 ; i <= totalRowNum ; i++) {
            //获得第i行对象
            Row row = sheet.getRow(i);
            //获得获得第i行第0列的 String类型对象
            Cell cell = row.getCell((short)0);
            phone =getRightTypeCell(cell);
            if(phone!=null&&!phone.equals("")){
            	  if(!listPhone.contains(phone)){ //判断是否有重复phone
            		  System.out.println("手机号码："+phone);
            		  listPhone.add(phone);
                	  Userbasicsinfo user=getUserbasicsinfo(String.valueOf(phone));
                	  if(user!=null){
                		    Marketing marketing=new Marketing();
                		    marketing.setPhone(String.valueOf(phone));
                		    marketing.setName(user.getName());
                		    // 用户总投资额度/注册手机号认购单数
                		    Object[] objOne=getSumTenderMoney(user.getId(),beginTenderTime,endTenderTime);
                		    marketing.setLoanRecrodPerformance(objOne[0]==null?0.00:Double.valueOf(objOne[0].toString()));
                		    marketing.setLoanRecordNumber(objOne[1]==null?0:Integer.valueOf(objOne[1].toString()));
                		    //其推荐人总投资额度/其推荐人认购单数
                		    Object[] objTwo=getSumGeneralizeTenderMoney(user.getId(),beginTenderTime,endTenderTime);
                		    marketing.setGenergerPerformance(objTwo[0]==null?0.00:Double.valueOf(objTwo[0].toString()));
                		    marketing.setGenergerLoanRecordNumber(objTwo[1]==null?0:Integer.valueOf(objTwo[1].toString()));
                		    //其推荐人数
                		    Integer genergerCountUid=getGeneralizeCountUid(user.getId(),beginTenderTime,endTenderTime);
                		    marketing.setGenergerCountUid(genergerCountUid);
                		    //其推荐认购人数
                		    Integer genergerNumber=getGeneralizeCount(user.getId(), beginTenderTime, endTenderTime);
                		    marketing.setGenergerNumber(genergerNumber);
                		    //查询注册手机号年化业绩
                		    Double tenderMoneyOne=getSumTenderMoneyTypeOne(user.getId(), beginTenderTime, endTenderTime);
                		    Double tenderMoneyTwo=getSumTenderMoneyTypeTwo(user.getId(), beginTenderTime, endTenderTime);
                		    marketing.setPhonePerformance(Arith.add(tenderMoneyOne, tenderMoneyTwo));
                		    //查询其推荐人年化业绩
                		    Double uidPerformanceOne=getSumGenTenderMoneyOne(user.getId(), beginTenderTime, endTenderTime);
                		    Double uidPerformanceTwo=getSumGenTenderMoneyTwo(user.getId(), beginTenderTime, endTenderTime);
                		    marketing.setUidPerformance(Arith.add(uidPerformanceOne, uidPerformanceTwo));
                		    list.add(marketing);
            	      }
            	  }
            }
        }
        return list;
    }
    
    /**
     *     
     * @param cell 一个单元格的对象
     * @return 返回该单元格相应的类型的值
     */
    public static Object getRightTypeCell(Cell cell){
    
        Object object = null;
        switch(cell.getCellType())
        {
            case Cell.CELL_TYPE_STRING :
            {
                object=cell.getStringCellValue();
                break;
            }
            case Cell.CELL_TYPE_NUMERIC :
            {
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                object=cell.getNumericCellValue();
                break;
            }
                
            case Cell.CELL_TYPE_FORMULA :
            {
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                object=cell.getNumericCellValue();
                break;
            }
            
            case Cell.CELL_TYPE_BLANK :
            {
                cell.setCellType(Cell.CELL_TYPE_BLANK);
                object=cell.getStringCellValue();
                break;
            }
        }
        return object;
    }
}
