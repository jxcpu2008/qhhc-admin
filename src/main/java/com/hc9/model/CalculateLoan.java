package com.hc9.model;

import java.math.BigDecimal;

/**
 * 借贷工具，等额本息计算
 * 
 * @author Administrator
 * 
 */
public class CalculateLoan {

    /**
     * 还款期数
     */
    private int count;

    /**
     *  回购本金
     */
    private BigDecimal benjin;

    /**
     * 利息
     */
    private BigDecimal lixi;

    /**
     *  未归还本金
     */
    private BigDecimal notReturn;

    /**
    * <p>Title: getCount</p>
    * <p>Description: </p>
    * @return int
    */
    public int getCount() {
        return count;
    }

    /**
    * <p>Title: setCount</p>
    * <p>Description: </p>
    * @param count 还款期数
    */
    public void setCount(int count) {
        this.count = count;
    }

    /**
    * <p>Title: getBenjin</p>
    * <p>Description: </p>
    * @return BigDecimal
    */
    public BigDecimal getBenjin() {
        return benjin;
    }

    /**
    * <p>Title: setBenjin</p>
    * <p>Description: </p>
    * @param benjin 回购本金
    */
    public void setBenjin(BigDecimal benjin) {
        this.benjin = benjin;
    }

    /**
    * <p>Title: getLixi</p>
    * <p>Description: </p>
    * @return BigDecimal
    */
    public BigDecimal getLixi() {
        return lixi;
    }

    /**
    * <p>Title: setLixi</p>
    * <p>Description: </p>
    * @param lixi 利息
    */
    public void setLixi(BigDecimal lixi) {
        this.lixi = lixi;
    }

    /**
    * <p>Title: getNotReturn</p>
    * <p>Description: </p>
    * @return BigDecimal
    */
    public BigDecimal getNotReturn() {
        return notReturn;
    }

    /**
    * <p>Title: setNotReturn</p>
    * <p>Description: </p>
    * @param notReturn 未归还本金
    */
    public void setNotReturn(BigDecimal notReturn) {
        this.notReturn = notReturn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = benjin.longValue();
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + count;
        temp = lixi.longValue();
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = notReturn.longValue();
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CalculateLoan other = (CalculateLoan) obj;
        if (benjin.doubleValue() != Double.doubleToLongBits(other.benjin
                .doubleValue()))
            return false;
        if (count != other.count)
            return false;
        if (lixi.doubleValue() != other.lixi.doubleValue())
            return false;
        if (notReturn.doubleValue() != other.notReturn.doubleValue())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CalculateLoan [benjin=" + benjin + ", count=" + count
                + ", lixi=" + lixi + ", notReturn=" + notReturn + "]";
    }

}
