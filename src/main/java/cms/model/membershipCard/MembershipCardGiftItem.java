package cms.model.membershipCard;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 会员卡赠送项
 *
 */
@Entity
@Table(name="membershipcardgiftitem_0",indexes = {@Index(name="membershipCardGiftItem_idx", columnList="membershipCardGiftTaskId")})
public class MembershipCardGiftItem extends MembershipCardGiftItemEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 5510904045129099287L;

	
}
