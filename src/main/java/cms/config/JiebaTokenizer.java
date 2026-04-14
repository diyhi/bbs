package cms.config;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.SegmentingTokenizerBase;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

/**
 * jieba分词器
 *
 */
public class JiebaTokenizer extends SegmentingTokenizerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiebaTokenizer.class);

    /** used for breaking the text into sentences */
    private static final BreakIterator sentenceProto = BreakIterator.getSentenceInstance(Locale.ROOT);

    /** 词元 **/
    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    /** 位移 **/
    private final OffsetAttribute offsetAttribute = addAttribute(OffsetAttribute.class);
    /** 距离 **/
    private final PositionIncrementAttribute positionAttribute = addAttribute(PositionIncrementAttribute.class);
    /** 词性 **/
    private final TypeAttribute typeAttribute = addAttribute(TypeAttribute.class);

    /** Jieba Segmenter and tokens */
    private final JiebaSegmenter wordSegmenter = new JiebaSegmenter();

    private Iterator<SegToken> tokens;

    /** jieba segMode */
    private final JiebaSegmenter.SegMode segMode;

    /** record field sentence offset */
    private int sentenceStart = 0;
    private int sentenceEnd = 0;

    /**
     * used for sentence witch length > 1024 if sentence length greater than 1024, setNextSentence method parameter sentenceStart will be 0 fieldIdCounter is filed id counter, curFieldId
     */
    private long fieldIdCounter = 0;
    private static long MAX_FIELD_ID = 1000000;
    private long curFieldId = 0;
    private int fieldOffset = 0;

    /** Creates a new JiebaTokenizer */
    public JiebaTokenizer(JiebaSegmenter.SegMode segMode) {
        super(sentenceProto);
        this.segMode = segMode;
    }

    /** Creates a new JiebaTokenizer, supplying the AttributeFactory */
    public JiebaTokenizer(JiebaSegmenter.SegMode segMode, AttributeFactory factory) {
        super(factory, (BreakIterator) sentenceProto.clone());
        this.segMode = segMode;
    }

    @Override
    protected void setNextSentence(int sentenceStart, int sentenceEnd) {
        if (curFieldId != fieldIdCounter) {// after reset, new field start.
            curFieldId = fieldIdCounter;

            this.sentenceStart = sentenceStart;
            this.sentenceEnd = sentenceEnd;
        } else {// field not change
            if (sentenceStart == 0) {
                fieldOffset = this.sentenceEnd;
            }

            this.sentenceStart = sentenceStart + fieldOffset;
            this.sentenceEnd = sentenceEnd + fieldOffset;
        }

        String sentence = new String(buffer, sentenceStart, sentenceEnd - sentenceStart);
        List<SegToken> segTokenList = wordSegmenter.process(sentence, segMode);

        // need order SegTokens by startOffset
        Collections.sort(segTokenList, new Comparator<SegToken>() {
            /**
             * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
             * @param o1
             * @param o2
             */
            @Override
            public int compare(SegToken o1, SegToken o2) {
                return o1.startOffset - o2.startOffset;
            }
        });

        tokens = segTokenList.iterator();
    }

    @Override
    protected boolean incrementWord() {
        if (tokens == null || !tokens.hasNext()) {
            return false;
        } else {
            SegToken token = tokens.next();
            clearAttributes();
            termAttribute.copyBuffer(token.word.toCharArray(), 0, token.word.length());
            int startOffset = sentenceStart + token.startOffset;
            int endOffset = sentenceStart + token.endOffset;

            offsetAttribute.setOffset(startOffset, endOffset);
            typeAttribute.setType("word");
            return true;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();

        fieldIdCounter = (++fieldIdCounter % MAX_FIELD_ID);

        tokens = null;
        sentenceStart = 0;
        sentenceEnd = 0;
        fieldOffset = 0;
    }

}