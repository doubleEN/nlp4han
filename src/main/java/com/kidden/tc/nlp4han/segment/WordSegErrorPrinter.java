package com.kidden.tc.nlp4han.segment;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 分词评价中输出错误的切分结果，帮助进行结果分析
 * 
 * @author 刘小峰
 */
public class WordSegErrorPrinter extends WordSegEvaluationMonitor
{

    private PrintStream errOut;

    public WordSegErrorPrinter(OutputStream out)
    {
        errOut = new PrintStream(out);
    }

    @Override
    public void missclassified(WordSegSample reference, WordSegSample prediction)
    {
        errOut.println(reference.toSample());
        errOut.println("[*]" + prediction.toSample());
    }
}
