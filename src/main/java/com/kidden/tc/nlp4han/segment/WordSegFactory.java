package com.kidden.tc.nlp4han.segment;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.util.InvalidFormatException;

/**
 * 中文分词工厂
 * 
 * @author 刘小峰
 *
 */
public class WordSegFactory
{
    public final static byte MODEL_PKU = 0;
    public final static byte MODEL_MSR = 1;
    
    private WordSegFactory(){}
    
    /**
     * 获得和给定模型对应的中文分词器
     * 
     * @param modelType 模型类型，MODEL_PKU或MODEL_MSR
     * @return 若模型类型不对，返回null
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static WordSegmenter getWordSegmenter(byte modelType) throws InvalidFormatException, IOException
    {
        String modelName = "com/kidden/tc/nlp4han/segment/ws-pku-utf8.model";
        if(modelType == MODEL_MSR)
            modelName = "com/kidden/tc/nlp4han/segment/ws-msr-utf8.model";
        else if(modelType == MODEL_PKU)
            modelName = "com/kidden/tc/nlp4han/segment/ws-pku-utf8.model";
        else
            return null;
        
        InputStream modelIn = WordSegFactory.class.getClassLoader().getResourceAsStream(modelName);
        WordSegModel model = new WordSegModel(modelIn);
        
        WordSegmenterME segmenter = new WordSegmenterME(model, new WordSegContextGeneratorConf());
        
        return segmenter;
    }
    
    public static void main(String[] args) throws InvalidFormatException, IOException
    {
        String text  = "这个句子分词难不难？";
        
        WordSegmenter segmenter = WordSegFactory.getWordSegmenter(MODEL_MSR);
        String[] words = segmenter.segment(text);
        for(String w : words)
            System.out.println(w);
    }
}
