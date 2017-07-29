package com.lc.nlp4han.segment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * 字典装载器
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DictionaryLoader
{
    public static Set<String> getWords(String dictPath, String encoding) throws IOException
    {
        Set<String> wordSet = new HashSet<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dictPath)), encoding));
        String line = null;
        while ((line = br.readLine()) != null)
        {
            line = line.replaceAll("\\s", "").replaceAll("\n", "");
            if (line.length() != 1)
            {
                wordSet.add(line);
            }
        }
        
        br.close();

        return wordSet;
    }
    
    public static Set<String> getWords(InputStream dictStream, String encoding) throws IOException
    {
        Set<String> wordSet = new HashSet<>();

        BufferedReader br = new BufferedReader(new InputStreamReader(dictStream, encoding));
        String line = null;
        while ((line = br.readLine()) != null)
        {
            line = line.replaceAll("\\s", "").replaceAll("\n", "");
            if (line.length() != 1)
            {
                wordSet.add(line);
            }
        }
        
        br.close();

        return wordSet;
    }
}
