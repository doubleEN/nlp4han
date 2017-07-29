
package com.lc.nlp4han.segment;

import org.junit.Test;

import com.lc.nlp4han.segment.SentenceSegmenter;
import com.lc.nlp4han.segment.SentenceSegmenterRule;

import static org.junit.Assert.*;

/**
 * SentenceSegmenterRule单元测试
 * 
 * @author 刘小峰
 */
public class SentenceSegmenterRuleTest
{
    @Test
    public void testSegment()
    {
        SentenceSegmenter segmenter = new SentenceSegmenterRule();
        
        String text1 = "我喜欢“NLP”。你喜欢吗？NLP非常有趣啊！作为一个研究者，学习不能停止";    
        String[] sentences = segmenter.segment(text1);
        assertEquals(4, sentences.length);
        assertEquals("我喜欢“NLP”。", sentences[0]);
        assertEquals("你喜欢吗？", sentences[1]);
        assertEquals("NLP非常有趣啊！", sentences[2]);
        assertEquals("作为一个研究者，学习不能停止", sentences[3]);
        
        String text2 = "妈妈说：“做个好孩子！好好学习。”“做个有志向的人。”爸爸说";
        sentences = segmenter.segment(text2);
        assertEquals(3, sentences.length);
        assertEquals("妈妈说：“做个好孩子！好好学习。”", sentences[0]);
        assertEquals("“做个有志向的人。”", sentences[1]);
        assertEquals("爸爸说", sentences[2]);
        
        String text3 = "大家小心呀！！！汽车很危险。";
        sentences = segmenter.segment(text3);
        assertEquals(2, sentences.length);
        assertEquals("大家小心呀！！！", sentences[0]);
        assertEquals("汽车很危险。", sentences[1]);
    }

    
}
