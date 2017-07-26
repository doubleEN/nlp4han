package com.kidden.tc.nlp4han.segment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/*
 * 基于配置文件的上下文特征产生器
 * 
 * @author 刘小峰
 * @author 王馨苇
 */
public class WordSegContextGeneratorConf implements WordSegContextGenerator
{

    private boolean c_2Set;
    private boolean c_1Set;
    private boolean c0Set;
    private boolean c1Set;
    private boolean c2Set;
    private boolean c_2c_1Set;
    private boolean c_1c0Set;
    private boolean c0c1Set;
    private boolean c1c2Set;
    private boolean c_1c1Set;
    private boolean t_2Set;
    private boolean t_1Set;

    private boolean c_2c0set;
    private boolean c_1c0c1set;
    private boolean c0prefix;
    
    // 增加的字典特征的控制变量
    private boolean Lt0Set;
    private boolean c_1t0Set;
    private boolean c0t0Set;
    private boolean c1t0Set;
    
    
    // 标点符号的特征
    private boolean PuSet;
    
    // 是否为数字，年月日，字母，其他的特征
    private boolean TSet;

    // 获取词典中的词（自己提供地址和编码方式）
    Set<String> dictWords;

    public WordSegContextGeneratorConf() throws IOException
    {
        InputStream dictIn = WordSegContextGeneratorConf.class.getClassLoader().getResourceAsStream("com/kidden/tc/nlp4han/segment/words-gb.txt");
        dictWords = DictionaryLoader.getWords(dictIn, "gbk");

        Properties featureConf = new Properties();
        InputStream featureStream = WordSegContextGeneratorConf.class.getClassLoader().getResourceAsStream("com/kidden/tc/nlp4han/segment/feature.properties");
        featureConf.load(featureStream);

        init(featureConf);
    }

    public WordSegContextGeneratorConf(Properties config)
    {
        init(config);
    }

    private void init(Properties config)
    {
        c_2Set = (config.getProperty("feature.c_2", "true").equals("true"));
        c_1Set = (config.getProperty("feature.c_1", "true").equals("true"));
        c0Set = (config.getProperty("feature.c0", "true").equals("true"));
        c1Set = (config.getProperty("feature.c1", "true").equals("true"));
        c2Set = (config.getProperty("feature.c2", "true").equals("true"));

        c_2c_1Set = (config.getProperty("feature.c_2c_1", "true").equals("true"));
        c_1c0Set = (config.getProperty("feature.c_1c0", "true").equals("true"));
        c0c1Set = (config.getProperty("feature.c0c1", "true").equals("true"));
        c1c2Set = (config.getProperty("feature.c1c2", "true").equals("true"));

        c_1c1Set = (config.getProperty("feature.c_1c1", "true").equals("true"));

        t_2Set = (config.getProperty("feature.t_2", "true").equals("true"));
        t_1Set = (config.getProperty("feature.t_1", "true").equals("true"));

        c_2c0set = (config.getProperty("feature.c_2c0", "true").equals("true"));
        c_1c0c1set = (config.getProperty("feature.c_1c0c1", "true").equals("true"));

        c0prefix = (config.getProperty("feature.c0pre", "true").equals("true"));

        // 获取配置文件中的字典特征的设置值
        Lt0Set = (config.getProperty("feature.Lt0", "true").equals("true"));
        c_1t0Set = (config.getProperty("feature.c_1t0", "true").equals("true"));
        c0t0Set = (config.getProperty("feature.c0t0", "true").equals("true"));
        c1t0Set = (config.getProperty("feature.c1t0", "true").equals("true"));

        PuSet = (config.getProperty("feature.Pu", "true").equals("true"));
        TSet = (config.getProperty("feature.T", "true").equals("true"));
    }


    @Override
    public String[] getContext(int index, String[] tokens, String[] tags, Object[] ac)
    {
        return getContext(index, tokens, tags);
    }

