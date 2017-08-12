package com.lc.nlp4han.segment;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 * 分词交叉验证器
 * 
 * @author 刘小峰
 * 
 */
public class WordSegCrossValidatorTool
{

    private final String languageCode;

    private final TrainingParameters params;

    private WordSegEvaluationMonitor[] listeners;

    /**
     * 构建交叉验证器
     * 
     * @param languageCode
     *            语言代码
     * @param trainParam
     *            训练参数配置
     * @param listeners
     *            评价监视器
     */
    public WordSegCrossValidatorTool(String languageCode, TrainingParameters trainParam, WordSegEvaluationMonitor... listeners)
    {
        this.languageCode = languageCode;
        this.params = trainParam;
        this.listeners = listeners;
    }

    /**
     * 交叉验证
     * 
     * @param samples
     *            训练和测试的样本
     * @param nFolds
     *            交叉验证的折数
     * @param contextGenerator
     *            训练和分词的上下文产生器
     * 
     * @throws IOException
     */
    public void evaluate(ObjectStream<WordSegSample> samples, int nFolds, WordSegContextGenerator contextGenerator) throws IOException
    {
        CrossValidationPartitioner<WordSegSample> partitioner = new CrossValidationPartitioner<>(samples, nFolds);

        int run = 1;
        while (partitioner.hasNext())
        {
            System.out.println("Run " + run + "...");
            CrossValidationPartitioner.TrainingSampleStream<WordSegSample> trainingSampleStream = partitioner.next();
            
            System.out.println("从样本构建词典...");
            HashSet<String> dict = WordSegmenterME.buildDict(trainingSampleStream);
            
            System.out.println("训练分词模型...");
            trainingSampleStream.reset();
            WordSegModel model = WordSegmenterME.train(languageCode, trainingSampleStream, params, contextGenerator);

            System.out.println("评价分词模型...");
            WordSegMeasure measure = new WordSegMeasure(dict);
            WordSegEvalTool evaluator = new WordSegEvalTool(new WordSegmenterME(model, contextGenerator), listeners);
            evaluator.setMeasure(measure);
            evaluator.evaluate(trainingSampleStream.getTestSampleStream());
            
            System.out.println(measure);

            run++;
        }

//        System.out.println(measure);
    }

    private static void usage()
    {
        System.out.println(WordSegCrossValidatorTool.class.getName() + " -data <corpusFile> -encoding <encoding> [-folds <nFolds>] " + "[-context <contextGenClass>] [-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        int cutoff = 3;
        int iters = 100;
        int folds = 10;
        String contextClass = "com.lc.nlp4han.segment.DefaultWordSegContextGenerator";
        File corpusFile = null;
        String encoding = "UTF-8";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-cutoff"))
            {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-context"))
            {
                contextClass = args[i + 1];
                i++;
            }
            else if (args[i].equals("-iters"))
            {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-folds"))
            {
                folds = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));

        WordSegContextGenerator contextGenerator = (WordSegContextGenerator) Class.forName(contextClass).newInstance();
        System.out.println(contextGenerator);

        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(corpusFile), encoding);
        ObjectStream<WordSegSample> sampleStream = new WordTagSampleStream(lineStream);


        WordSegCrossValidatorTool crossValidator = new WordSegCrossValidatorTool("zh", params);

        crossValidator.evaluate(sampleStream, folds, contextGenerator);
    }
}
