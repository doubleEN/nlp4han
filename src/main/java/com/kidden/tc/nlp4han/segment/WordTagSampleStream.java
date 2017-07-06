package com.kidden.tc.nlp4han.segment;

import java.io.IOException;

import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

/**
 * 将空格分隔的切分句子转换成{@link WordSegSample}
 * 
 * @author 刘小峰
 */
public class WordTagSampleStream extends FilterObjectStream<String, WordSegSample>
{
    /**
     * 
     * @param sentences 待转换的切分句子流
     */
    public WordTagSampleStream(ObjectStream<String> sentences)
    {
        super(sentences);
    }

    /**
     * 读取下一切分句子，并转换成{@link WordSegSample}
     *
     *
     * @return 转换后的{@link WordSegSample}，或者当流结束返回null
     * @throws java.io.IOException
     */
    @Override
    public WordSegSample read() throws IOException
    {
        String sentence = samples.read();

        if (sentence != null)
        {
            WordSegSample sample = WordSegSample.parse(sentence);

            return sample;
        } 
        else
        {
            // 句子流结束
            return null;
        }
    }
}
