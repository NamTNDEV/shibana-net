package com.shibana.post_service.repo.jdbc_repo;

import com.github.f4b6a3.uuid.UuidCreator;
import com.shibana.post_service.messaging.dto.payloads.PostReactedPayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReactionJdbcRepository {
    JdbcTemplate jdbcTemplate;

    public void batchUpsert(List<PostReactedPayload> batch) {
        String sql = """
                    INSERT INTO reactions (id, target_id, author_id, reaction_type, target_type, created_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON CONFLICT (target_id, author_id)
                    DO UPDATE SET reaction_type = EXCLUDED.reaction_type
                """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostReactedPayload payload = batch.get(i);

                ps.setObject(1, UuidCreator.getTimeOrderedEpoch());
                ps.setObject(2, payload.getTargetId());
                ps.setObject(3, payload.getRequesterId());
                ps.setString(4, payload.getReactionType().toString());
                ps.setString(5, payload.getReactionTargetType().toString());
                ps.setTimestamp(6, Timestamp.from(Instant.now()));
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });
    }

    public void batchDelete(List<PostReactedPayload> payloads) {
        String sql = """
                DELETE FROM reactions WHERE target_id = ? AND author_id = ?
                """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PostReactedPayload p = payloads.get(i);
                ps.setObject(1, p.getTargetId());
                ps.setObject(2, p.getRequesterId());
            }

            @Override
            public int getBatchSize() {
                return payloads.size();
            }
        });
    }
}
