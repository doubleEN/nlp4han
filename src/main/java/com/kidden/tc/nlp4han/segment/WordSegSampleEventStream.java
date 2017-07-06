package com.kidden.tc.nlp4han.segment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 * 将分词样本{@link WordSegSample}流转换成最大熵训练需要的事件流
 * 
 * @author 刘小峰
 */
public class WordSegSampleEventStream extends AbstractEventStream<WordSegSample> {

    /**
     * 用于产生事件的上下文产生器
     */
    private WordSegContextGenerator cg;

    /**
     * 用分词样本流和上下文产生器创建实例
     *
     * @param samples 分词样本流
     * @param cg 上下文产生器
     */
    public WordSegSampleEventStream(ObjectStream<WordSegSample> samples, WordSegContextGenerator cg) {
        super(samples);

        this.cg = cg;
    }

//    /**
//     * 使用缺省上下文产生器{@link DefaultWordSegContextGenerator}和样本流创建实例.
//     *
//     * @param samples 分词样本流
//     */
//    public WordSegSampleEventStream(ObjectStream<WordSegSample> samples) {
//        this(samples, new DefaultWordSegContextGenerator());
//    }

    @Override
    protected Iterator<Event> createEvents(WordSegSample sample) {
        String sentence[] = sample.getSentence();
        String tags[] = sample.getTags();
        Object ac[] = sample.getAddictionalContext();
        List<Event> events = generateEvents(sentence, tags, ac, cg);
        return events.iterator();
    }

    /**
     * 由一个切分句子创建训练事件序列
     * 
     * @param sentence 切分句子中的字
     * @param tags 字的标记
     * @param additionalContext 字的额外特征
     * @param cg 上下文产生器
     * @return 事件序列
     */
    public static List<Event> generateEvents(String[] sentence, String[] tags,
            Object[] additionalContext, WordSegContextGenerator cg) {
        List<Event> events = new ArrayList<Event>(sentence.length);

        for (int i = 0; i < sentence.length; i++) {

            // it is safe to pass the tags as previous tags because
            // the context generator does not look for non predicted tags
            String[] context = cg.getContext(i, sentence, tags, additionalContext);

            events.add(new Event(tags[i], context));
        }
        return events;
    }

    /**
     * 由一个切分句子创建训练事件序列
     * 
     * @param sentence 切分句子中的字
     * @param tags 字的标记
     * @param cg 上下文产生器
     * @return 事件序列
     */
    public static List<Event> generateEvents(String[] sentence, String[] tags,
            WordSegContextGenerator cg) {
        return generateEvents(sentence, tags, null, cg);
    }
}
