package com.capybara.springboot.storage.db.core;


import com.capybara.springboot.storage.db.CoreDbContextTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JusoRoadCodeRepositoryTest extends CoreDbContextTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 또는 DataSource를 직접 사용할 경우
    @Autowired
    private DataSource dataSource;


    private final JusoRoadCodeRepository jusoRoadCodeRepository;

    public JusoRoadCodeRepositoryTest(JusoRoadCodeRepository jusoRoadCodeRepository) {
        this.jusoRoadCodeRepository = jusoRoadCodeRepository;
    }

    @Test
    public void testReadAndSaveRoadCodes() throws Exception {
        // 파일 경로 설정 (테스트 리소스 폴더 내의 파일)
        File file = new ClassPathResource("개선_도로명코드_전체분.txt").getFile();
        List<JusoRoadCode> roadCodes = new ArrayList<>();

        // 여러 인코딩을 시도해봅니다
        String[] encodings = {"MS949"};

        for (String encoding : encodings) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), encoding))) {

                String line;
                // 첫 줄을 읽어서 제대로 인코딩이 되었는지 확인합니다
                if ((line = reader.readLine()) != null) {
                    System.out.println("인코딩 " + encoding + "로 읽은 첫 줄: " + line);

                    // 한글이 제대로 보이는지 확인합니다
                    if (line.contains("?") || line.contains("�")) {
                        System.out.println(encoding + " 인코딩은 적합하지 않습니다.");
                        continue; // 다음 인코딩 시도
                    }

                    // 올바른 인코딩을 찾았으면 파일을 처음부터 다시 읽습니다
                    reader.close();
                    readAndParseFile(file, encoding, roadCodes);
                    break; // 정상적으로 파일을 읽었으면 인코딩 시도 종료
                }
            } catch (Exception e) {
                System.out.println(encoding + " 인코딩으로 읽기 실패: " + e.getMessage());
            }
        }

        // 데이터베이스에 저장
        if (!roadCodes.isEmpty()) {
//            int total = roadCodes.size();
//
//            final int BATCH_SIZE = 1000;
//
//            for (int i = 0; i < total; i += BATCH_SIZE) {
//                int end = Math.min(i + BATCH_SIZE, total);
//                List<JusoRoadCode> batch = roadCodes.subList(i, end);
//                jusoRoadCodeRepository.saveAll(batch);
//                // 명시적 flush와 clear로 영속성 컨텍스트 관리
//                jusoRoadCodeRepository.flush();
//                // EntityManager를 주입받았다면: entityManager.clear();
//            }


            int total = roadCodes.size();
            final int BATCH_SIZE = 1000; // 배치 크기 증가

            // JDBC batch insert 사용
            jdbcBatchInsert(roadCodes, BATCH_SIZE);
            System.out.println(roadCodes.size() + "개의 도로명코드를 저장했습니다.");
        } else {
            System.out.println("파싱된 데이터가 없습니다. 인코딩 문제를 확인하세요.");
        }
    }





    private void jdbcBatchInsert(List<JusoRoadCode> roadCodes, int batchSize) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            for (int i = 0; i < roadCodes.size(); i += batchSize) {
                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO juso_road_code (juso_road_code_id, road_name_code, road_name, road_name_roman, emdong_serial_no, " +
                        "sido, sido_roman, sigungu, sigungu_roman, emdong_name, emdong_roman, emdong_type, " +
                        "emdong_code, is_used, change_reason, change_history_info, announcement_date, deletion_date) VALUES ");

                int end = Math.min(i + batchSize, roadCodes.size());
                for (int j = i; j < end; j++) {
                    if (j > i) {
                        sql.append(", ");
                    }
                    sql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    long idCounter = i + 1; // ID는 1부터 시작

                    for (int j = i; j < end; j++) {
                        JusoRoadCode code = roadCodes.get(j);
                        pstmt.setLong(paramIndex++, idCounter++);
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
                        pstmt.setString(paramIndex++, code.getIsUsed());
                        pstmt.setString(paramIndex++, code.getChangeReason());
                        pstmt.setString(paramIndex++, code.getChangeHistoryInfo());
                        pstmt.setString(paramIndex++, code.getAnnouncementDate());
                        pstmt.setString(paramIndex++, code.getDeletionDate());
                    }

                    pstmt.executeUpdate();
                    System.out.println((end - i) + "건 벌크 INSERT 완료");
                }

                conn.commit();
            }

            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("SQL 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void readAndParseFile(File file, String encoding, List<JusoRoadCode> roadCodes) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), encoding))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // 파이프(|)로 구분된 데이터 파싱
                String[] fields = line.split("\\|", -1);


                if (fields.length >= 16) { // 필요한 필드 수 확인
                    JusoRoadCode roadCode = new JusoRoadCode();

                    // UUID를 사용하여 ID 생성 (실제 환경에서는 다른 전략을 사용할 수 있음)
//                    roadCode.setJusoRoadCodeId(Math.abs(UUID.randomUUID().getMostSignificantBits()));

                    // 필드 매핑 (순서에 주의)
                    roadCode.setRoadNameCode(fields[0]);
                    roadCode.setRoadName(fields[1]);
                    roadCode.setRoadNameRoman(fields[2]);
                    roadCode.setEmdongSerialNo(fields[3]);
                    roadCode.setSido(fields[4]);
                    roadCode.setSidoRoman(fields[5]);
                    roadCode.setSigungu(fields[6]);
                    roadCode.setSigunguRoman(fields[7]);
                    roadCode.setEmdongName(fields[8]);
                    roadCode.setEmdongRoman(fields[9]);
                    roadCode.setEmdongType(fields[10]);
                    roadCode.setEmdongCode(fields[11]);
                    roadCode.setIsUsed(fields[12]);
                    roadCode.setChangeReason(fields.length > 13 ? fields[13] : null);
                    roadCode.setChangeHistoryInfo(fields.length > 14 ? fields[14] : null);
                    roadCode.setAnnouncementDate(fields.length > 15 ? fields[15] : null);
                    roadCode.setDeletionDate(fields.length > 16 ? fields[16] : null);

                    roadCodes.add(roadCode);

                    // 처리 중인 데이터 확인 (처음 몇 개만)
                    if (roadCodes.size() <= 5) {
                        System.out.println("파싱된 데이터: " + roadCode.getRoadName());
                    }
                }
            }
        }
    }
}