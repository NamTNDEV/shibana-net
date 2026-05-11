package com.shibana.post_service.service;

import com.shibana.post_service.exception.AppException;
import com.shibana.post_service.messaging.dto.payloads.AvatarUpdatedPayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostEventSyncService {
//    MongoTemplate mongoTemplate;

    public void syncAuthorAvatar(List<AvatarUpdatedPayload> payloads) {
        CompletableFuture<Void> postFuture = CompletableFuture.runAsync(() -> syncAvatarToPosts(payloads));
        CompletableFuture<Void> commentFuture = CompletableFuture.runAsync(() -> syncAvatarToComments(payloads));
        try {
            CompletableFuture.allOf(postFuture, commentFuture).join();
        } catch (Exception e) {
            log.error(":: ❌ Lỗi nghiêm trọng khi đồng bộ song song: {} ::", e.getMessage());
            throw e;
        }
    }

    private void syncAvatarToPosts(List<AvatarUpdatedPayload> payloads) {
//        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Post.class);
//        Instant now = Instant.now();
//        for (AvatarUpdatedPayload payload : payloads) {
//            Query query = new Query(Criteria.where("author.userId").is(payload.getUserId()));
//            Update update = new Update()
//                    .set("author.avatarMediaName", payload.getAvatarMediaName())
//                    .set("author.avatarScale", payload.getAvatarScale())
//                    .set("author.avatarPositionX", payload.getAvatarPositionX())
//                    .set("author.avatarPositionY", payload.getAvatarPositionY())
//                    .set("updatedAt", now);
//
//            bulkOps.updateMulti(query, update);
//        }

//        try {
//            bulkOps.execute();
//        } catch (BulkOperationException e) {
//            log.error(":: ❌ Lỗi BulkWrite POST: {} bản ghi hỏng ::", e.getErrors().size());
//            e.getErrors().forEach(error ->
//                    log.warn(":: Chi tiết lỗi DB POST ở index {}: {} ::", error.getIndex(), error.getMessage())
//            );
//        }
    }

    private void syncAvatarToComments(List<AvatarUpdatedPayload> payloads) {
//        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Comment.class);
//        Instant now = Instant.now();
//        for (AvatarUpdatedPayload payload : payloads) {
//            Query query = new Query(Criteria.where("author.userId").is(payload.getUserId()));
//            Update update = new Update()
//                    .set("author.avatarMediaName", payload.getAvatarMediaName())
//                    .set("author.avatarScale", payload.getAvatarScale())
//                    .set("author.avatarPositionX", payload.getAvatarPositionX())
//                    .set("author.avatarPositionY", payload.getAvatarPositionY())
//                    .set("updatedAt", now);
//
//            bulkOps.updateMulti(query, update);
//        }

//        try {
//            bulkOps.execute();
//        } catch (BulkOperationException e) {
//            log.error(":: ❌ Lỗi BulkWrite COMMENT: {} bản ghi hỏng ::", e.getErrors().size());
//            e.getErrors().forEach(error ->
//                    log.warn(":: Chi tiết lỗi DB COMMENT ở index {}: {} ::", error.getIndex(), error.getMessage())
//            );
//        }
    }
}
