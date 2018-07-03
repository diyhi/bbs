package cms.service.statistic.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.statistic.PV;
import cms.service.besa.DaoSupport;
import cms.service.statistic.PageViewService;

/**
 * 页面访问量
 *
 */
@Service
@Transactional
public class PageViewServiceBean extends DaoSupport<PV> implements PageViewService{

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
	public void deletePageView(Date endTime){
		Query query = em.createQuery("delete from PV o where o.times<:date")
				.setParameter("date", endTime);
			query.executeUpdate();

	}
	
}
