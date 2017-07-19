package com.kidden.tc.nlp4han.segment;

import opennlp.tools.util.eval.EvaluationMonitor;

/**
 * 分词评价监视器
 * 
 * @author 刘小峰
 *
 */
public class WordSegEvaluationMonitor implements EvaluationMonitor<WordSegSample>
{

    /**
     * 正确切分
     * 
     * @param reference 参考切分的句子
     * @param prediction 系统切分的结果
     */
    @Override
    public void correctlyClassified(WordSegSample reference, WordSegSample prediction)
    {
    }

    /**
     * 错误切分
     * 
     * @param reference 参考切分的句子
     * @param prediction 系统切分的结果
     */
    @Override
    public void missclassified(WordSegSample reference, WordSegSample prediction)
    {
    }
}
