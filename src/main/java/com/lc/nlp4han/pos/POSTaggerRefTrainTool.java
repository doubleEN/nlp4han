package com.lc.nlp4han.pos;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 基准词性标注器训练程序
 * 
 * @author 刘小峰
 * 
 */
public class POSTaggerRefTrainTool
{
    private static final String WOTD_TAG_SEP = "_";

    /**
     * 训练基准词性标注模型
     * 
     * @param trainFile
     *            词性标注语料文件
     * @param encoding
     *            语料文件编码
     * @return 基准词性标注模型
     * @throws IOException
     */
    public static POSModelRef train(File trainFile, String encoding) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(trainFile), encoding));
        String sentence = null;
        HashMap<String, Integer> tag2Count = new HashMap<String, Integer>();
        HashMap<String, HashMap<String, Integer>> word2TagCount = new HashMap<String, HashMap<String, Integer>>();
        while ((sentence = in.readLine()) != null)
        {
            String[] wordtags = sentence.split("\\s");

            for (int i = 0; i < wordtags.length; i++)
            {
                String wordtag = wordtags[i];

                int pos = wordtag.lastIndexOf(WOTD_TAG_SEP);
                if (pos < 0)
                    continue;

                String word = wordtag.substring(0, pos);
                String tag = wordtag.substring(pos + 1);

                int tagCount = 1;
                if (tag2Count.containsKey(tag))
                {
                    tagCount = tag2Count.get(tag);
                    tagCount++;
                    tag2Count.put(tag, tagCount);
                }
                else
                    tag2Count.put(tag, tagCount);

                if (word2TagCount.containsKey(word))
                {
                    HashMap<String, Integer> tag2CountByWord = word2TagCount.get(word);

                    int tagCountByWord = 1;
                    if (tag2CountByWord.containsKey(tag))
                    {
                        tagCountByWord = tag2CountByWord.get(tag);
                        tagCountByWord++;
                        tag2CountByWord.put(tag, tagCountByWord);
                    }
                    else
                        tag2CountByWord.put(tag, tagCountByWord);
                }
                else
                {
                    HashMap<String, Integer> tag2CountByWord = new HashMap<String, Integer>();
                    tag2CountByWord.put(tag, 1);
                    word2TagCount.put(word, tag2CountByWord);
                }

            }

        }

        in.close();

        Map.Entry<String, Integer> e = biggestEntry(tag2Count);

        String mostFreqTag = "";
        if (e != null)
            mostFreqTag = e.getKey();

        HashMap<String, String> word2MostFreqTag = new HashMap<String, String>();
        for (Map.Entry<String, HashMap<String, Integer>> element : word2TagCount.entrySet())
        {
            String word = element.getKey();
            HashMap<String, Integer> counts = element.getValue();

            e = biggestEntry(counts);
            String freqTag = "";
            if (e != null)
                freqTag = e.getKey();

            word2MostFreqTag.put(word, freqTag);
        }

        return new POSModelRef(word2MostFreqTag, mostFreqTag);
    }
    
    public static void train(File trainFile, String encoding, File modelFile) throws IOException
    {
        POSModelRef model = train(trainFile, encoding);
        
        DataOutput out = new DataOutputStream(new FileOutputStream(modelFile));
        model.write(out);
    }

    private static Map.Entry<String, Integer> biggestEntry(HashMap<String, Integer> elements)
    {
        Map.Entry<String, Integer> result = null;
        for (Map.Entry<String, Integer> e : elements.entrySet())
        {
            if (result == null)
            {
                result = e;
                continue;
            }

            if (e.getValue() > result.getValue())
                result = e;
        }

        return result;
    }

    private static void usage()
    {
        System.out.println(POSTaggerRefTrainTool.class.getName() + " -data <corpusFile> -model <modelFile> [-encoding <encoding>]");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        File corpusFile = null;
        File modelFile = null;
        String encoding = "UTF-8";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-model"))
            {
                modelFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
        }
        
        train(corpusFile, encoding, modelFile);

    }

}
