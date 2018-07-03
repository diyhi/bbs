package cms.bean;

import java.io.Serializable;

/**
 * 购物车Cookie对象集合
 * @author Administrator
 *
 */
public class CookieCart implements Serializable{
	private static final long serialVersionUID = 894253658125202698L;
	
	/** 商品样式ID **/
	private Long styleId;
	/** 商品数量 **/
	private Integer quantity = 0;
	/** 是否选择购买 true:选中  false:未选中**/
	private boolean selected = true;

	
	public Long getStyleId() {
		return styleId;
	}
	public void setStyleId(Long styleId) {
		this.styleId = styleId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	


	
}
