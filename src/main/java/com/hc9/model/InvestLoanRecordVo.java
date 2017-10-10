package com.hc9.model;

import com.hc9.dao.entity.InterestIncreaseCard;
import com.hc9.dao.entity.Loanrecord;
import com.hc9.dao.entity.RedEnvelopeDetail;

/** 投资记录辅助对象 */
public class InvestLoanRecordVo {
	/** 投资记录 */
	private Loanrecord loanrecord;
	
	/** 投资报文 */
	private String bidinfoXml;
	
	/** 是否使用红包 */
	private boolean redDetail;
	
	/** 奖励红包 */
	private RedEnvelopeDetail redEnvelopeDetail;
	
	/** 是否使用加息券 */
	private boolean addCardId;
	
	/** 所使用的加息券 */
	private InterestIncreaseCard increaseCard;

	public Loanrecord getLoanrecord() {
		return loanrecord;
	}

	public void setLoanrecord(Loanrecord loanrecord) {
		this.loanrecord = loanrecord;
	}

	public String getBidinfoXml() {
		return bidinfoXml;
	}

	public void setBidinfoXml(String bidinfoXml) {
		this.bidinfoXml = bidinfoXml;
	}
	
	public boolean isRedDetail() {
		return redDetail;
	}

	public void setRedDetail(boolean redDetail) {
		this.redDetail = redDetail;
	}

	public RedEnvelopeDetail getRedEnvelopeDetail() {
		return redEnvelopeDetail;
	}

	public void setRedEnvelopeDetail(RedEnvelopeDetail redEnvelopeDetail) {
		this.redEnvelopeDetail = redEnvelopeDetail;
	}

	public boolean isAddCardId() {
		return addCardId;
	}

	public void setAddCardId(boolean addCardId) {
		this.addCardId = addCardId;
	}

	public InterestIncreaseCard getIncreaseCard() {
		return increaseCard;
	}

	public void setIncreaseCard(InterestIncreaseCard increaseCard) {
		this.increaseCard = increaseCard;
	}
}