package com.capybara.springboot.storage.db.core;

import com.capybara.springboot.storage.db.CoreDbContextTest;
import com.capybara.springboot.storage.db.core.batch.JusoRoadCodeBatchJobRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class springbatchTest extends CoreDbContextTest {

    @Autowired
    private JusoRoadCodeBatchJobRunner batchJobRunner;

    @Autowired
    private JusoRoadCodeRepository jusoRoadCodeRepository;

    @Test
    public void testBatchImportRoadCodes() throws Exception {
        // 기존 데이터 삭제 (테스트 환경에서만)
        jusoRoadCodeRepository.deleteAll();

        // 배치 작업 실행
        batchJobRunner.runBatchJob();

//        List<JusoRoadCode> all = jusoRoadCodeRepository.findAll();

//        all.forEach(System.out::println);

        // 결과 확인
        long count = jusoRoadCodeRepository.count();
        System.out.println("총 " + count + "개의 도로명코드가 저장되었습니다.");

        // 필요한 경우 추가 검증 로직 작성
    }

}
