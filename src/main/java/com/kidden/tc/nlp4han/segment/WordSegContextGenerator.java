package com.kidden.tc.nlp4han.segment;

import opennlp.tools.util.BeamSearchContextGenerator;

/**
 * 分词上下文或特征产生器接口
 *
 * @author 刘小峰
 */
public interface WordSegContextGenerator extends BeamSearchContextGenerator<String>
{

    /**
     * 获得给定位置的字的上下文或特征
     *
     * @param index 上下文或特征产生的字的位置.
     * @param tokens 字序列.
     * @param tags 当前字前面字的标记序列.
     * @param ac 当前字的额外上下文
     *
     * @return 当前字的上下文或特征
     */
    public String[] getContext(int index, String[] tokens, String[] tags, Object[] ac);

}
