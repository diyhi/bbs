package cms.service.statistic;

import java.util.Date;
import java.util.List;

import cms.bean.statistic.PV;
import cms.service.besa.DAO;

/**
 * 页面访问量
 *
 */
public interface PageViewService extends DAO<PV>{
	/**
	 * 保存访问量
	 * @param pvList 访问量集合
	 */
	public void savePageView(List<PV> pvList);
	/**
	 * 删除访问量
	 * @param endTime 结束时间
	 */
	public void deletePageView(Date endTime);
}
