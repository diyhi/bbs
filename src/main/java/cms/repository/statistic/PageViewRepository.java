package cms.repository.statistic;

import cms.model.statistic.PV;
import cms.repository.besa.DAO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * 页面访问量管理接口
 *
 */
public interface PageViewRepository extends DAO<PV> {
	/**
	 * 保存访问量
	 * @param pvList 访问量集合
	 */
	public void savePageView(List<PV> pvList);
	/**
	 * 删除访问量
	 * @param endTime 结束时间
	 */
	public void deletePageView(LocalDateTime endTime);
}
