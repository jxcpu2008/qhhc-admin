package com.hc9.service;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hc9.common.util.DateUtils;
import com.hc9.dao.entity.Autointegral;
import com.hc9.dao.entity.Manualintegral;
import com.hc9.dao.entity.Repaymentrecord;
import com.hc9.dao.entity.Userbasicsinfo;
import com.hc9.dao.impl.HibernateSupport;
import com.hc9.model.PageModel;

/**
* <p>Title:integralService</p>
* <p>Description: 积分Service</p>
* <p>Company: 前海红筹</p>
* @author frank
* <p>date 2014年2月14日</p>
*/
@Service
public class IntegralService {

    /** dao */
    @Resource
    private HibernateSupport dao;

    /** loanSignQuery */
    @Autowired
    private LoanSignQuery loanSignQuery;

    /** autointegralQuery */
    @Autowired
    private AutointegralQuery autointegralQuery;

    /** borrowersQuery */
    @Autowired
    private BorrowersQuery borrowersQuery;
    
    @Resource
    private BorrowService borrowService;
    

    /**
     * 通过还款记录信息来清空这一个借款标所有实际积分
     * 
     * @param repaymentrecord
     *            还款记录
     * @return 是否成功
     */
    public boolean clearRealityintegral(Repaymentrecord repaymentrecord) {
        StringBuffer sb = new StringBuffer(
                "update autointegral ato set ato.realityintegral=0 where ato.loansign_id=")
                .append(repaymentrecord.getLoansign().getId());
        dao.executeSql(sb.toString());
        return true;
    }

    /**
     * 按时还款
     * 
     * @param repaymentrecord
     *            还款记录
     * @param money
     *            金额
     */
    public void timelyRepaymentIntegral(Repaymentrecord repaymentrecord,
            BigDecimal money) {
        // 先判断该借款标之前有没有逾期的数据
        boolean b = loanSignQuery.isExceed(repaymentrecord.getLoansign()
                .getId());

      /*  Autointegral autointegral = new Autointegral();
        autointegral.setIsover(0);
        autointegral.setLoansign(repaymentrecord.getLoansign());
        int integtal = autointegralQuery.calculationIntegral(money
                .doubleValue());
        autointegral.setPredictintegral(integtal);
        autointegral.setRealityintegral(b == true ? integtal : 0);
        autointegral.setRepaymentrecord(repaymentrecord);
        autointegral.setUserbasicsinfo(repaymentrecord.getLoansign()
                .getUserbasicsinfo());
        // 计算用户的积分总和
        int sumInte = autointegralQuery.queryAllIntegral(autointegral
                .getUserbasicsinfo()) + autointegral.getRealityintegral();
        dao.save(autointegral);

        Borrowersbase borrowersbase = borrowersQuery
                .queryBorrowersbaseByUserfundId(autointegral
                        .getUserbasicsinfo().getId());
        borrowersbase.setSuminte(sumInte);*/
       // dao.update(borrowersbase);
    }

    /**
     * 未按时还款
     * 
     * @param repaymentrecord
     *            还款记录
     * @param money
     *            金额
     */
    public void overdueRepaymentIntegral(Repaymentrecord repaymentrecord,
            BigDecimal money) {

      /*  Autointegral autointegral = new Autointegral();
        autointegral.setIsover(1);
        autointegral.setLoansign(repaymentrecord.getLoansign());
        int integtal = autointegralQuery.calculationIntegral(money
                .doubleValue());
        autointegral.setPredictintegral(integtal);
        autointegral.setRealityintegral(0);
        autointegral.setRepaymentrecord(repaymentrecord);
        autointegral.setUserbasicsinfo(repaymentrecord.getLoansign()
                .getUserbasicsinfo());
        dao.save(autointegral);

        clearRealityintegral(repaymentrecord);

        Borrowersbase borrowersbase = borrowersQuery
                .queryBorrowersbaseByUserfundId(autointegral
                        .getUserbasicsinfo().getId());
        borrowersbase.setSuminte(autointegralQuery
                .queryAllIntegral(autointegral.getUserbasicsinfo()));
        dao.update(borrowersbase);*/
    }
    
    /**
     * <p>Title: queryByuserId</p>
     * <p>Description: 查询用户的自动积分</p>
     * @param page 分页
     * @param id 编号
     * @return  集合
     */
     public List queryByuserId(PageModel page,String bid){
         
         StringBuffer sqlbuffer=new StringBuffer("SELECT ato.id, ato.loansign_id, re.periods, ato.realityintegral, ato.Isover,ROUND(re.realMoney+re.money,2),");
         sqlbuffer.append(" ato.predictintegral,(SELECT loanNumber from loansignbasics where id=ato.loansign_id) FROM autointegral ato, borrowersbase bb, repaymentrecord re ");
         sqlbuffer.append("WHERE ato.user_id = bb.userbasicinfo_id AND ato.repayment_id = re.id AND bb.id = ").append(bid);

         
         StringBuffer sqlcount=new StringBuffer("SELECT count(0) FROM autointegral ato, borrowersbase bb, repaymentrecord re ");
         sqlcount.append(" WHERE ato.user_id = bb.userbasicinfo_id AND ato.repayment_id = re.id AND bb.id = ").append(bid);
         
        return  dao.pageListBySql(page, sqlcount.toString(), sqlbuffer.toString(),null);
     }

     /**
      * <p>Title: saveOrUpdateManualinte</p>
      * <p>Description: 保存或修改手动积分信息</p>
      * @param manualin  手动积分信息
      * @param amountpoints 手动积分总和
      * @return 是否成功
      */
      public boolean saveOrUpdateManualinte(Manualintegral manualin,int amountpoints){
          
        //通过用户的编号进行查询是否有该借款人的手动积分信息
          Manualintegral manualintegral=autointegralQuery.queryManuaByuser(manualin.getUserbasicsinfo());
          if(manualintegral==null){
              manualintegral=new Manualintegral();
          }
          manualintegral.setAmountPoints(amountpoints);
          manualintegral.setBankWaterPoints(manualin.getBankWaterPoints());
          manualintegral.setCkVaule(manualin.getCkVaule());
          manualintegral.setCreditCardPoints(manualin.getCreditCardPoints());
          manualintegral.setHouseCardPoints(manualin.getHouseCardPoints());
          manualintegral.setSalesContractInvoicePoints(manualin.getSalesContractInvoicePoints());
          manualintegral.setSocialPoints(manualin.getSocialPoints());
          manualintegral.setUserbasicsinfo(manualin.getUserbasicsinfo());
          if(null!=manualintegral.getId()){
              dao.update(manualintegral);
          }else{
              dao.save(manualintegral);
          }
          int autoint=autointegralQuery.queryAutoSUMIntegral(manualin.getUserbasicsinfo())+amountpoints;
          borrowService.updateinteger(autoint, manualin.getUserbasicsinfo().getId());
          
          return true;
      }
     /**
      * 保存用户积分
      * @return
      */
     public boolean savePoints(String content,int value, Userbasicsinfo user){
    	 try{
	    	 Autointegral autointegral=new Autointegral();
	    	 autointegral.setContent(content);
	    	 autointegral.setRealityintegral(value);
	    	 autointegral.setGetintegraltime(DateUtils.formatSimple());
	    	 autointegral.setUserbasicsinfo(user);
	    	 dao.save(autointegral);
	    	 return true;
    	 }catch(Exception exception){
    		 return false;
    	 }
    	 
      }
     
     public void addPoints(String value){
    	 
     }
}
