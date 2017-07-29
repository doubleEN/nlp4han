package com.lc.nlp4han.segment;

import java.io.IOException;

import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.Sequence;
import opennlp.tools.ml.model.SequenceStream;
import opennlp.tools.util.ObjectStream;

public class WordSegSampleSequenceStream implements SequenceStream {

    private WordSegContextGenerator pcg;
    private ObjectStream<WordSegSample> psi;

    public WordSegSampleSequenceStream(ObjectStream<WordSegSample> psi) throws IOException {
        this(psi, new DefaultWordSegContextGenerator());
    }

    public WordSegSampleSequenceStream(ObjectStream<WordSegSample> psi, WordSegContextGenerator pcg)
            throws IOException {
        this.psi = psi;
        this.pcg = pcg;
    }

    @SuppressWarnings("unchecked")
    public Event[] updateContext(Sequence sequence, AbstractModel model) {
        Sequence<WordSegSample> pss = sequence;
        WordSegmenterME tagger = new WordSegmenterME(new WordSegModel("x-unspecified", model, null));
        String[] sentence = pss.getSource().getSentence();
        Object[] ac = pss.getSource().getAddictionalContext();
        String[] tags = tagger.tag(pss.getSource().getSentence());
        Event[] events = new Event[sentence.length];
        
        WordSegSampleEventStream.generateEvents(sentence, tags, ac, pcg)
                .toArray(events);
        
        return events;
    }

    @Override
    public Sequence read() throws IOException {
        WordSegSample sample = psi.read();

        if (sample != null) {
            String sentence[] = sample.getSentence();
            String tags[] = sample.getTags();
            Event[] events = new Event[sentence.length];

            for (int i = 0; i < sentence.length; i++) {

                // it is safe to pass the tags as previous tags because
                // the context generator does not look for non predicted tags
                String[] context = pcg.getContext(i, sentence, tags, null);

                events[i] = new Event(tags[i], context);
            }
            
            Sequence<WordSegSample> sequence = new Sequence<WordSegSample>(events, sample);
            return sequence;
        }

        return null;
    }

    @Override
    public void reset() throws IOException, UnsupportedOperationException {
        psi.reset();
    }

    @Override
    public void close() throws IOException {
        psi.close();
    }
}
