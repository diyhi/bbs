package cms.config;

import cms.utils.PathUtil;
import com.huaban.analysis.jieba.JiebaSegmenter;
import jakarta.annotation.Resource;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Lucene搜索配置
 */
@Configuration
public class LuceneConfig {
    @Resource ResourceLoader resourceLoader;

    /**
     * 结巴分词
     * @param stopWords 停用词
     * @return
     */
    @Bean
    public Analyzer analyzer(CharArraySet stopWords) {
        // 实例化并返回 JiebaAnalyzer
        // 你可以在这里根据需要配置不同的分词模式或停用词
        return new JiebaAnalyzer(JiebaSegmenter.SegMode.INDEX,stopWords);
    }

    /**
     * 停用词
     * @return
     * @throws IOException
     */
    @Bean
    public CharArraySet stopWords() throws IOException {
        // 加载 classpath 下的 stopwords.txt 文件
        org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:stopwords.txt");
        Set<String> stopWordSet = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 移除行首尾的空格，并添加到集合
                stopWordSet.add(line.trim());
            }
        }

        // 返回一个不可变的 CharArraySet 实例
        return new CharArraySet(stopWordSet, false);
    }

    /**
     * 话题写索引
     * @param analyzer 结巴分词
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "close")
    public IndexWriter topicIndexWriter(Analyzer analyzer) throws IOException {
        String indexPath = PathUtil.defaultExternalDirectory()+ File.separator+"data"+File.separator+"topicIndex"+File.separator;

        //话题Lucene初始化
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);//创建索引模式：CREATE:覆盖模式； APPEND:追加模式；   CREATE_OR_APPEND:每次都追加更新
        IndexWriter writer = new IndexWriter(directory, config);

        // 索引是否为空
        if (!DirectoryReader.indexExists(directory)) {
            // 如果索引不存在，先创建 IndexWriter，然后写入一个空文档并提交
            //writer.addDocument(new Document());
            writer.commit();
        }

        return writer;
    }

    /**
     * 问题写索引
     * @param analyzer 结巴分词
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "close")
    public IndexWriter questionIndexWriter(Analyzer analyzer) throws IOException {
        String indexPath = PathUtil.defaultExternalDirectory()+File.separator+"data"+File.separator+"questionIndex"+File.separator;

        //问题Lucene初始化
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);//创建索引模式：CREATE:覆盖模式； APPEND:追加模式；   CREATE_OR_APPEND:每次都追加更新
        IndexWriter writer = new IndexWriter(directory, config);

        // 索引是否为空
        if (!DirectoryReader.indexExists(directory)) {
            // 如果索引不存在，先创建 IndexWriter，然后写入一个空文档并提交
            //writer.addDocument(new Document());
            writer.commit();
        }

        return writer;
    }

    /**
     * 获取索引对象
     * @param topicIndexWriter 话题写索引
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "close")
    public SearcherManager topicSearcherManager(IndexWriter topicIndexWriter) throws IOException {
        // SearcherManager 需要 IndexWriter 来进行刷新
        return new SearcherManager(topicIndexWriter, false, false, null);
    }

    /**
     * 问题获取索引对象
     * @param questionIndexWriter 问题写索引
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "close")
    public SearcherManager questionSearcherManager(IndexWriter questionIndexWriter) throws IOException {
        // SearcherManager 需要 IndexWriter 来进行刷新
        return new SearcherManager(questionIndexWriter, false, false, null);
    }

}
