package com.shibana.media_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "medias")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Media {
    @Id
    String id; // MongoDB sẽ tự hiểu đây là ObjectId

    @Field("file_name")
    @Indexed(unique = true) // Đánh index để tìm kiếm file vật lý cực nhanh
    String fileName;

    @Field("original_name")
    String originalName;

    @Field("url")
    String url; // Đường dẫn ảo: /media/uuid.png

    @Field("content_type")
    String contentType;

    @Field("size")
    Long size;

    @Field("created_at")
    Instant createdAt;

    @Field("owner_id")
    @Indexed // Index để sau này lấy danh sách file của 1 user nhanh hơn
    String ownerId;

    @Field("storage_type")
    String storageType; // LOCAL, S3, v.v.

    @Field("checksum")
    @Indexed // Quan trọng để check trùng file nhanh
    String checksum;

//    @Field("access_type")
//    String accessType; // PUBLIC, PRIVATE
}
