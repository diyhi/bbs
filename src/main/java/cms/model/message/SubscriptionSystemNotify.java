package cms.model.message;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * 订阅系统通知
 *
 */
@Entity
@Table(name="subscriptionsystemnotify_0",indexes = {@Index(name="subscriptionSystemNotify_1_idx", columnList="systemNotifyId"),@Index(name="subscriptionSystemNotify_2_idx", columnList="userId,status,systemNotifyId")})
public class SubscriptionSystemNotify extends SubscriptionSystemNotifyEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -6898567200600595532L;

}
