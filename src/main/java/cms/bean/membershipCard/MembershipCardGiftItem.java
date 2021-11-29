package cms.bean.membershipCard;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 会员卡赠送项
 *
 */
@Entity
@Table(name="membershipcardgiftitem_0",indexes = {@Index(name="membershipCardGiftItem_idx", columnList="membershipCardGiftTaskId")})
public class MembershipCardGiftItem extends MembershipCardGiftItemEntity implements Serializable{

	private static final long serialVersionUID = 5510904045129099287L;

	
}
