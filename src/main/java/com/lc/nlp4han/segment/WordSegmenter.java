package com.lc.nlp4han.segment;

/**
 * 中文分词器接口
 *
 * @author 刘小峰
 */
public interface WordSegmenter
{

    /**
     * 将句子切分为词
     *
     * @param sentence 待切分的句子
     * @return 切分后的词数组
     */
    public String[] segment(String sentence);

}
