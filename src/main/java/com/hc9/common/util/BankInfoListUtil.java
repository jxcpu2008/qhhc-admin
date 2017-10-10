package com.hc9.common.util;

import java.util.ArrayList;
import java.util.List;

import com.hc9.model.BankInfo;
import com.hc9.model.BankList;

/**
 * 将银行字符串拆解成一个银行对象
 * @author RanQiBing 2014-01-23
 *
 */
public class BankInfoListUtil{
	/**
	 * 获取银行列表
	 * @param bankList 银行信息字符串
	 * @return 返回一个银行信息集合
	 */
	public static List<BankInfo> dismantling(BankList bankList){
		//得到银行信息的字符串
		String bank = bankList.getpBankList();
		//根据‘#’拆解成一个数组
		String[] arrayBank = bank.split("#"); 
		//声明一个银行集合对象
		List<BankInfo> list = new ArrayList<BankInfo>();
		for(int i=0;i<arrayBank.length;i++){
			//根据‘|’拆解成一个数组
			String[] arrayBanks=arrayBank[i].split("\\|");
			//创建银行对象
			BankInfo bankInfo = new BankInfo();
			bankInfo.setBankName(arrayBanks[0]);
			bankInfo.setBankAliases(arrayBanks[1]);
			bankInfo.setBankNumber(arrayBanks[2]);
			list.add(bankInfo);
		}
		
		return list;
	}
}
