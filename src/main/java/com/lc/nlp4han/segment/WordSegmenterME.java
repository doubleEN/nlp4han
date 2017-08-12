package com.lc.nlp4han.segment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventModelSequenceTrainer;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

/**
 * 基于最大熵的中文分词器
 *
 * @author 刘小峰
 */
public class WordSegmenterME implements WordSegmenter
{

    public static final int DEFAULT_BEAM_SIZE = 3;

    private WordSegModel modelPackage;

    /**
     * 上下文产生器
     */
    protected WordSegContextGenerator contextGen;

    /**
     * 决定最佳分词序列的搜索bean数
     */
    protected int size;

    private Sequence bestSequence;

    private SequenceClassificationModel<String> model;

    private SequenceValidator<String> sequenceValidator;

    /**
     * 由分词模型构建分词器
     *
     * @param model 分词模型
     */
    public WordSegmenterME(WordSegModel model)
    {
        this(model, new DefaultWordSegContextGenerator());
    }

    /**
     *
     * @param model 分词模型
     * @param contextGenerator 分词上下文产生器
     */
    public WordSegmenterME(WordSegModel model, WordSegContextGenerator contextGenerator)
    {
        init(model, contextGenerator);

    }
    
    private void init(WordSegModel model, WordSegContextGenerator contextGenerator)
    {
        int beamSize = WordSegmenterME.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null)
        {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGen = contextGenerator;
        size = beamSize;

        sequenceValidator = new DefaultWordSegSequenceValidator();

        if (model.getWordsegSequenceModel() != null)
        {
            this.model = model.getWordsegSequenceModel();
        } else
        {
            this.model = new opennlp.tools.ml.BeamSearch<String>(beamSize,
                    model.getWordsegModel(), 0);
        }
    }
    
    /**
     * 由分词模型文件构建分词器
     * 
     * @param modelFile 分词模型文件
     */
    public WordSegmenterME(File modelFile) throws IOException
    {
        this(modelFile, new DefaultWordSegContextGenerator());
    }
    
    /**
     * 由分词模型文件构建分词器
     * 
     * @param modelFile 分词模型文件
     * @param contextGenerator 分词上下文产生器
     */
    public WordSegmenterME(File modelFile, WordSegContextGenerator contextGenerator) throws IOException
    {
        WordSegModel model = new WordSegModel(modelFile);
        
        init(model, contextGenerator);
        
    }

    /**
     * 获得所有可能位置标记
     *
     * @return 位置标记集
     */
    public String[] getAllPositionTags()
    {
        return model.getOutcomes();
    }

    String[] tag(String[] sentence)
    {
        return this.tag(sentence, null);
    }

    /**
     * 对句子中的字进行分词位置标注
     *
     * @param sentence 待切分的句子
     *
     * @return 每个字的位置标记
     */
    public String[] tag(String sentence)
    {
        String[] chars = new String[sentence.length()];

        for (int i = 0; i < sentence.length(); i++)
        {
            chars[i] = sentence.charAt(i) + "";
        }

        return tag(chars, null);
    }

    /**
     * 对句子中的字进行分词位置标注
     *
     * @param sentence 待切分的句子
     * @param additionaContext 每个字的额外上下文
     *
     * @return 每个字的位置标记
     */
    public String[] tag(String sentence, Object[] additionaContext)
    {
        String[] chars = toCharArray(sentence);

        return tag(chars, additionaContext);
    }
    
    private String[] toCharArray(String sentence)
    {
        String[] chars = new String[sentence.length()];

        for (int i = 0; i < sentence.length(); i++)
        {
            chars[i] = sentence.charAt(i) + "";
        }
        
        return chars;
    }

    public String[] tag(String[] sentence, Object[] additionaContext)
    {
        bestSequence = model.bestSequence(sentence, additionaContext, contextGen, sequenceValidator);
        List<String> t = bestSequence.getOutcomes();
        return t.toArray(new String[t.size()]);
    }

    /**
     * 对待切分的句子返回多个标记（或切分）序列
     *
     * @param numTaggings 返回的切分数
     * @param text 待切分句子
     *
     * @return 待切分的句子的多个标记（或切分）序列
     */
    public String[][] tag(int numTaggings, String text)
    {
        String[] sentence = toCharArray(text);
        
        Sequence[] bestSequences = model.bestSequences(numTaggings, sentence, null,
                contextGen, sequenceValidator);
        String[][] tags = new String[bestSequences.length][];
        for (int si = 0; si < tags.length; si++)
        {
            List<String> t = bestSequences[si].getOutcomes();
            tags[si] = t.toArray(new String[t.size()]);
        }
        return tags;
    }

