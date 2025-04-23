package com.capybara.springboot.storage.db.core.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class JusoRoadCodeItemReader implements ItemReader<String[]> {

    private static final Logger logger = LoggerFactory.getLogger(JusoRoadCodeItemReader.class);
    private static final int PREFETCH_COUNT = 10000; // 미리 읽을 라인 수

    private Resource resource;
    private String encoding = "UTF-8";
    private BufferedReader reader;
    private final List<String[]> prefetchedLines = new CopyOnWriteArrayList<>();
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private boolean initialized = false;
    private boolean endOfFile = false;

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void afterPropertiesSet() throws Exception {
        if (resource == null) {
            throw new IllegalStateException("Resource must be set");
        }
    }

    private synchronized void initialize() throws Exception {
        if (!initialized) {
            reader = new BufferedReader(
                    new DefaultBufferedReaderFactory()
                            .create(resource, String.valueOf(Charset.forName(encoding))), 81920); // 버퍼 크기 증가 (8KB → 80KB)
            prefetchLines();
            initialized = true;
        }
    }

    private void prefetchLines() throws Exception {
        if (endOfFile) {
            return;
        }

        long startTime = System.currentTimeMillis();
        List<String[]> newLines = new ArrayList<>(PREFETCH_COUNT);
        String line;
        int count = 0;

        while (count < PREFETCH_COUNT && (line = reader.readLine()) != null) {
            newLines.add(line.split("\\|", -1));
            count++;
        }

        if (count < PREFETCH_COUNT) {
            endOfFile = true;
            reader.close();
        }

        prefetchedLines.addAll(newLines);
        long endTime = System.currentTimeMillis();
        logger.info("Prefetched {} lines in {}ms", count, (endTime - startTime));
    }

    @Override
    public String[] read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!initialized) {
            initialize();
        }

        int index = currentIndex.getAndIncrement();

        // 미리 읽은 데이터의 절반을 소비하면 추가 데이터 프리페치
        if (!endOfFile && index >= prefetchedLines.size() / 2 && index % 100 == 0) {
            prefetchLines();
        }

        if (index < prefetchedLines.size()) {
            return prefetchedLines.get(index);
        }

        return null; // 모든 데이터 소비 완료
    }
}
