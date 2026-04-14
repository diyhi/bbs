package cms.component.frontendModule;

import cms.component.JsonComponent;
import cms.model.frontendModule.FrontendSettings;
import cms.model.frontendModule.Section;
import cms.repository.frontendModule.FrontendSettingsRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 站点栏目组件
 */
@Component("sectionComponent")
public class SectionComponent {
    @Resource
    FrontendSettingsRepository frontendSettingsRepository;
    @Resource
    JsonComponent jsonComponent;

    /**
     * 获取站点栏目列表
     * @return
     */
    public List<Section> getSectionList(){
        List<Section> sectionList = new ArrayList<>();
        FrontendSettings frontendSettings = frontendSettingsRepository.findFrontendSettings();
        if(frontendSettings != null && frontendSettings.getSectionData() != null && !frontendSettings.getSectionData().isEmpty()){
            List<Section> parsedSections = jsonComponent.toGenericObject(frontendSettings.getSectionData(), new TypeReference< List<Section> >(){});
            if(parsedSections != null){
                sectionList.addAll(parsedSections);
            }
        }
        return sectionList;
    }


    /**
     * 添加栏目
     * @param section 栏目
     * @return
     */
    public Integer addSection(Section section){
        List<Section> sectionList = getSectionList();
        TreeSet<Integer> sectionIdList = this.sectionIdList(sectionList,new TreeSet<Integer>());
        //最大号码
        int maxId = 0;
        if(sectionIdList != null && sectionIdList.size() >0){
            maxId = sectionIdList.last();
        }
        maxId++;
        section.setId(maxId);

        if(section.getParentId() >0){//有父栏目
            this.addSectionIdList(sectionList,section);
        }else{
            sectionList.add(section);
        }

        //栏目排序
        this.sectionSort(sectionList);
        int i = frontendSettingsRepository.updateSection(jsonComponent.toJSONString(sectionList));
        if(i >0){
            return maxId;
        }
        return -1;
    }

    /**
     * 栏目排序
     * @param sectionList 栏目集合
     * @return
     */
    private List<Section> sectionSort(List<Section> sectionList){
        Collections.sort(sectionList, new Comparator<Section>(){
            @Override
            public int compare(Section o1, Section o2) {
                int s1 = o1.getSort();
                int s2 = o2.getSort();
                if(s1 < s2){
                    return 1;
                }else{
                    if(s1 == s2){
                        return 0;
                    }else{
                        return -1;
                    }
                }
            }
        });
        if(sectionList.size() >0){
            for(Section section : sectionList){
                if(section.getChildSection() != null && section.getChildSection().size() >0){
                    sectionSort(section.getChildSection());
                }

            }
        }

        return sectionList;
    }

    /**
     * 修改栏目
     * @param section 栏目
     * @return
     */
    public List<Section> updateSection(Section section){
        List<Section> sectionList = getSectionList();
        List<Section> newSectionList = recursionUpdateSection(sectionList,section);
        //栏目排序
        this.sectionSort(newSectionList);
        String newSectionList_json = jsonComponent.toJSONString(newSectionList);

        int i = frontendSettingsRepository.updateSection(newSectionList_json);
        if(i >0){
            return newSectionList;
        }
        return null;
    }

    /**
     * 删除栏目
     * @param sectionId 栏目Id
     * @return
     */
    public List<Section> deleteSection(Integer sectionId){
        List<Section> sectionList = getSectionList();
        List<Section> newSectionList = recursionDeleteSection(sectionList,sectionId);
        int i = frontendSettingsRepository.updateSection(jsonComponent.toJSONString(newSectionList));
        if(i >0){
            return newSectionList;
        }
        return null;
    }

