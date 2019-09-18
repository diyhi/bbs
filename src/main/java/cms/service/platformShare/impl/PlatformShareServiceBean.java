package cms.service.platformShare.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.platformShare.TopicUnhidePlatformShare;
import cms.service.besa.DaoSupport;
import cms.service.platformShare.PlatformShareService;

/**
 * 平台分成
 *
 */
@Service
@Transactional
public class PlatformShareServiceBean extends DaoSupport<TopicUnhidePlatformShare> implements PlatformShareService{
	
	
}
