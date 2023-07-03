package cms.web.action.report;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;
import cms.bean.report.ReportType;

/**
 * 举报分类组件
 *
 */
@Component("reportTypeManage")
public class ReportTypeManage {
	
    /**
	 * 分类放入分类中(递归)
	 * @param new_reportType 新分类
	 * @param reportTypeList 未存入子分类集合
	 * @return
	*/
	public void childReportType(ReportType new_reportType,List<ReportType> reportTypeList){
		if(new_reportType != null && new_reportType.getChildNodeNumber() >0){
			for(ReportType reportType : reportTypeList){
				if(reportType.getParentId().equals(new_reportType.getId())){
					new_reportType.addChildType(reportType);  
			    	this.childReportType(reportType, reportTypeList);
			    	
			    }   
			}
		}
	}
	/**
	 * 举报分类排序(递归)
	 * @param reportTypeList 已存入子分类集合
	 * @return
	*/
	public void reportTypeSort(List<ReportType> reportTypeList){
		if(reportTypeList != null && reportTypeList.size() >0){
			this.reportTypeItemSort(reportTypeList);
			for(ReportType reportType : reportTypeList){
				List<ReportType> childReportType = reportType.getChildType();
				if(childReportType != null && childReportType.size() >0){
					this.reportTypeSort(childReportType);
				}
			}
		}
	}
	/**
	 * 举报分类项排序
	 * @param reportTypeList
	 */
	private void reportTypeItemSort(List<ReportType> reportTypeList){
		Collections.sort(reportTypeList, new Comparator<ReportType>(){
			@Override
			public int compare(ReportType o1, ReportType o2) {
				long s_1 = o1.getSort();
				long s_2 = o2.getSort();
				if(s_1 <s_2){
        			return 1;
        			
        		}else{
        			if(s_1 == s_2){
            			return 0;
            		}else{
            			return -1;
            		}
        		}  
			}   
		});
	}
}
