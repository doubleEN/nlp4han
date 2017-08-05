package com.lc.nlp4han.pos;

/**
 * 基准词性标注器，作为比较基准使用
 * 
 * 每个词采用最常见的词性标注
 * 
 * @author 刘小峰
 *
 */
public class POSTaggerRef implements POSTagger
{
    private POSModelRef model;
    
    public POSTaggerRef(POSModelRef model)
    {
        this.model = model;
    }

    @Override
    public String[] tag(String[] sentence)
    {
        String tags[] = new String[sentence.length];
        
        for(int i=0; i<sentence.length; i++)
            tags[i] = model.getMostFreqTag(sentence[i]);
        
        return tags;
    }

}
