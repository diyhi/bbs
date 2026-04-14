package cms.repository.statistic.impl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


import cms.model.statistic.PV;
import cms.repository.besa.DaoSupport;
import cms.repository.statistic.PageViewRepository;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * 页面访问量管理接口实现类
 *
 */
@Repository
@Transactional
public class PageViewRepositoryImpl extends DaoSupport<PV> implements PageViewRepository {

	/**
	 * 保存访问量
	 * @param pvList 访问量集合
	 */
	public void savePageView(List<PV> pvList){
		if(pvList != null && pvList.size() >0){
			for(PV pv : pvList){
				this.save(pv);
			}
			
		}
		
	}
	
	/**
	 * 删除访问量
	 * @param endTime 结束时间
	 */
	public void deletePageView(LocalDateTime endTime){
		Query query = em.createQuery("delete from PV o where o.times<:date")
				.setParameter("date", endTime);
			query.executeUpdate();

	}
	
}
