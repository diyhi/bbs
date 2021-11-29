package cms.service.data.impl;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.data.ConstraintProperty;
import cms.bean.data.FieldProperties;
import cms.bean.data.TableInfoObject;
import cms.bean.data.TableProperty;
import cms.service.besa.DaoSupport;
import cms.service.data.DataService;
import cms.utils.HexConversion;
import cms.utils.ObjectConversion;
import cms.web.action.SystemException;
import cms.web.action.data.DataRunMarkManage;

/**
 * MySQL备份/还原管理
 *
 */

@Service
public class MySqlDataServiceBean extends DaoSupport implements DataService {
	
	private static final Logger logger = LogManager.getLogger( MySqlDataServiceBean.class);
	
	@Resource JdbcTemplate jdbcTemplate;
	@Resource DataSource dataSource;
	@Resource LobHandler lobHandler;
	@Resource DataRunMarkManage dataRunMarkManage;
	
	/**
	 * 查询数据库版本
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public String findDatabaseVersion(){
		String version = null;
		Query query = em.createNativeQuery("select version();");
		List<Object> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object object = objectList.get(o);
				version = ObjectConversion.conversion(object, ObjectConversion.STRING);
			}
		}
		return version;
	}
	
	/**
	 * 查询所有表信息
	 * @param formsgroupid
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<TableInfoObject> findTable(){
		DatabaseMetaData databaseMetaData;
		Connection  conn = null;
		ResultSet rs = null;
		List<TableInfoObject> tableInfoObjectList = new ArrayList<TableInfoObject>();
		String databaseName = "";//数据库名称
		try {
			conn = DataSourceUtils.getConnection(dataSource);//spring的jdbc template中的datasource 
			databaseMetaData = conn.getMetaData();
			databaseName = conn.getCatalog();
			//获取所有表
			rs = databaseMetaData.getTables(databaseName, "%", "%", new String[]{"TABLE"});
			
			while(rs.next()){ 
				TableInfoObject tableInfoObject = new TableInfoObject();
				tableInfoObject.setName(rs.getString("TABLE_NAME"));
				tableInfoObject.setDataSize(this.tableSize(databaseName,tableInfoObject.getName()));
				tableInfoObject.setIndexSize(this.indexSize(databaseName,tableInfoObject.getName()));
				tableInfoObject.setRows(this.findCount(tableInfoObject.getName()));
				tableInfoObjectList.add(tableInfoObject);
			} 
			rs.close();
		} catch (CannotGetJdbcConnectionException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("查询所有表信息",e);
	        }
		} catch (SQLException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("查询所有表信息",e);
	        }
		}finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("查询所有表信息",e);
			        }
				}
			}
			DataSourceUtils.releaseConnection(conn,dataSource);
		}
		return tableInfoObjectList;
	}
	/**
	 * 数据库表大小
	 * @param name 表名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	private String tableSize(String databaseName,String tableName){
		String size = "";
		Connection  conn  = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn  = DataSourceUtils.getConnection(dataSource);//这里自己写得到Connection,我用的是spring的jdbc template中的datasource 
		//	String sql="SELECT sum(DATA_LENGTH)+sum(INDEX_LENGTH)FROM information_schema.TABLES where TABLE_SCHEMA='itcast'";
		//	String sql="SELECT concat(round(sum(DATA_LENGTH/1024/1024),2),'MB') as data FROM information_schema.TABLES WHERE table_schema='"+databaseName+"' AND table_name='"+tablName+"'";
			//SHOW TABLE STATUS FROM `itcast` like 'orders'
			String sql="SELECT DATA_LENGTH FROM information_schema.TABLES WHERE TABLE_SCHEMA=? AND TABLE_NAME=?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, databaseName);
			pstmt.setString(2, tableName);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				size = rs.getString(1);
			}
			
		} catch (CannotGetJdbcConnectionException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("数据库表大小",e);
	        }
		} catch (SQLException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("数据库表大小",e);
	        }
		}finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("数据库表大小",e);
			        }
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("数据库表大小",e);
			        }
				}
			}
			
		}
		
		return size;
	}
	/**
	 * 数据库表索引大小
	 * @param name 表名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	private String indexSize(String databaseName,String tableName){
		String size = "";
		Connection  conn  = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn  = DataSourceUtils.getConnection(dataSource);//这里自己写得到Connection,我用的是spring的jdbc template中的datasource 
		//	String sql="SELECT sum(DATA_LENGTH)+sum(INDEX_LENGTH)FROM information_schema.TABLES where TABLE_SCHEMA='itcast'";
		//	String sql="SELECT concat(round(sum(DATA_LENGTH/1024/1024),2),'MB') as data FROM information_schema.TABLES WHERE table_schema='"+databaseName+"' AND table_name='"+tablName+"'";
			//SHOW TABLE STATUS FROM `itcast` like 'orders'
			String sql="SELECT INDEX_LENGTH FROM information_schema.TABLES WHERE TABLE_SCHEMA=? AND TABLE_NAME=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, databaseName);
			pstmt.setString(2, tableName);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				size = rs.getString(1);
			}
			
		} catch (CannotGetJdbcConnectionException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("数据库表索引大小",e);
	        }
		} catch (SQLException e) {
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("数据库表索引大小",e);
	        }
		}finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("数据库表索引大小",e);
			        }
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("数据库表索引大小",e);
			        }
				}
			}
			
		}
		
		return size;
	}
	
	
	
	/**
	 * 根据表名查询字段属性
	 * @param tableName 表名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public TableProperty findFieldBytableName(String tableName){
		
		TableProperty tableProperty = new TableProperty();
		
	//	List<FieldProperties> tempFieldPropertiesList = new ArrayList<FieldProperties>();//临时存放字段约束
		

		Connection  conn  = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String databaseName = "";
		
		int numberOfColumns = 0;////获取字段数 
		try {
			conn  = DataSourceUtils.getConnection(dataSource);
			
			DatabaseMetaData  dbmd = conn.getMetaData();//DatabaseMetaData类提供多种方法获得数据库的一些信息。 
			
			//取得表字段属性
			String tableField = "select *  from  "+tableName+" limit 0,1";
			pstmt = conn.prepareStatement(tableField); 
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();  //获取字段名 
			numberOfColumns = rsmd.getColumnCount(); //获取字段数 
			databaseName = conn.getCatalog();
			tableProperty.setTableName(tableName);
			List<FieldProperties> fieldPropertiesList = new ArrayList<FieldProperties>();
			for ( int i=1;i<=numberOfColumns;i++){
				//在数据库中类型的最大字符个数 
				int columnDisplaySize=rsmd.getColumnDisplaySize(i); 
				//某列类型的精确度(类型的长度) 
				int precision= rsmd.getPrecision(i); 
				FieldProperties fieldProperties= new FieldProperties();
				fieldProperties.setFieldName(rsmd.getColumnName(i));
				fieldProperties.setFieldTypeName(rsmd.getColumnTypeName(i));
				fieldProperties.setFieldType(rsmd.getColumnType(i));
				fieldPropertiesList.add(fieldProperties);
			//			fieldProperties.setFieldIndex(rsmd.get);
					//	if("BLOB".equals(rsmd.getColumnTypeName(i))){ 
					//		blobTable.add(dbObjectName);//将含有Blob字段的表名放入集合里
					//	}
			} 
			tableProperty.setFieldProperties(fieldPropertiesList);
			
			/**
			//索引信息 
			rs = dbmd.getIndexInfo(databaseName, null, tableName, false, true);   
            while (rs.next()) {   
                System.out.println("数据库名: "+ rs.getString(1));   
                System.out.println("表模式: "+ rs.getString(2));   
                System.out.println("表名称: "+ rs.getString(3));   
                System.out.println("索引值是否可以不唯一: "+ rs.getString(4));   
                System.out.println("索引类别: "+ rs.getString(5));   
                System.out.println("索引名称: "+ rs.getString(6));   
                System.out.println("索引类型: "+ rs.getString(7));   
                System.out.println("索引中的列序列号: "+ rs.getString(8));   
                System.out.println("列名称: "+ rs.getString(9));   
                System.out.println("列排序序列: "+ rs.getString(10));   
                System.out.println("TYPE为 tableIndexStatistic时它是表中的行数否则它是索引中唯一值的数量: "+ rs.getString(11));   
                System.out.println("TYPE为 tableIndexStatisic时它是用于表的页数否则它是用于当前索引的页数: "+ rs.getString(12));   
                System.out.println("过滤器条件: "+ rs.getString(13));   
                System.out.println(" ");
            }   **/

