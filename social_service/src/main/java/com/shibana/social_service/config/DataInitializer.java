package com.shibana.social_service.config;

import com.shibana.social_service.entity.Privacy;
import com.shibana.social_service.enums.PrivacyLevel;
import com.shibana.social_service.repo.jpa.PrivacyRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DataInitializer implements ApplicationRunner {
    PrivacyRepo privacyRepo;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(privacyRepo.count()==0) {
            Privacy privatePrivacy = Privacy.builder()
                    .name(PrivacyLevel.PRIVATE)
                    .description("Only Owner can see this field")
                    .build();

            Privacy publicPrivacy = Privacy.builder()
                    .name(PrivacyLevel.PUBLIC)
                    .description("All Users can see this field")
                    .build();

            privacyRepo.save(privatePrivacy);
            privacyRepo.save(publicPrivacy);
        }
    }
}
