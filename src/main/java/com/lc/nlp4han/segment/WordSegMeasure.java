package com.lc.nlp4han.segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * 分词评价指标
 * 
 * @author 刘小峰
 *
 */
public final class WordSegMeasure
{

    /**
     * |selected| = true positives + false positives <br>
     * 切分结果数.
     */
    private long selected;

    /**
     * |target| = true positives + false negatives <br>
     * 目标或参考结果数
     */
    private long target;

    /**
     * 正确切分结果数
     */
    private long truePositive;

    private long sentences;
    private long sentencesOK;

    private HashSet<String> dictionary;

    private long targetIV;
    private long targetOOV;

    private long truePositiveIV;
    private long truePositiveOOV;

    public WordSegMeasure(HashSet<String> dict)
    {
        this.dictionary = dict;
    }

    public WordSegMeasure()
    {

    }

    /**
     * 分词查准率P
     * 
     * @return 查准率
     */
    public double getPrecisionScore()
    {
        return selected > 0 ? (double) truePositive / (double) selected : 0;
    }

    /**
     * 分词查全率R
     * 
     * @return 查全率
     */
    public double getRecallScore()
    {
        return target > 0 ? (double) truePositive / (double) target : 0;
    }

    /**
     * 登录词查全率Riv
     * 
     * @return 登录词查全率
     */
    public double getRecallScoreIV()
    {
        return targetIV > 0 ? (double) truePositiveIV / (double) targetIV : 0;
    }

    /**
     * 未登录词查全率Roov
     * 
     * @return 未登录词查全率
     */
    public double getRecallScoreOOV()
    {
        return targetOOV > 0 ? (double) truePositiveOOV / (double) targetOOV : 0;
    }

    /**
     * 句子切分正确率SA
     * 
     * @return 句子切分正确率
     */
    public double getSentenceAccuracy()
    {
        return sentences > 0 ? (double) sentencesOK / (double) sentences : 0;
    }

    /**
     * 分词F1度量
     * 
     * F1 = 2 * P * R / (P + R)
     * 
     * @return 分词F1度量
     */
    public double getMeasure()
    {

        if (getPrecisionScore() + getRecallScore() > 0)
        {
            return 2 * (getPrecisionScore() * getRecallScore()) / (getPrecisionScore() + getRecallScore());
        }
        else
        {
            // cannot divide by zero, return error code
            return -1;
        }
    }

    /**
     * 根据参考切分和系统切分更新评价指标
     * 
     * @param references
     *            参考切分
     * @param predictions
     *            系统切分
     */
    public void updateScores(final String[] references, final String[] predictions)
    {
        sentences++;

        if (references.length == predictions.length)
        {
            boolean okSent = true;
            for (int i = 0; i < references.length; i++)
            {
                if (!references[i].equals(predictions[i]))
                    okSent = false;
            }

            if (okSent)
                sentencesOK++;
        }

        truePositive += countTruePositivesWithDictionary(references, predictions);
        selected += predictions.length;
        target += references.length;
    }

    /**
     * 合并其他评价结果
     * 
     * @param measure
     *            待合并评价结果
     */
    public void mergeInto(final WordSegMeasure measure)
    {
        this.selected += measure.selected;
        this.target += measure.target;
        this.truePositive += measure.truePositive;

        this.sentences += measure.sentences;
        this.sentencesOK += measure.sentencesOK;

        this.targetIV += measure.targetIV;
        this.truePositiveIV += measure.truePositiveIV;

        this.targetOOV += measure.targetOOV;
        this.truePositiveOOV += measure.truePositiveOOV;
    }

    /**
     * 产生可读的评价结果串
     * 
     * @return 可读的评价结果串
     */
    @Override
    public String toString()
    {
        return "Precision: " + Double.toString(getPrecisionScore()) + "\n" + "Recall: " + Double.toString(getRecallScore()) + "\n" + "F-Measure: " + Double.toString(getMeasure()) + "\n" + "RIV: " + Double.toString(getRecallScoreIV()) + "\n" + "ROOV: " + Double.toString(getRecallScoreOOV()) + "\n" + "SentenceAccuray: " + Double.toString(getSentenceAccuracy());
    }

    private int countTruePositivesWithDictionary(final String[] references, final String[] predictions)
    {

        List<String> predListSpans = new ArrayList<String>(predictions.length);
        Collections.addAll(predListSpans, predictions);
        int truePositives = 0;
        Object matchedItem = null;

        for (int referenceIndex = 0; referenceIndex < references.length; referenceIndex++)
        {
            String referenceName = references[referenceIndex];

            boolean isIV = true;

            if (dictionary != null)
            {
                isIV = dictionary.contains(referenceName);

                if (isIV)
                    targetIV++;
                else
                    targetOOV++;
            }

            for (int predIndex = 0; predIndex < predListSpans.size(); predIndex++)
            {

                if (referenceName.equals(predListSpans.get(predIndex)))
                {
                    matchedItem = predListSpans.get(predIndex);
                    truePositives++;

                    if (dictionary != null)
                    {
                        if (isIV)
                            truePositiveIV++;
                        else
                            truePositiveOOV++;
                    }

                    break;
                }
            }

            if (matchedItem != null)
            {
                predListSpans.remove(matchedItem);

                matchedItem = null;
            }
        }
        
        return truePositives;
    }

    /**
     * 根据参考切分和系统切分统计正确切分的单词数。

     * 匹配单词从参考切分中移除
     * 
     * @param references
     *            参考切分
     * @param predictions
     *            系统切分
     * @return 正确切分的单词数
     */
    static int countTruePositives(final String[] references, final String[] predictions)
    {
        List<String> predListSpans = new ArrayList<String>(predictions.length);
        Collections.addAll(predListSpans, predictions);
        int truePositives = 0;
        Object matchedItem = null;

        for (int referenceIndex = 0; referenceIndex < references.length; referenceIndex++)
        {
            String referenceName = references[referenceIndex];

            for (int predIndex = 0; predIndex < predListSpans.size(); predIndex++)
            {

                if (referenceName.equals(predListSpans.get(predIndex)))
                {
                    matchedItem = predListSpans.get(predIndex);
                    truePositives++;
                    break;
                }
            }

            if (matchedItem != null)
            {
                predListSpans.remove(matchedItem);

                matchedItem = null;
            }
        }
        return truePositives;
    }

    /**
     * 根据参考切分和系统切分计算查准率
     * 
     * @param references
     *            参考切分
     * @param predictions
     *            系统切分
     * @return NaN，如果参考为空
     */
    public static double precision(final String[] references, final String[] predictions)
    {

        if (predictions.length > 0)
        {
            return countTruePositives(references, predictions) / (double) predictions.length;
        }
        else
        {
            return Double.NaN;
        }
    }

    /**
     * 根据参考切分和系统切分计算查全率
     * 
     * @param references
     *            参考切分
     * @param predictions
     *            系统切分
     * @return NaN，如果参考为空
     */
    public static double recall(final String[] references, final String[] predictions)
    {

        if (references.length > 0)
        {
            return countTruePositives(references, predictions) / (double) references.length;
        }
        else
        {
            return Double.NaN;
        }
    }
}