    /**
     * 获得句子的多个最好切分结果
     * 
     * 个数由bean搜索个数确定，缺省为3
     * 
     * @param text 待切分句子
     * @return 句子的多个切分序列
     */
    public Sequence[] topKSequences(String text)
    {      
        return this.topKSequences(text, null);
    }

    /**
     * 获得句子的多个最好切分结果
     * 
     * 个数由bean搜索个数确定，缺省为3
     * 
     * @param text 待切分句子
     * @param additionaContext 句子中每个字的额外上下文
     * @return 句子的多个切分序列
     */
    public Sequence[] topKSequences(String text, Object[] additionaContext)
    {
        String[] sentence = toCharArray(text);
        
        return model.bestSequences(size, sentence, additionaContext, contextGen, sequenceValidator);
    }

    /**
     * 获得每个字的标记的概率
     *
     * @param probs 填充的概率
     */
    void probs(double[] probs)
    {
        bestSequence.getProbs(probs);
    }

    /**
     * 获得每个字的标记的概率
     *
     * @return 每个字的标记的概率
     */
    double[] probs()
    {
        return bestSequence.getProbs();
    }

    @Override
    public String[] segment(String text)
    {
        String[] tags = tag(text);

        String word = new String();
        ArrayList<String> words = new ArrayList<String>();
        for (int i = 0; i < tags.length; i++)
        {
            word += text.charAt(i);

            if (tags[i].equals("S") || tags[i].equals("E"))
            {
                words.add(word);
                word = "";
            }

        }

        if (word.length() > 0)
        {
            words.add(word);
        }

        return words.toArray(new String[words.size()]);
    }
    
    /**
     * 从分词样本流中构建词典
     * 
     * @param samples 分词样本流
     * @return 样本的词典
     * @throws IOException
     */
    public static HashSet<String> buildDict(ObjectStream<WordSegSample> samples) throws IOException
    {
        HashSet<String> dict = new HashSet<String>();
        WordSegSample sample = null;
        while((sample = samples.read()) != null)
        {
            String[] words = sample.toWords();
            
            for(String word : words)
                dict.add(word);
        }
        
        return dict;
    }

    public static WordSegModel train(String languageCode,
            ObjectStream<WordSegSample> samples, TrainingParameters trainParams) throws IOException
    {
        return train(languageCode, samples, trainParams, new DefaultWordSegContextGenerator());
    }

    /**
     * 训练最大熵分词模型
     * 
     * @param languageCode 语言代码
     * @param samples 样本流
     * @param trainParams 最大熵训练参数
     * @param contextGenerator 上下文产生器
     * 
     * @return 最大熵分词模型
     * @throws IOException 
     */
    public static WordSegModel train(String languageCode,
            ObjectStream<WordSegSample> samples, TrainingParameters trainParams,
            WordSegContextGenerator contextGenerator) throws IOException
    {

        String beamSizeString = trainParams.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

        int beamSize = WordSegmenterME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null)
        {
            beamSize = Integer.parseInt(beamSizeString);
        }

        //WordSegContextGenerator contextGenerator = new DefaultWordSegContextGenerator();
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();

        TrainerType trainerType = TrainerFactory.getTrainerType(trainParams.getSettings());

        MaxentModel posModel = null;
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
        {
            ObjectStream<Event> es = new WordSegSampleEventStream(samples, contextGenerator);

            EventTrainer trainer = TrainerFactory.getEventTrainer(trainParams.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(es);
        } else if (TrainerType.EVENT_MODEL_SEQUENCE_TRAINER.equals(trainerType))
        {
            WordSegSampleSequenceStream ss = new WordSegSampleSequenceStream(samples, contextGenerator);
            EventModelSequenceTrainer trainer = TrainerFactory.getEventModelSequenceTrainer(trainParams.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(ss);
        } else if (TrainerType.SEQUENCE_TRAINER.equals(trainerType))
        {
            SequenceTrainer trainer = TrainerFactory.getSequenceModelTrainer(
                    trainParams.getSettings(), manifestInfoEntries);

            // TODO: This will probably cause issue, since the feature generator uses the outcomes array
            WordSegSampleSequenceStream ss = new WordSegSampleSequenceStream(samples, contextGenerator);
            seqPosModel = trainer.train(ss);
        } else
        {
            throw new IllegalArgumentException("Trainer type is not supported: " + trainerType);
        }

        if (posModel != null)
        {
            return new WordSegModel(languageCode, posModel, beamSize, manifestInfoEntries);
        } else
        {
            return new WordSegModel(languageCode, seqPosModel, manifestInfoEntries);
        }
    }

}
