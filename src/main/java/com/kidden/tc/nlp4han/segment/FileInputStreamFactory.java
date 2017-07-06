package com.kidden.tc.nlp4han.segment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.util.InputStreamFactory;

public class FileInputStreamFactory implements InputStreamFactory
{
    private File file;

    public FileInputStreamFactory(File file) throws FileNotFoundException
    {
        if (!file.exists())
        {
            throw new FileNotFoundException("File '" + file + "' cannot be found");
        }
        this.file = file;
    }

    @Override
    public InputStream createInputStream() throws IOException
    {
        return new FileInputStream(file);
    }
}
