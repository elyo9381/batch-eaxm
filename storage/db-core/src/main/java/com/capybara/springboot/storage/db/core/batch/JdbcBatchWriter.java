package com.capybara.springboot.storage.db.core.batch;


import com.capybara.springboot.storage.db.core.JusoRoadCode;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.random.RandomGenerator;

public class JdbcBatchWriter implements ItemWriter<JusoRoadCode> {

    private static final Logger logger = LoggerFactory.getLogger(JdbcBatchWriter.class);
    private static final AtomicInteger totalCount = new AtomicInteger(0);
    private static final long startTime = System.currentTimeMillis();
    
    @Autowired
    private DataSource dataSource;

    private static final int MAX_BATCH_SIZE = 50; // H2에서 안전하게 처리할 수 있는 배치 크기

    @Override
    public void write(Chunk<? extends JusoRoadCode> chunk) throws Exception {
        List<? extends JusoRoadCode> items = chunk.getItems();
        if (items.isEmpty()) {
            return;
        }

        long chunkStartTime = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();

        try (Connection conn = dataSource.getConnection()) {
            // 성능 최적화를 위한 연결 속성 설정
            conn.setAutoCommit(false);

            // DB별 최적화 설정은 생략
            // H2 데이터베이스 사용중이므로 특별한 설정 필요 없음
            logger.debug("DB 연결: {}", conn.getMetaData().getDatabaseProductName());

            // 여러 작은 배치로 나누어 처리
            for (int batchStart = 0; batchStart < items.size(); batchStart += MAX_BATCH_SIZE) {
                int batchEnd = Math.min(batchStart + MAX_BATCH_SIZE, items.size());
                List<? extends JusoRoadCode> batchItems = items.subList(batchStart, batchEnd);

                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO juso_road_code (juso_road_code_id, road_name_code, road_name, road_name_roman, emdong_serial_no, " +
                        "sido, sido_roman, sigungu, sigungu_roman, emdong_name, emdong_roman, emdong_type, " +
                        "emdong_code, is_used, change_reason, change_history_info, announcement_date, deletion_date) VALUES ");

                for (int i = 0; i < batchItems.size(); i++) {
                    if (i > 0) {
                        sql.append(", ");
                    }
                    sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;

                    for (JusoRoadCode code : batchItems) {
                        pstmt.setLong(paramIndex++, code.getJusoRoadCodeId());
                        pstmt.setString(paramIndex++, code.getRoadNameCode());
                        pstmt.setString(paramIndex++, code.getRoadName());
                        pstmt.setString(paramIndex++, code.getRoadNameRoman());
                        pstmt.setString(paramIndex++, code.getEmdongSerialNo());
                        pstmt.setString(paramIndex++, code.getSido());
                        pstmt.setString(paramIndex++, code.getSidoRoman());
                        pstmt.setString(paramIndex++, code.getSigungu());
                        pstmt.setString(paramIndex++, code.getSigunguRoman());
                        pstmt.setString(paramIndex++, code.getEmdongName());
                        pstmt.setString(paramIndex++, code.getEmdongRoman());
                        pstmt.setString(paramIndex++, code.getEmdongType());
                        pstmt.setString(paramIndex++, code.getEmdongCode());
                        pstmt.setString(paramIndex++, code.getIsUsed() != null ? code.getIsUsed() : "0"); // Ensure isUsed is not null
                        pstmt.setString(paramIndex++, code.getChangeReason());
                        pstmt.setString(paramIndex++, code.getChangeHistoryInfo());
                        pstmt.setString(paramIndex++, code.getAnnouncementDate());
                        pstmt.setString(paramIndex++, code.getDeletionDate());
                    }

                    pstmt.executeUpdate();
                    logger.debug("서브 배치 실행: {}/{} 레코드", batchEnd, items.size());
                }
            }

            conn.commit();

            // 성능 통계 로깅
            int currentCount = totalCount.addAndGet(items.size());
            long chunkEndTime = System.currentTimeMillis();
            long chunkElapsedTime = chunkEndTime - chunkStartTime;
            long totalElapsedTime = chunkEndTime - startTime;

            logger.info("[{}] 청크 처리 완료: {}건, 소요시간: {}ms, 초당 처리량: {}/s, 누적: {}, 총 소요시간: {}s",
                    threadName,
                    items.size(),
                    chunkElapsedTime,
                    chunkElapsedTime > 0 ? (items.size() * 1000 / chunkElapsedTime) : 0,
                    currentCount,
                    totalElapsedTime / 1000);
        }
    }
}
