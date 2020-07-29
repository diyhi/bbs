package cms.bean.template;

import java.io.Serializable;

/**
 * 领取红包用户
 * @author Gao
 *
 */
public class Forum_RedEnvelopeRelated_ReceiveRedEnvelopeUser  implements Serializable{
	private static final long serialVersionUID = 8304841425448878312L;
	
	/** 版块---红包相关--领取红包用户列表  Id **/
	private String receiveRedEnvelopeUser_id;
	
	/** 显示记录数 **/
	private Integer  receiveRedEnvelopeUser_maxResult;
	/** 排序 10.领取时间新--旧 20..领取时间旧--新 **/
	private Integer receiveRedEnvelopeUser_sort;
	
	public String getReceiveRedEnvelopeUser_id() {
		return receiveRedEnvelopeUser_id;
	}
	public void setReceiveRedEnvelopeUser_id(String receiveRedEnvelopeUser_id) {
		this.receiveRedEnvelopeUser_id = receiveRedEnvelopeUser_id;
	}
	public Integer getReceiveRedEnvelopeUser_maxResult() {
		return receiveRedEnvelopeUser_maxResult;
	}
	public void setReceiveRedEnvelopeUser_maxResult(Integer receiveRedEnvelopeUser_maxResult) {
		this.receiveRedEnvelopeUser_maxResult = receiveRedEnvelopeUser_maxResult;
	}
	public Integer getReceiveRedEnvelopeUser_sort() {
		return receiveRedEnvelopeUser_sort;
	}
	public void setReceiveRedEnvelopeUser_sort(Integer receiveRedEnvelopeUser_sort) {
		this.receiveRedEnvelopeUser_sort = receiveRedEnvelopeUser_sort;
	}
	
	
	
	
}
