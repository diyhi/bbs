package cms.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.spi.copy.Copier;


/**
 * 作用是读取或写入数据时不使用原始数据，而使用拷贝数据
 * 由ehcache.xml配置调用
 * @author Gao
 * 也可以实现ReadWriteCopier<T>
 * @param <T>
 */
public class EhcacheCopyStrategy<T> implements Copier<Object>{
    private static final Logger logger = LogManager.getLogger(EhcacheCopyStrategy.class);

    /**
     * 参考net.sf.ehcache.store.ReadWriteSerializationCopyStrategy.java
     */
    @Override
    public Object copyForRead(Object storedValue) {
        if(storedValue != null){
            ByteArrayInputStream bin = new ByteArrayInputStream((byte[]) storedValue);

            try (ObjectInputStream ois = new ObjectInputStream(bin);){

                return ois.readObject();
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("copyForRead序列化错误",e);
                }
            }
        }
        return storedValue;
    }

    @Override
    public Object copyForWrite(Object value) {
        if(value != null){
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            try (ObjectOutputStream oos = new ObjectOutputStream(bout);){

                oos.writeObject(value);
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("copyForWrite序列化错误",e);
                }
            }
            return bout.toByteArray();
        }
        return value;
    }
}