            /** 获取当指定模式下的表的主键列信息*/
            /** 第一个参数: 一个代表目录名的字符串*/
            /** 第二个参数: 模式名字符串*/
            /** 第三个参数: 表名字符串*/
			/**
            rs = dbmd.getPrimaryKeys(null, null, tableName); 
            while (rs.next()) { 
            	System.out.print("表名:"+rs.getString(3));
                System.out.print(" 列名:"+rs.getString(4));
                System.out.println(" 主键名:"+rs.getString(6));
            System.out.println("+++++++++++++++++++++++++++++++++"); 

            } **/

			
			//约束信息 
			rs=dbmd.getExportedKeys(null, null, tableName); 
			List<ConstraintProperty> constraintPropertyList = new ArrayList<ConstraintProperty>();
			while (rs.next()) {
				ConstraintProperty constraintProperty = new ConstraintProperty();
				
		        String PKTABLE_NAME=rs.getString("PKTABLE_NAME");//表名
		        String PKCOLUMN_NAME=rs.getString("PKCOLUMN_NAME");//主键   本表约束字段
		        String FKTABLE_NAME=rs.getString("FKTABLE_NAME");//被哪个表约束
		        String FKCOLUMN_NAME=rs.getString("FKCOLUMN_NAME");//被哪个表的某个键约束
		        String KEY_SEQ=rs.getString("KEY_SEQ"); 
		        String FK_NAME=rs.getString("FK_NAME");//约束名称
		        
		        constraintProperty.setPk_Table_Name(PKTABLE_NAME);
		        constraintProperty.setPk_Column_Name(PKCOLUMN_NAME);
		        constraintProperty.setFk_Column_Name(FKCOLUMN_NAME);
		        constraintProperty.setFk_Name(FK_NAME);
		        constraintProperty.setFk_Table_Name(FKTABLE_NAME);
		        constraintProperty.setKey_SEQ(Integer.parseInt(KEY_SEQ));
		        constraintPropertyList.add(constraintProperty);
			} 
			tableProperty.setConstraintProperty(constraintPropertyList);
			

		} catch (CannotGetJdbcConnectionException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("根据表名查询字段属性",e);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("根据表名查询字段属性",e);
	        }
		}finally{ 
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("根据表名查询字段属性",e);
			        }
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("根据表名查询字段属性",e);
			        }
				}
			}
			DataSourceUtils.releaseConnection(conn,dataSource);
		}
		
		return tableProperty;
	}
	
	/**
	 * 根据表名分页读取数据
	 * @param param 返回值参数
	 * @param tableName 表名
	 * @param currentPage  当前页
	 * @param maxResult 每页显示记录数
	 * @param tableProperty 表属性
	 * @return
	 */
	public List<List<String>> findPageByTableName(String param,String tableName,Long currentPage,Long maxResult,TableProperty tableProperty ){
		//采用分页方式遍历数据，防止内存溢出
		Connection  conn  = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		List<FieldProperties> fieldPropertiesList = tableProperty.getFieldProperties();//取得字段属性
		List<List<String>> tablePageData = new ArrayList<List<String>>();//表分页数据
		
		try {
			conn  = DataSourceUtils.getConnection(dataSource);
			String sql="select "+param+" from "+tableName+" limit "+currentPage+","+maxResult;//一次取100条数据一共消耗：329375毫秒时间;一次取2000条数据一共消耗：24250毫秒时间
			pstmt = conn.prepareStatement(sql); 
			rs = pstmt.executeQuery();
			while(rs.next()){
				List<String> field = new ArrayList<String>();//字段数据
				for(int i=0;i< fieldPropertiesList.size();i++){ 	
					FieldProperties fieldProperties = fieldPropertiesList.get(i);
					String data = null; //存放字段数据
			        if(rs.getString(i+1)!=null){ 
			             if(rs.getString(i+1).length()>0){ 
			                    data=rs.getString(i+1);
			             } 
			        } 
					if("BIT".equals(fieldProperties.getFieldTypeName())){ //如果为bit类型
						//方法一
						data = String.valueOf(rs.getBoolean(i+1));
				    }else if("BLOB".equals(fieldProperties.getFieldTypeName())){ 
				      //  String Hex = "";   	  
				        byte[] b = lobHandler.getBlobAsBytes(rs, i+1);//以二进制数据的方式获取结果集中的 BLOB 数据
				        if(b != null){
					        data = HexConversion.byteToHex(b);
				        }
					}
					field.add(data);
				}
				tablePageData.add(field);
			}
			
			
			
		} catch (SQLException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("根据表名分页读取数据",e);
	        }
		}finally{ 
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("根据表名分页读取数据",e);
			        }
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("根据表名分页读取数据",e);
			        }
				}
			}
			DataSourceUtils.releaseConnection(conn,dataSource);
		}
		return tablePageData;
	}
	/**
	 * 查询总页数
	 * @param tableName 表名
	 * @return
	 */
	public Long findCount(String tableName){
		//这个查询只能是查询一条记录的查询,返回一个map,key的值是column的值   
		Map map = jdbcTemplate.queryForMap("select count(*) as keyval from "+tableName);  
		return Long.parseLong(map.get("keyval").toString());
	}
	
	/**
	 * 还原数据
	 * @param tableProperty 数据库表属性
	 * @param path 路径
	 */
	public void restoreData(TableProperty tableProperty,String path ) {  
		List<String> fieldName = new ArrayList<String>();
		List<FieldProperties> fieldPropertiesList = tableProperty.getFieldProperties();//取得字段属性
		//组装SQL语句
        StringBuffer sql = new StringBuffer();
        StringBuffer sb_fieldName = new StringBuffer();//字段名称
        StringBuffer placeholder = new StringBuffer();//字段占位符
        
        for(FieldProperties fieldProperties : fieldPropertiesList){
			fieldName.add(fieldProperties.getFieldName());
			sb_fieldName.append("`"+fieldProperties.getFieldName()+"`,");
        	placeholder.append("?,");
		}
        sql.append("INSERT INTO ")
	    .append(tableProperty.getTableName()+" (")
	    .append(sb_fieldName.deleteCharAt(sb_fieldName.length()-1)+")")//删除多余的逗号
	    .append(" VALUES(")
	    .append(placeholder.deleteCharAt(placeholder.length()-1)+")");//删除多余的逗号
        
    
		
        Connection conn = null;
        PreparedStatement pstmt = null;
		
        BufferedReader br = null;
        CSVParser parser = null;
        
		try {
			//读取CSV备份文件     
			Path csvPath = Paths.get(path);
			br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
			
			//跳过表头
			CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader();
	        
			parser = new CSVParser(br,csvFormat);
			
			List<CSVRecord> records = parser.getRecords();   
			 
			conn  = DataSourceUtils.getConnection(dataSource);
			conn.setAutoCommit(false);
			
			
			//禁用MySQL外键约束
			this.disableConstraint(conn);
			
		
			pstmt =  conn.prepareStatement(sql.toString());

			long l = 0;	
			for (CSVRecord record : records) {//逐行读入除表头的数据       
				l++;
 			
				for(int i = 0; i<fieldPropertiesList.size(); i++){
					FieldProperties fieldProperties = fieldPropertiesList.get(i);
					
					if(record.get(fieldProperties.getFieldName()) != null && !"".equals(record.get(fieldProperties.getFieldName()))){
						
						if("BIT".equals(fieldProperties.getFieldTypeName())){
							pstmt.setBoolean(i+1, Boolean.valueOf(record.get(fieldProperties.getFieldName())));
						 }else if("DATETIME".equals(fieldProperties.getFieldTypeName())){
							 
							 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date date = null;
								try {
									date = format.parse(record.get(fieldProperties.getFieldName()));
								} catch (ParseException e1) {
								//	e1.printStackTrace();
									if (logger.isErrorEnabled()) {
							            logger.error("还原数据",e1);
							        }
									throw new SystemException("日期时间转换出错！"+record.get(fieldProperties.getFieldName())+" ---表--- "+ tableProperty.getTableName()+" ---字段--- "+fieldProperties.getFieldName());
								}
								pstmt.setObject(i+1, date);
						 }else if("DATE".equals(fieldProperties.getFieldTypeName())){
							 
							 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								Date date = null;
								try {
									date = format.parse(record.get(fieldProperties.getFieldName()));
								} catch (ParseException e1) {
								//	e1.printStackTrace();
									if (logger.isErrorEnabled()) {
							            logger.error("还原数据",e1);
							        }
									throw new SystemException("日期转换出错！"+record.get(fieldProperties.getFieldName())+" ---表--- "+ tableProperty.getTableName()+" ---字段--- "+fieldProperties.getFieldName());
								}
								pstmt.setObject(i+1, date);
						 }else if("BLOB".equals(fieldProperties.getFieldTypeName())){ 
							 byte[] b = HexConversion.hexStringToByte(record.get(fieldProperties.getFieldName()));
							  
							 //设置二进制BLOB参数
							 lobHandler.getLobCreator().setBlobAsBytes(pstmt, i+1,b);
						 }else{
							 pstmt.setObject(i+1, record.get(fieldProperties.getFieldName()));
						 }
					}else{//如果取得空值，返回null
						pstmt.setObject(i+1, null);
					}
				}
				 //加入批处理 
                pstmt.addBatch(); 
				if(l %1000L == 0L){//1000行保存一次
					dataRunMarkManage.taskRunMark_delete();
					dataRunMarkManage.taskRunMark_add(l);
					
					pstmt.executeBatch();//执行批处理 
				}
			}      
			pstmt.executeBatch();//执行批处理 
			conn.commit();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("还原数据",e);
	        }
		} catch (IOException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("还原数据",e);
	        }
		} catch (CannotGetJdbcConnectionException e2) {
		//	e2.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("还原数据",e2);
	        }
		} catch (SQLException e2) {
		//	e2.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("还原数据",e2);
	        }
		}finally { 
			//启用MySQL外键约束
			this.enableConstraint(conn);

			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("还原数据",e);
			        }
				}
			}
			DataSourceUtils.releaseConnection(conn,dataSource);
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("还原数据",e);
			        }
				}
			}
			if(parser != null){
				try {
					parser.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("还原数据",e);
			        }
				}
			}
        }  
	}
	/**
     * 启用MySQL外键约束
     */
    private void enableConstraint(Connection conn){	
    	String sql = "SET FOREIGN_KEY_CHECKS=1";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = conn.prepareStatement(sql);
    		pstmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("启用MySQL外键约束",e);
	        }
		}finally {
		    if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("启用MySQL外键约束关闭PreparedStatement错误",e);
			        }
				}
			}
		}
    }
    /**
     * 禁用MySQL外键约束
     */
    private void disableConstraint(Connection conn){	
    	String sql = "SET FOREIGN_KEY_CHECKS=0";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = conn.prepareStatement(sql);
    		pstmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("禁用MySQL外键约束",e);
	        }
		}finally {
		    if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("禁用MySQL外键约束关闭PreparedStatement错误",e);
			        }
				}
			}
		}
    }
    /**
     * 查询MySQL外键约束状态
     * @return 1:外键约束启用  0:外键约束已禁用
     */
    private int findConstraintStatus(Connection conn){	
    	String sql = "SELECT @@FOREIGN_KEY_CHECKS";
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
		try {
			pstmt= conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
		//		System.out.println("查询MySQL外键约束状态 "+rs.getInt(1));
				return rs.getInt(1);
			}
		
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally { 
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("查询MySQL外键约束状态",e);
			        }
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("查询MySQL外键约束状态关闭PreparedStatement错误",e);
			        }
				}
			}
		}
    	return -1;
    }
	
	/**
     * 清空MySQL数据库
     * @param table
     */
    public void deleteDatabase(List<String> table){	
    	Connection  conn  = null;
    	PreparedStatement pstmt = null;
		try {
			conn  = DataSourceUtils.getConnection(dataSource);
			//禁用MySQL外键约束
			this.disableConstraint(conn);
			if(table != null && table.size() >0){
	    		for(String s : table){
	    			String sql = "truncate table "+ s ;
	    			pstmt = conn.prepareStatement(sql);
	    			pstmt.execute();
	        	}	
	    	}	
		}catch (SQLException e) {
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("清空MySQL数据库",e);
	        }
		}finally {
			//启用MySQL外键约束
		    this.enableConstraint(conn);
		    if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("清空MySQL数据库关闭错误",e);
			        }
				}
			}
			DataSourceUtils.releaseConnection(conn,dataSource);
		}
    	
    //	String sql = "DROP TABLE "+ table ;
		
	}
    

	
}
