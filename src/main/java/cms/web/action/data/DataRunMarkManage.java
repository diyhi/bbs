package cms.web.action.data;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 运行标记管理
 *
 */
@Component("dataRunMarkManage")
public class DataRunMarkManage {
	/**
	 * 查询/添加任务运行标记
	 * @param count 次数  -1为查询方式
	 * @return
	 */
	@Cacheable(value="dataRunMarkManage_cache_taskRunMark",key="'taskRunMark'")
	public Long taskRunMark_add(Long count){
		return count;
	}
	/**
	 * 删除任务运行标记
	 * @return
	 */
	@CacheEvict(value="dataRunMarkManage_cache_taskRunMark",key="'taskRunMark'")
	public void taskRunMark_delete(){
	}
}
