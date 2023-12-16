package com.mongodb.test.config;

import com.mongodb.test.util.DateUtil;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 配置事务管理器
 */
@Configuration
public class ServerConfig {

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    /**
     * 自定义转换
     * @param dbFactory
     * @param mongoMappingContext
     * @return
     */
    @Bean
    public MappingMongoConverter mongoConverter(MongoDatabaseFactory dbFactory,
                                                MongoMappingContext mongoMappingContext) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(dbFactory);
        MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);

        //MongoCustomConversions conversions = new MongoCustomConversions(Collections.emptyList());
        //MongoMappingContext context = new MongoMappingContext();
        //context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        //context.afterPropertiesSet();
        List<Converter<?, ?>> converters = new ArrayList<>();
        //converters.add(DateConverter.INSTANCE);
        mongoConverter.setCustomConversions(new CustomConversions(converters));

        //this is my customization
        mongoConverter.setMapKeyDotReplacement("_");
        mongoConverter.afterPropertiesSet();
        return mongoConverter;
    }

    //private enum DateConverter implements Converter<Date, String> {
    //    INSTANCE;
    //    @Override
    //    public String convert(Date date) {
    //        return DateUtil.format(date);
    //    }
    //}
}
