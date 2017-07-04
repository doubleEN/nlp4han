package com.kidden.tc.nlp4han.segment;

import opennlp.tools.util.SequenceValidator;

/**
 * 对最大熵产生的中间分词结果进行验证
 *
 * @author 刘小峰
 */
public class DefaultWordSegSequenceValidator implements SequenceValidator<String>
{

    /**
     * 判断分词序列（标记序列）是否合法
     *
     * 不合法的标记序列例子有：BB, BS, MB, MS, EE, EM, SM, SE等
     *
     * @param i 当前检查位置
     * @param inputSequence 待切分文本
     * @param outcomesSequence 当前位置前输出结果
     * @param outcome 当前标记结果
     * @return true合法，false非法
     */
    public boolean validSequence(int i, String[] inputSequence,
            String[] outcomesSequence, String outcome)
    {
//      System.out.println("" + i + Arrays.toString(inputSequence) + " " +
//      Arrays.toString(outcomesSequence) + " " + outcome);

        if (i == 0)
        {
            return outcome.equals("S") || outcome.equals("B");
        } else
        {
            if (outcome.equals("S"))
            {
                return outcomesSequence[i - 1].equals("S") || outcomesSequence[i - 1].equals("E");
            } else if (outcome.equals("B"))
            {
                return outcomesSequence[i - 1].equals("S") || outcomesSequence[i - 1].equals("E");
            } else if (outcome.equals("M"))
            {
                return outcomesSequence[i - 1].equals("B") || outcomesSequence[i - 1].equals("M");
            } else if (outcome.equals("E"))
            {
                return outcomesSequence[i - 1].equals("B") || outcomesSequence[i - 1].equals("M");
            }
        }

        return true;
    }
}
