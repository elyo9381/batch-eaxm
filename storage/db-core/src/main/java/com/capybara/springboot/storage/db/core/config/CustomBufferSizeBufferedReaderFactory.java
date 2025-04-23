package com.capybara.springboot.storage.db.core.config;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CustomBufferSizeBufferedReaderFactory implements BufferedReaderFactory {

    private final int bufferSize;
    private String encoding = StandardCharsets.UTF_8.name(); // 기본값

    public CustomBufferSizeBufferedReaderFactory(int bufferSize) {
        Assert.isTrue(bufferSize > 0, "Buffer size must be positive");
        this.bufferSize = bufferSize;
    }

    // 필요시 인코딩 설정 생성자 추가 가능
    public CustomBufferSizeBufferedReaderFactory(int bufferSize, String encoding) {
        this(bufferSize);
        this.encoding = encoding;
    }

    @Override
    public BufferedReader create(Resource resource, String encoding) throws IOException {
        // FlatFileItemReaderBuilder에서 설정한 인코딩이 우선 적용됨
        String effectiveEncoding = (encoding != null) ? encoding : this.encoding;
        Charset charset = Charset.forName(effectiveEncoding); // 올바른 Charset 객체 생성
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), charset), this.bufferSize);
    }
}