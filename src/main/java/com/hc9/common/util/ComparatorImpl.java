package com.hc9.common.util;

import java.util.Comparator;

import com.hc9.model.UserVo;
/**
 * 比较Integer
 * @author frank
 *
 */
public class ComparatorImpl implements Comparator<UserVo> {
	
	public int compare(UserVo o1, UserVo o2) {
		UserVo u1=(UserVo) o1;
		UserVo u2=(UserVo) o2;
		int flag=u1.getId().compareTo(u2.getId());
		if(flag==0){
			return u1.getId().compareTo(u2.getId());
		}else{
			return flag;
		}
	}
}