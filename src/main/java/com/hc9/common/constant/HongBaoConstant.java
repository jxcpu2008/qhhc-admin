package com.hc9.common.constant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class HongBaoConstant {

	/** 红包抽奖相关金额信息:红包奖品：4:￥5; 5：￥7； 6：￥9；7：￥19；8：￥29；9：￥50； */
	private static Map<Integer, Double> redEnvMap = new HashMap<Integer, Double>();
	
	/** 红包最低使用门槛 */
	private static Map<Integer, Long> redEnvLowestRequireMap = new HashMap<Integer, Long>();
	
	static {
		redEnvMap.put(4, 5.00);
		redEnvMap.put(5, 7.00);
		redEnvMap.put(6, 9.00);
		redEnvMap.put(7, 19.00);
		redEnvMap.put(8, 29.00);
		redEnvMap.put(9, 50.00);
		
		redEnvLowestRequireMap.put(5, 1000L);
		redEnvLowestRequireMap.put(7, 5000L);
		redEnvLowestRequireMap.put(9, 10000L);
		redEnvLowestRequireMap.put(10, 10000L);
		redEnvLowestRequireMap.put(19, 20000L);
		redEnvLowestRequireMap.put(20, 20000L);
		redEnvLowestRequireMap.put(25, 25000L);
		redEnvLowestRequireMap.put(29, 30000L);
		redEnvLowestRequireMap.put(30, 30000L);
		redEnvLowestRequireMap.put(35, 35000L);
		redEnvLowestRequireMap.put(40, 40000L);
		redEnvLowestRequireMap.put(45, 45000L);
		redEnvLowestRequireMap.put(50, 50000L);
		redEnvLowestRequireMap.put(60, 60000L);
		redEnvLowestRequireMap.put(70, 70000L);
		redEnvLowestRequireMap.put(80, 80000L);
		redEnvLowestRequireMap.put(100, 150000L);
		redEnvLowestRequireMap.put(120, 180000L);
		redEnvLowestRequireMap.put(150, 220000L);
	}
	
	/** 获取抽奖所送红包的对应金额 */
	public static Double getRedEnvMoney(Integer redEnvKey) {
		return redEnvMap.get(redEnvKey);
	}
	
	/** 获取红包的最低使用金额 */
	public static Double getRedEnvLowestRequireMoney(Double redEvnMoney) {
		BigDecimal bigRedEvnMoney = new BigDecimal(redEvnMoney);
		BigDecimal big = new BigDecimal(redEnvLowestRequireMap.get(bigRedEvnMoney.intValue()));
		return big.doubleValue();
	}
	/**	用户注册 */
	public static final int USER_REG=1;
	/**	用户投资 */
	public static final int USER_INVEST=2;
}