    /**
     * 递归修改栏目
     * @param sectionList 栏目集合
     * @param newSection 新栏目
     * @return
     */
    private List<Section> recursionUpdateSection(List<Section> sectionList,Section newSection){
        if(sectionList != null && sectionList.size() >0){
            for(Section section : sectionList){
                if(section.getId().equals(newSection.getId())){
                    section.setName(newSection.getName());
                    section.setMultiLanguageExtensionMap(newSection.getMultiLanguageExtensionMap());
                    section.setSort(newSection.getSort());
                    section.setLinkMode(newSection.getLinkMode());
                    section.setUrl(newSection.getUrl());
                    break;
                }

                if(section.getChildSection() != null && section.getChildSection().size() >0){
                    recursionUpdateSection(section.getChildSection(),newSection);
                }

            }
        }
        return sectionList;
    }
    /**
     * 递归删除栏目
     * @param sectionList 栏目集合
     * @param sectionId 栏目Id
     * @return
     */
    private List<Section> recursionDeleteSection(List<Section> sectionList,Integer sectionId){
        if(sectionList != null && sectionList.size() >0){
            for(Section section : sectionList){
                if(section.getId().equals(sectionId)){
                    sectionList.remove(section);
                    break;
                }

                if(section.getChildSection() != null && section.getChildSection().size() >0){
                    recursionDeleteSection(section.getChildSection(),sectionId);
                }

            }
        }
        return sectionList;


    }

    /**
     * 根据Id查询栏目
     * @param sectionId 栏目Id
     * @return
     */
    public Section querySectionById(Integer sectionId){
        List<Section> sectionList = getSectionList();
        if(sectionList != null && sectionList.size() >0){
            return getSection(sectionList,sectionId);
        }
        return null;
    }



    /**
     * 递归获取所有栏目Id
     * @param sectionList 栏目集合
     * @param id 栏目Id
     * @return
     */
    public TreeSet<Integer> sectionIdList(List<Section> sectionList,TreeSet<Integer> id){
        if(sectionList != null && sectionList.size() >0){
            for(Section section : sectionList){
                id.add(section.getId());
                if(section.getChildSection() != null && section.getChildSection().size() >0){
                    sectionIdList(section.getChildSection(),id);
                }

            }
        }
        return id;
    }
    /**
     * 递归添加到栏目
     * @param sectionList 栏目集合
     * @param new_section 新子栏目
     * @return
     */
    private List<Section> addSectionIdList(List<Section> sectionList,Section new_section){
        if(sectionList != null && sectionList.size() >0){
            for(Section section : sectionList){
                if(section.getId().equals(new_section.getParentId())){
                    section.addSection(new_section);
                    break;
                }

                if(section.getChildSection() != null && section.getChildSection().size() >0){
                    addSectionIdList(section.getChildSection(),new_section);
                }

            }
        }
        return sectionList;
    }

    /**
     * 递归获取栏目
     * @param sectionList 栏目集合
     * @param sectionId 栏目Id
     * @return
     */
    private Section getSection(List<Section> sectionList,Integer sectionId){
        Section returnSection = null;
        if(sectionList != null && sectionList.size() >0){

            for(Section section : sectionList){
                if(section.getId().equals(sectionId)){
                    returnSection = section;
                }
                if(returnSection != null){
                    break;
                }
                if(section.getChildSection() != null && section.getChildSection().size() >0){
                    returnSection = getSection(section.getChildSection(),sectionId);
                }

            }
        }
        return returnSection;
    }





    /**
     * 判断URL地址是否合法
     * @param urlString URL地址
     * 1：该正则表达式匹配的字符串必须以http://、https://、ftp://开头；
     * 2：该正则表达式能匹配URL或者IP地址；（如：http://www.baidu.com 或者 http://192.168.1.1）
     * 3：该正则表达式能匹配到URL的末尾，即能匹配到子URL；（如能匹配：http://www.baidu.com/s?wd=a&rsv_spt=1&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg&inputT=1236）
     * 4：该正则表达式能够匹配端口号；
     * @return
     */
    public boolean validURL(String urlString){
        //	String regex = "((http|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&\\%_\\./-~-]*)?" ;
        String regex = "((http|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&\\%_\\./-~-\u4E00-\u9FFF]*)?" ;

        Pattern patt = Pattern. compile(regex);
        Matcher matcher = patt.matcher(urlString);
        boolean isMatch = matcher.matches();
        if (isMatch) {
            return true;
        }else {
            return false;
        }
    }

}
