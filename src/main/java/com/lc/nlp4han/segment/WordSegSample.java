package com.lc.nlp4han.segment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 * 切分后的句子的内部表示或训练样本内部表示.
 *
 * 句子中每个字给出了位置标记。 位置标记集为：S（单字词）, B（多字词的第一个字）, M（多字词的中间字）, E（多字词的末尾字）.
 *
 * @author 刘小峰
 */
public class WordSegSample
{

    private List<String> sentence; // 句子中字

    private List<String> tags; // 对应的每个字的位置标记

    private final String[][] additionalContext; // 每个字的额外特征或上下文

    /**
     *
     * @param sentence 句子的字系列
     * @param tags 每个字的位置标记
     */
    public WordSegSample(String sentence[], String tags[])
    {
        this(sentence, tags, null);
    }

    /**
     *
     * @param sentence 句子的字系列
     * @param tags 每个字的位置标记
     */
    public WordSegSample(List<String> sentence, List<String> tags)
    {
        this(sentence, tags, null);
    }

    /**
     *
     * @param sentence 句子的字系列
     * @param tags 每个字的位置标记
     * @param additionalContext 每个字的额外上下文或特征
     */
    public WordSegSample(List<String> sentence, List<String> tags,
            String[][] additionalContext)
    {
        this.sentence = Collections.unmodifiableList(sentence);
        this.tags = Collections.unmodifiableList(tags);

        checkArguments();
        String[][] ac;
        if (additionalContext != null)
        {
            ac = new String[additionalContext.length][];

            for (int i = 0; i < additionalContext.length; i++)
            {
                ac[i] = new String[additionalContext[i].length];
                System.arraycopy(additionalContext[i], 0, ac[i], 0,
                        additionalContext[i].length);
            }
        } else
        {
            ac = null;
        }
        this.additionalContext = ac;
    }

    /**
     *
     * @param sentence 句子的字系列
     * @param tags 每个字的位置标记
     * @param additionalContext 每个字的额外上下文或特征
     */
    public WordSegSample(String sentence[], String tags[],
            String[][] additionalContext)
    {
        this(Arrays.asList(sentence), Arrays.asList(tags), additionalContext);
    }

    private void checkArguments()
    {
        if (sentence.size() != tags.size())
        {
            throw new IllegalArgumentException(
                    "There must be exactly one tag for each token. tokens: " + sentence.size()
                    + ", tags: " + tags.size());
        }

        if (sentence.contains(null))
        {
            throw new IllegalArgumentException("null elements are not allowed in sentence tokens!");
        }
        if (tags.contains(null))
        {
            throw new IllegalArgumentException("null elements are not allowed in tags!");
        }
    }

    /**
     * 句子的字系列
     *
     * @return 句子的字系列
     */
    public String[] getSentence()
    {
        return sentence.toArray(new String[sentence.size()]);
    }

    /**
     * 每个字的位置标记
     *
     * @return 每个字的位置标记
     */
    public String[] getTags()
    {
        return tags.toArray(new String[tags.size()]);
    }

    /**
     * 每个字的额外上下文或特征
     *
     * @return 每个字的额外上下文或特征
     */
    public String[][] getAddictionalContext()
    {
        return this.additionalContext;
    }

    /**
     * 将切分的句子样本转换成词序列
     *
     * @return 样本句子的词系列
     */
    public String[] toWords()
    {
        String word = new String();
        ArrayList<String> words = new ArrayList<String>();
        for (int i = 0; i < tags.size(); i++)
        {
            word += sentence.get(i);

            if (tags.get(i).equals("S") || tags.get(i).equals("E"))
            {
                words.add(word);
                word = "";
            }
        }

        if (word.length() > 0)
        {
            words.add(word);
        }

        return words.toArray(new String[words.size()]);
    }

    /**
     * 将切分的句子样本转换成空格分隔的词组成的字符串
     *
     * @return 空格分隔的词组成的字符串
     */
    public String toSample()
    {
        String sample = new String();
        ArrayList<String> words = new ArrayList<String>();
        for (int i = 0; i < tags.size(); i++)
        {
            sample += sentence.get(i);

            if (tags.get(i).equals("S") || tags.get(i).equals("E"))
            {
                sample += " ";
            }
        }

        return sample;
    }

    @Override
    /**
     * 将分词样本转换成:字_标记 字_标记 字_标记...
     */
    public String toString()
    {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < getSentence().length; i++)
        {
            result.append(getSentence()[i]);
            result.append('_');
            result.append(getTags()[i]);
            result.append(' ');
        }

        if (result.length() > 0)
        {
            // get rid of last space
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    /**
     * 将空格分隔的切分句子转换成样本内部表示
     * 
     * @param sentenceString 空格分隔的切分句子
     * @return 样本内部表示
     */
    public static WordSegSample parse(String sentenceString)
    {

        String[] words = WhitespaceTokenizer.INSTANCE.tokenize(sentenceString);

        ArrayList<String> sentence = new ArrayList<String>();
        ArrayList<String> tags = new ArrayList<String>();

        for (int i = 0; i < words.length; i++)
        {
            String word = words[i];

            if (word.length() == 1)
            {
                sentence.add(word);
                tags.add("S");
                continue;
            }

            for (int j = 0; j < word.length(); j++)
            {
                char c = word.charAt(j);
                if (j == 0)
                {
                    sentence.add(c + "");
                    tags.add("B");
                } else if (j == word.length() - 1)
                {
                    sentence.add(c + "");
                    tags.add("E");
                } else
                {
                    sentence.add(c + "");
                    tags.add("M");
                }
            }
        }

        return new WordSegSample(sentence, tags);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        } else if (obj instanceof WordSegSample)
        {
            WordSegSample a = (WordSegSample) obj;

            return Arrays.equals(getSentence(), a.getSentence())
                    && Arrays.equals(getTags(), a.getTags());
        } else
        {
            return false;
        }
    }
}
