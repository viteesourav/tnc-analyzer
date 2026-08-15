package com.tnc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
    This class acts as an interface for Java to Redis. Handles data interpretion for keys and values.
*/

@Configuration
public class RedisConfig {

    @SuppressWarnings("null")
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory connectionFactory,
        ObjectMapper objectMapper
    ) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // connectionFactory bean is injected automatically and pass as args to this method.
        template.setConnectionFactory(connectionFactory);

        // Keys will be stored as plain string.
        template.setKeySerializer(new StringRedisSerializer());

        // Values will be stored as JSON. -> supports debugging and human-readable format.
        template.setValueSerializer(
            new GenericJackson2JsonRedisSerializer(objectMapper)
        );

        // Hash Keys will be stoed as plain string.
        template.setHashKeySerializer(new StringRedisSerializer());

        // Hash Value will be also JSON.
        template.setHashValueSerializer(
            new GenericJackson2JsonRedisSerializer(objectMapper)
        );

        template.afterPropertiesSet();

        return template;
        
    }
    
}

/*
   Notes: 
    -- Notice here we are using @Configuration and @Bean keyword for the Redis template.
    -- Why ?
        -> This annotation together tells Spring's IOC [Inversion of Control] contianer to instanciate, configure and manage third-party or custom objects as Spring Beans.
        -> these beans you can @Autowired / Inject them anywhere in the application.
    
    1. @configuration (Class Level)
        -- This annotation marks the class as factory method.
        -- It means: During the service startup, it send Spring signal like: "Hey, read this class on startup, it contains instruction on how to instantiate and configure" 

    2. @bean (Method level)
        -- It tells spring, that the object retured by this method should be register as bean in the spring Application Context.
        -- usecase:
            1. Explict object creation:
                You use @Bean when auto-detection (@Component, @Service, @Repository) isn't possible—such as when configuring third-party classes from external libraries like RedisTemplate.
            2. Method name as Bean identifier:
                Bydefault, name of the method becomes the name of the registered bean in the container.
            3. Automatic Dependency injection:
                Spring automatically checks the container, find existing RedisConnectionFactory and ObjectMapper beans, and pass them as arguments in the method which building it.
    
    -- Flow: 
        Spring Application Starts Up
            └─> Scans for @Configuration classes
                └─> Finds RedisConfig
                    └─> Executes redisTemplate(...) method marked with @Bean
                            ├─> Injects RedisConnectionFactory & ObjectMapper dependencies
                            ├─> Configures String & JSON serializers
                            └─> Stores the fully assembled RedisTemplate in the Spring Container

    -- How we are making sure the serialization is consistent.
        -> "StringRedisSerializer" -> Make sures, the serialization is consistent.
        -> "GenericJackson2JsonRedisSerializer"  -> Make sure the serialization is not defaulted to Java's binary serializtion.
        -> we are using serialization for HashKey and HashValue.
        -> If we miss this, then Spring defaults it to Java's default binarySerializer(JdkSerializationRedisSerializer).
        -> eg: 
            # Without Hash Serializers:
            \xac\xed\x00\x05t\x00\x07hash123 -> \xac\xed\x00\x05sr\x00\x18com.tnc.dto.AnalyzeResponse...

            # With Hash Serializers:
            "hash123" -> "{\"safetyScore\": 95, \"summary\": \"...\"}"



*/