    @Override
    public String toString()
    {
        return "WordSegContextGeneratorConf [c_2Set=" + c_2Set + ", c_1Set=" + c_1Set + ", c0Set=" + c0Set + ", c1Set=" + c1Set + ", c2Set=" + c2Set + ", c_2c_1Set=" + c_2c_1Set + ", c_1c0Set=" + c_1c0Set + ", c0c1Set=" + c0c1Set + ", c1c2Set=" + c1c2Set + ", c_1c1Set=" + c_1c1Set + ", t_2Set=" + t_2Set + ", t_1Set=" + t_1Set + ", c_2c0set=" + c_2c0set + ", c_1c0c1set=" + c_1c0c1set
                + ", c0prefix=" + c0prefix + ", Lt0Set=" + Lt0Set + ", c_1t0Set=" + c_1t0Set + ", c0t0Set=" + c0t0Set + ", c1t0Set=" + c1t0Set + ", PuSet=" + PuSet + ", TSet=" + TSet + "]";
    }

    public String[] getContext(int index, Object[] tokens, String[] tags)
    {
        String c1, c2, c3, c0, c_1, c_2, c_3;
        c1 = c2 = c3 = c0 = c_1 = c_2 = c_3 = null;
        String TC_1, TC_2, TC0, TC1, TC2;
        TC_1 = TC_2 = TC0 = TC1 = TC2 = null;

        String t_1 = null;
        String t_2 = null;

        c0 = tokens[index].toString();
        TC0 = FeaturesTools.featureType(c0);
        if (tokens.length > index + 1)
        {
            c1 = tokens[index + 1].toString();
            TC1 = FeaturesTools.featureType(c1);

            if (tokens.length > index + 2)
            {
                c2 = tokens[index + 2].toString();
                TC2 = FeaturesTools.featureType(c2);
                if (tokens.length > index + 3)
                    c3 = tokens[index + 3].toString();
            }
        }

        if (index - 1 >= 0)
        {
            c_1 = tokens[index - 1].toString();
            TC_1 = FeaturesTools.featureType(c_1);
            t_1 = tags[index - 1];

            if (index - 2 >= 0)
            {
                c_2 = tokens[index - 2].toString();
                t_2 = tags[index - 2];
                TC_2 = FeaturesTools.featureType(c_2);

                if (index - 3 >= 0)
                    c_3 = tokens[index - 3].toString();
            }
        }

        List<String> features = new ArrayList<String>();

        if (c0Set)
            features.add("c0=" + c0);

        if (c_1 != null)
        {
            if (c_1Set)
                features.add("c_1=" + c_1);

            if (t_1Set)
                features.add("t_1=" + t_1);

            if (c_2 != null)
            {
                if (c_2Set)
                    features.add("c_2=" + c_2);

                if (t_2Set)
                    features.add("t_2=" + t_2 + "," + t_1);

                if (c_2c0set)
                    features.add("c_2c0=" + c_2 + c0);
            }
        }

        if (c1 != null)
        {
            if (c1Set)
                features.add("c1=" + c1);
            
            if (c2 != null)
            {
                if (c2Set)
                    features.add("c2=" + c2);
            }
        }

        if (c_2 != null && c_1 != null)
        {
            if (c_2c_1Set)
                features.add("c_2c_1=" + c_2 + c_1);
        }

        if (c_1 != null)
        {
            if (c_1c0Set)
                features.add("c_1c0=" + c_1 + c0);
        }

        if (c1 != null)
        {
            if (c0c1Set)
                features.add("c0c1=" + c0 + c1);
        }

        if (c1 != null && c2 != null)
        {
            if (c1c2Set)
                features.add("c1c2=" + c1 + c2);
        }

        if (c_1 != null && c1 != null)
        {
            if (c_1c1Set)
                features.add("c_1c1=" + c_1 + c1);

            if (c_1c0c1set)
                features.add("c_1c0c1=" + c_1 + c0 + c1);
        }
        
        // 增加标点符号的特征【应用了全角转半角的strq2b方法】
        if (PuSet)
        {
            if (FeaturesTools.isChinesePunctuation(FeaturesTools.strq2b(c0)))
                features.add("Pu=" + 1);
            else
                features.add("Pu=" + 0);
        }
        
        // 增加是否为数字字母等特征
        if (TSet)
            features.add("T=" + TC_2 + TC_1 + TC0 + TC1 + TC2);
        
        // 下面是增加和词典匹配后特征的情况

        // 标志位:(1)如果匹配四字词成功则不需要匹配三字词，两字词
        // (2)如果匹配四字词不成功则需要匹配三字词，匹配三字词成功，则不需要匹配两字词
        boolean flagByThree = true;
        boolean flagByTwo = true;
        // 加入词典中提取出来的特征
        // 1.如果当前词左右两侧三个都不为空，则匹配四字词
        // (1)c_3c_2c_1c0
        if (c_3 != null && c_2 != null && c_1 != null && c0 != null)
        {
            if (isDictionalWords(c_3 + c_2 + c_1 + c0))
            {
                if (Lt0Set)
                {
                    features.add("Le=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1e=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0e=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1e=" + c1);
                }
                flagByThree = false;
                flagByTwo = false;
            }
        }
        // (2)c_2c_1c0c1与c_1c0c1c2记录的特征是一样的
        if (c_2 != null && c_1 != null && c0 != null && c1 != null)
        {
            if (isDictionalWords(c_2 + c_1 + c0 + c1))
            {
                if (Lt0Set)
                {
                    features.add("Lm=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1m=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0m=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1m=" + c1);
                }
                flagByThree = false;
                flagByTwo = false;
            }
        }
        // (3)c_2c_1c0c1与c_1c0c1c2记录的特征是一样的
        if (c_1 != null && c0 != null && c1 != null && c2 != null)
        {
            if (isDictionalWords(c_1 + c0 + c1 + c2))
            {
                if (Lt0Set)
                {
                    features.add("Lm=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1m=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0m=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1m=" + c1);
                }
                flagByThree = false;
                flagByTwo = false;
            }
        }
        // (4)c0c1c2c3
        if (c0 != null && c1 != null && c2 != null && c3 != null)
        {
            if (isDictionalWords(c0 + c1 + c2 + c3))
            {
                if (Lt0Set)
                {
                    features.add("Lb=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1b=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0b=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1b=" + c1);
                }
                flagByThree = false;
                flagByTwo = false;
            }
        }

        // 2.匹配三字词的情形
        // （1）c_2c_1c0
        if (c_2 != null && c_1 != null && c0 != null && flagByThree)
        {
            if (isDictionalWords(c_2 + c_1 + c0))
            {
                if (Lt0Set)
                {
                    features.add("Le=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1e=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0e=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1e=" + c1);
                }
                flagByTwo = false;
            }
        }
        // (2)c_1c0c1
        if (c_1 != null && c0 != null && c1 != null && flagByThree)
        {
            if (isDictionalWords(c_1 + c0 + c1))
            {
                if (Lt0Set)
                {
                    features.add("Lm=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1m=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0m=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1m=" + c1);
                }
                flagByTwo = false;
            }
        }
        // (3)c0c1c2
        if (c0 != null && c1 != null && c2 != null && flagByThree)
        {
            if (isDictionalWords(c0 + c1 + c2))
            {
                if (Lt0Set)
                {
                    features.add("Lb=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1b=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0b=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1b=" + c1);
                }
                flagByTwo = false;
            }
        }
        // 3.匹配两字的情形
        // （1）c_1c0
        if (c_1 != null && c0 != null && flagByTwo)
        {
            if (isDictionalWords(c_1 + c0))
            {
                if (Lt0Set)
                {
                    features.add("Le=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1e=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0e=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1e=" + c1);
                }
            }
        }
        // (2)c0c1
        if (c0 != null && c1 != null && flagByTwo)
        {
            if (isDictionalWords(c0 + c1))
            {
                if (Lt0Set)
                {
                    features.add("Lb=" + 4);
                }
                if (c_1t0Set)
                {
                    features.add("c_1b=" + c_1);
                }
                if (c0t0Set)
                {
                    features.add("c0b=" + c0);
                }
                if (c1t0Set)
                {
                    features.add("c1b=" + c1);
                }
            }
        }

        String[] contexts = features.toArray(new String[features.size()]);

        return contexts;
    }

    public boolean isDictionalWords(String words)
    {
        if (dictWords.contains(words))
            return true;
        else
            return false;
    }
}
