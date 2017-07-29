package com.lc.nlp4han.segment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 基于规则的中文断句器
 *
 * @author 刘小峰
 */
public class SentenceSegmenterRule implements SentenceSegmenter
{

    @Override
    public String[] segment(String text)
    {
        ArrayList<String> sentences = new ArrayList<String>();
        int start = 0;
        int pos = 0;
        int length = text.length();
        char c;
        char nextC;
        boolean inQuote = false;
        while (pos < length)
        {
            c = text.charAt(pos);

            if (c == '。' || c == '？' || c == '！')
            {
                if (pos >= length - 1) // 以句号、问号和感叹号结束文本，成句
                {
                    String sentence = text.substring(start, pos + 1);
                    sentences.add(sentence);

                    pos++;
                    start = pos;
                } else
                // 句号、问号和感叹号后还有内容
                {
                    nextC = text.charAt(pos + 1);

                    if (nextC == '”') // 句号、问号和感叹号在引号内，和引号一起成句
                    {
                        String sentence = text.substring(start, pos + 2);
                        sentences.add(sentence);

                        pos += 2;
                        start = pos;

                        inQuote = false;
                    } else if (nextC == '。' || nextC == '？' || nextC == '！') // 多个重复的。？！
                    {
                        pos++;
                    } else
                    {
                        if (!inQuote) // 句号、问号和感叹号不在引号内，成句
                        {
                            String sentence = text.substring(start, pos + 1);
                            sentences.add(sentence);

                            pos++;
                            start = pos;
                        } else // 寻找引号
                        {
                            pos++;
                        }
                    }
                }
            } else
            {
                if (c == '“')
                {
                    inQuote = true;
                } else if (c == '”')
                {
                    inQuote = false;
                }

                pos++;
            }
        }

        if (pos > start) // 还有剩余内容，成句
        {
            String sentence = text.substring(start, pos);
            sentences.add(sentence);
        }

        return sentences.toArray(new String[sentences.size()]);
    }
    
    private static void usage()
    {
        System.out.println(SentenceSegmenterRule.class.getName() + " <textFile> <encoding>");
    }

    public static void main(String[] args) throws IOException
    {
        if(args.length < 1)
        {
            usage();
            return;
        }
        String file = args[0];
        String encoding = args[1];

        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        SentenceSegmenter segmenter = new SentenceSegmenterRule();
        while ((line = reader.readLine()) != null)
        {
            String[] sentences = segmenter.segment(line);
            for (String sentence : sentences)
                System.out.println(sentence);
        }

        reader.close();
    }

}
