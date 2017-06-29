package com.kidden.tc.nlp4han.segment;

/**
 * 中文断句器接口
 *
 * @author 刘小峰
 */
public interface SentenceSegmenter
{
    /**
     * 将文本切分为句子
     * 
     * @param text 待断句的文本
     * @return 句子数组
     */
    public String[] segment(String text);
}
