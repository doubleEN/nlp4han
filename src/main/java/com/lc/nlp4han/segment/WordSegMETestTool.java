package com.lc.nlp4han.segment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 最大熵分词测试工具
 * 
 * @author kidden
 */
public class WordSegMETestTool
{
    private static void usage()
    {
        System.out.println(WordSegMETestTool.class.getName() + " -data <textFile> -model <modelFile> -encoding <encoding> [-out <resultFile>] [-context <contextGenClass>]");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        File modelFile = null;
        File textFile = null;
        File resultFile = null;
        String contextClass = "com.lc.nlp4han.segment.DefaultWordSegContextGenerator";
        String encoding = "UTF-8";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                textFile = new File(args[i + 1]);
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
            else if (args[i].equals("-context"))
            {
                contextClass = args[i + 1];
                i++;
            }
            else if (args[i].equals("-out"))
            {
                resultFile = new File(args[i + 1]);
                i++;
            }
        }

        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), encoding));
        WordSegContextGenerator contextGenerator = (WordSegContextGenerator) Class.forName(contextClass).newInstance();
        WordSegmenterME segmenter = new WordSegmenterME(modelFile, contextGenerator);
        PrintWriter out = new PrintWriter(System.out);
        if(resultFile != null)
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(resultFile), encoding));
        while ((line = reader.readLine()) != null)
        {
            String[] words = segmenter.segment(line);
            for (String word : words)
               out.print(word + " ");
            out.println();
        }
        out.close();
        reader.close();
    }
}
