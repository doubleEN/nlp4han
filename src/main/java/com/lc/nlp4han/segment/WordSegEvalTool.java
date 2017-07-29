package com.lc.nlp4han.segment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.eval.Evaluator;


/**
 * 基于{@link WordSegSample}的分词评价工具
 * 
 * @author 刘小峰
 *
 */
public class WordSegEvalTool extends Evaluator<WordSegSample>
{

    private WordSegmenterME tagger;

    private WordSegMeasure measure = new WordSegMeasure();

    public WordSegEvalTool(WordSegmenterME tagger, WordSegEvaluationMonitor... listeners)
    {
        super(listeners);
        this.tagger = tagger;
    }

    public void setMeasure(WordSegMeasure m)
    {
        this.measure = m;
    }
    
    public WordSegMeasure getMeasure()
    {
        return measure;
    }

    /**
     * 根据参考分词{@link WordSegSample}样本进行评价
     * 
     * 对参考样本用系统进行切分，并更新分词指标
     * 
     * @param reference
     *            参考切分
     * 
     * @return 系统切分
     */
    @Override
    protected WordSegSample processSample(WordSegSample reference)
    {
        String predictedTags[] = tagger.tag(reference.getSentence(), reference.getAddictionalContext());

        WordSegSample predictions = new WordSegSample(reference.getSentence(), predictedTags);

        measure.updateScores(reference.toWords(), predictions.toWords());

        return predictions;
    } 

    /**
     * 依据黄金切分标准评价分词效果
     * 
     * 各种评价指标结果会输出到控制台，分词错误的结果会输出到指定文件
     * 
     * @param modelFile 系统分词模型文件
     * @param goldFile 黄金分词标准文件
     * @param errorFile 系统分词错误输出文件
     * @param contextGenerator 上下文产生器
     * @param encoding 黄金分词标准文件编码
     * @throws IOException
     */
    public static void eval(File modelFile, File goldFile, File errorFile, WordSegContextGenerator contextGenerator, String encoding) throws IOException
    {
        InputStream modelIn = new FileInputStream(modelFile);
        WordSegModel model = new WordSegModel(modelIn);
        WordSegmenterME tagger = new WordSegmenterME(model, contextGenerator);
        
        WordSegErrorPrinter errorMonitor = new WordSegErrorPrinter(new FileOutputStream(errorFile));
        WordSegEvalTool evaluator = new WordSegEvalTool(tagger, errorMonitor);

        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(goldFile), encoding);
        ObjectStream<WordSegSample> sampleStream = new WordTagSampleStream(lineStream);

        evaluator.evaluate(sampleStream);

        WordSegMeasure f = evaluator.getMeasure();
        System.out.println(f);
    }

    /**
     * 依据黄金切分标准评价分词效果
     * 
     * 各种评价指标结果会输出到控制台
     * 
     * @param modelFile 系统分词模型文件
     * @param goldFile 黄金分词标准文件
     * @param contextGenerator 上下文产生器
     * @param encoding 黄金分词标准文件编码
     * @throws IOException
     */
    public static void eval(File modelFile, File goldFile, WordSegContextGenerator contextGenerator, String encoding) throws IOException
    {
        InputStream modelIn = new FileInputStream(modelFile);
        WordSegModel model = new WordSegModel(modelIn);
        WordSegmenterME tagger = new WordSegmenterME(model, contextGenerator);
        WordSegEvalTool evaluator = new WordSegEvalTool(tagger);

        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(goldFile), encoding);
        ObjectStream<WordSegSample> sampleStream = new WordTagSampleStream(lineStream);

        evaluator.evaluate(sampleStream);

        WordSegMeasure f = evaluator.getMeasure();
        System.out.println(f);
    }
    
    private static void usage()
    {
        System.out.println(WordSegEvalTool.class.getName() + " -model <modelFile> -gold <goldFile> -encoding <encoding> [-error <errorFile>] [-context <contextGenClass>]");
    }

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }
        
        String modelFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = null;
        String contextClass = "com.lc.nlp4han.segment.DefaultWordSegContextGenerator";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-model"))
            {
                modelFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-gold"))
            {
                goldFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-error"))
            {
                errorFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-context"))
            {
                contextClass = args[i + 1];
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
        }

//        WordSegContextGenerator contextGenerator = new DefaultWordSegContextGenerator();
        WordSegContextGenerator contextGenerator = (WordSegContextGenerator) Class.forName(contextClass).newInstance();

        if (errorFile != null)
        {
            eval(new File(modelFile), new File(goldFile), new File(errorFile), contextGenerator, encoding);
        }
        else
            eval(new File(modelFile), new File(goldFile), contextGenerator, encoding);
    }
}
