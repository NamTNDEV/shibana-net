package com.shibana.post_service.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 1. Khởi tạo đối tượng RedisTemplate cốt lõi của Spring để tương tác với Redis
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 2. Cắm "Nhà máy kết nối" (Connection Factory) vào template.
        // Thằng này chính là LettuceConnectionFactory được Spring tạo tự động từ application.yml
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // =========================================================================
        // PHẦN 1: CẤU HÌNH BỘ MÃ HÓA CHO KEY (KEY SERIALIZER)
        // Mặc định Spring dùng JDK Serializer làm Key bị dính ký tự rác nhị phân (\xac\xed...)
        // Sử dụng StringRedisSerializer để ép Key luôn là một chuỗi String UTF-8 sạch sẽ.
        // =========================================================================
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // Áp dụng bộ mã hóa chuỗi sạch cho các Key thông thường (String, Set, List)
        redisTemplate.setKeySerializer(stringRedisSerializer);

        // =========================================================================
        // PHẦN 2: CẤU HÌNH BỘ MÃ HÓA CHO VALUE DẠNG JSON (VALUE SERIALIZER)
        // Sử dụng thư viện Jackson để tự động biến đổi Object Java thành chuỗi JSON
        // khi lưu xuống Redis và ngược lại (Không cần viết code parse tay).
        // =========================================================================
        ObjectMapper objectMapper = new ObjectMapper();

        // Cấu hình cho phép Jackson truy cập vào TẤT CẢ các thuộc tính của Class Java (kể cả private field)
        // thông qua Reflection để đảm bảo không bỏ sót dữ liệu nào khi chuyển đổi.
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 🚨 DÒNG CHÍ MẠNG: Kích hoạt cơ chế "Định danh kiểu dữ liệu mặc định" (Default Typing).
        // Khi lưu Object (vd: PostDTO) xuống Redis, Jackson sẽ tự động nhét thêm một trường ẩn tên là "@class".
        // Nhờ trường này, khi đọc dữ liệu từ Redis lên RAM, Spring Boot sẽ biết chính xác cục JSON này
        // thuộc về Class Java nào để ép kiểu ngược lại một cách tự động, chống lỗi ClassCastException.
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        // Gói toàn bộ cấu hình ObjectMapper ở trên vào bộ Serializer chuyên dụng của Spring dành cho Jackson
        Jackson2JsonRedisSerializer<Object>  jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // Áp dụng bộ mã hóa JSON này cho tất cả các giá trị (Value) thông thường khi lưu xuống Redis
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        // Áp dụng bộ mã hóa JSON này cho tất cả các giá trị nằm bên trong cấu trúc dữ liệu Hash
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // =========================================================================
        // PHẦN 3: HOÀN TẤT VÀ CHỐT CẤU HÌNH
        // Gọi hàm này để RedisTemplate tự kiểm tra lại toàn bộ các tham số đã nạp cấu hình.
        // Nếu không có lỗi gì, nó sẽ chính thức "khóa chốt" và sẵn sàng thực chiến.
        // =========================================================================
        redisTemplate.afterPropertiesSet();

        log.info("✅ RedisTemplate bean created successfully.");
        return redisTemplate;
    }
}
