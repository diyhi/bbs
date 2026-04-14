package cms.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;

/**
 * 结巴分词
 */
public class JiebaAnalyzer extends Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiebaAnalyzer.class);

    private static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";

    private final CharArraySet stopWords;

    private JiebaSegmenter.SegMode segMode;

    public JiebaAnalyzer(JiebaSegmenter.SegMode segMode) {
        this.segMode = segMode;
        this.stopWords = new CharArraySet(128, true);
    }

    public JiebaAnalyzer(JiebaSegmenter.SegMode segMode, CharArraySet stopWords) {
        this.segMode = segMode;
        this.stopWords = new CharArraySet(stopWords, true);
    }

    /**
     * use for add user dictionary and stop words, user dictionary need with .dict suffix, stop words with file name: stopwords.txt
     *
     * @param userDictPath
     */
    public void init(String userDictPath) {
        if (!StringUtils.isEmpty(userDictPath)) {
            File file = new File(userDictPath);
            if (file.exists()) {
                // load user dict
                WordDictionary wordDictionary = WordDictionary.getInstance();
                wordDictionary.init(Paths.get(userDictPath));

                // load stop words from userDictPath with name stopwords.txt, one word per line.
                loadStopWords(Paths.get(userDictPath, DEFAULT_STOPWORD_FILE), Charset.forName("UTF-8"));
            }
        }
    }

    /**
     * load stop words from path
     *
     * @param userDict stop word path, one word per line
     * @param charset
     */
    private void loadStopWords(Path userDict, Charset charset) {
        try {
            BufferedReader br = Files.newBufferedReader(userDict, charset);
            int count = 0;
            while (br.ready()) {
                String line = br.readLine();
                if (!StringUtils.isEmpty(line)) {
                    stopWords.add(line);
                    ++count;
                }
            }
            System.out.println(String.format(Locale.getDefault(), "%s: load stop words total:%d!", userDict.toString(), count));
            br.close();
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s: load stop words failure!", userDict.toString()));
        }
    }

    @Override
    protected TokenStreamComponents createComponents(String s) {
        final Tokenizer tokenizer = new JiebaTokenizer(segMode);
        TokenStream result = tokenizer;

        if (!stopWords.isEmpty()) {
            result = new StopFilter(result, stopWords);
        }

        return new TokenStreamComponents(tokenizer, result);
    }

}