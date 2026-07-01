package com.shibana.post_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public RedisScript<String> toggleReactionScript() {
        ClassPathResource scriptSource = new ClassPathResource("scripts/toggle_reaction.lua");
        return RedisScript.of(scriptSource, String.class);
    }
}
