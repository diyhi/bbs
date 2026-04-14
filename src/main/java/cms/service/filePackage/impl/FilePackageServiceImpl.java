package cms.service.filePackage.impl;


import cms.component.JsonComponent;
import cms.component.filePackage.FilePackageComponent;
import cms.component.fileSystem.localImpl.LocalFileComponent;
import cms.config.BusinessException;
import cms.dto.filePackage.FilePackage;
import cms.dto.filePackage.FileResource;
import cms.repository.setting.SettingRepository;
import cms.service.filePackage.FilePackageService;
import cms.utils.FileSize;
import cms.utils.FileUtil;
import cms.utils.PathUtil;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 文件打包服务
 */
@Service
public class FilePackageServiceImpl implements FilePackageService {

    @Resource
    FilePackageComponent filePackageComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    LocalFileComponent localFileComponent;
    @Resource
    JsonComponent jsonComponent;

    /**
     * 获取文件打包列表
     */
    public List<FilePackage> getFilePackageList(){
        List<FilePackage> filePackageList = new ArrayList<FilePackage>();

        //模板目录
        String pathDir = "data"+ File.separator+"filePackage"+ File.separator;

        String[] extensions = null;//后缀名{"doc", "pdf"}
        boolean recursive = false;//是否递归
        Collection<File> files = FileUtils.listFiles(new File(PathUtil.defaultExternalDirectory()+File.separator+pathDir), extensions, recursive);


        // 迭代输出
        for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
            File file = iterator.next();

            FilePackage filePackage = new FilePackage();

            filePackage.setFileName(file.getName());
            filePackage.setCreateTime(new Date(file.lastModified()));
            filePackage.setSize(FileSize.conversion(file.length()));

            filePackageList.add(filePackage);


        }

        //排序
        Collections.sort(filePackageList, new ComparatorDate());
        return filePackageList;
    }

    /**
     * 删除压缩文件
     * @param fileName 文件名称
     */
    public void deleteExport(String fileName){
        if(fileName == null || fileName.trim().isEmpty()){
            throw new BusinessException(Map.of("file", "文件不名称不能为空"));
        }
        //替换路径中的..号
        fileName = FileUtil.toRelativePath(fileName);
        //模板文件路径
        String filePath = "data"+ File.separator+"filePackage"+ File.separator+fileName;
        localFileComponent.deleteFile(filePath);
    }

    /**
     * 打包
     * @param idGroup 选中Id
     */
    public void packages(String[] idGroup){
        if(idGroup == null || idGroup.length ==0){
            throw new BusinessException(Map.of("file", "未选择目录或文件"));
        }
        //要压缩的目录或文件
        ConcurrentSkipListSet<String> compressList = new ConcurrentSkipListSet<String>(new StringLengthSort());//线程安全有序集合
        for(int i =0; i< idGroup.length; i++){
            String id = idGroup[i];
            if(id != null && !id.trim().isEmpty()){
                if("|".equals(id.trim())){//如果根目录全选
                    compressList.clear();//清空
                    compressList.add(id.trim());
                    break;
                }
                compressList.add(id.trim());
            }
        }

        if(compressList.size() >1){
            //如果父目录全选，则删除子目录
            Iterator<String> filterIter = compressList.iterator();
            while(filterIter.hasNext()){
                String id = filterIter.next();

                Iterator<String> tempFilterIter = compressList.iterator();
                while(tempFilterIter.hasNext()){
                    String tempId = tempFilterIter.next();

                    //判断开始部分是否与二参数相同。不区分大小写
                    //StringUtils.startsWithIgnoreCase("中国共和国人民", "中国")
                    if(!id.equals(tempId) && Strings.CI.startsWith(tempId, id)){
                        tempFilterIter.remove();
                    }
                }
            }
        }
        filePackageComponent.filePack(compressList);
    }

    /**
     * 查询子目录
     * @param parentId 父Id
     * @return
     */
    public List<FileResource> querySubdirectory(String parentId){
        List<FileResource> fileResourceList = new ArrayList<FileResource>();

        if(parentId == null || parentId.trim().isEmpty()){//根目录
            FileResource fileResource = new FileResource();
            File file = new File(PathUtil.defaultExternalDirectory());
            fileResource.setId("|");//|表示根目录
            fileResource.setParentId("");
            fileResource.setName(file.getName());
            if(file.isDirectory()){//是目录
                fileResource.setLeaf(false);//不是叶子节点
            }else{
                fileResource.setLeaf(true);//是叶子节点
            }
            fileResourceList.add(fileResource);

        }else{
            if("|".equals(parentId.trim())){//遍历根目录
                String path = PathUtil.defaultExternalDirectory();

                File dir = new File(path);
                if(dir.isDirectory()){

                    File[] fs=dir.listFiles();
                    for(File file : fs){
                        FileResource fileResource = new FileResource();
                        fileResource.setId(file.getName());
                        fileResource.setParentId(parentId);
                        fileResource.setName(file.getName());
                        if(file.isDirectory()){//是目录
                            fileResource.setLeaf(false);//不是叶子节点
                        }else{
                            fileResource.setLeaf(true);//是叶子节点
                        }
                        fileResourceList.add(fileResource);

                    }
                }
            }else{
                String path = PathUtil.defaultExternalDirectory()+File.separator+(parentId == null || "".equals(parentId.trim()) ? "" :File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(parentId)));

                File dir = new File(path);
                if(dir.isDirectory()){

                    File[] fs=dir.listFiles();
                    for(File file : fs){
                        FileResource fileResource = new FileResource();
                        fileResource.setId(parentId+"/"+file.getName());
                        fileResource.setParentId(parentId);
                        fileResource.setName(file.getName());
                        if(file.isDirectory()){//是目录
                            fileResource.setLeaf(false);//不是叶子节点
                        }else{
                            fileResource.setLeaf(true);//是叶子节点
                        }
                        fileResourceList.add(fileResource);

                    }
                }

            }

        }
        return fileResourceList;
    }

    /**
     * 按日期排序(新到旧)
     *
     */
    private class ComparatorDate implements Comparator<Object> {

        public int compare(Object obj1, Object obj2) {
            Date begin = ((FilePackage)obj1).getCreateTime();
            Date end = ((FilePackage)obj2).getCreateTime();
            if (begin.before(end)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 按照字符串长度排序(从小到大)
     *
     */
    private class StringLengthSort implements Comparator<Object>{
        public int compare(Object o1,Object o2){
            String s1 = (String)o1;
            String s2 = (String)o2;
            int num =     s1.length() - s2.length();
            //判断字符串长度相同时，根据字典顺序排
            if(num == 0){
                return s1.compareTo(s2);
            }
            else
                return num;
        }
    }
}
