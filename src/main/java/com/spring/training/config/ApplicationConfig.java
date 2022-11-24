package com.spring.training.config;

import com.spring.training.batch.JobNotificationListener;
import com.spring.training.batch.PersonItemProcessor;
import com.spring.training.batch.PersonRowMapper;
import com.spring.training.model.Message;
import com.spring.training.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemWriterBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfig {

    static final String KAFKA_TOPIC = "javainuse";

    @Bean
    public ItemReader<Person> reader(DataSource dataSource) {
        JdbcCursorItemReader itemReader = new JdbcCursorItemReader();
        itemReader.setDataSource(dataSource);
        itemReader.setSaveState(false);
        itemReader.setSql("select id, first_name, last_name, email from persons");
        itemReader.setRowMapper(new PersonRowMapper());
        return itemReader;
    }

    @Bean
    public ItemWriter<Message> writer(KafkaTemplate<String, Message> kafkaTemplate) {
        kafkaTemplate.setDefaultTopic(KAFKA_TOPIC);
        return new KafkaItemWriterBuilder<String, Message>().
                kafkaTemplate(kafkaTemplate).
                itemKeyMapper(message -> "email-batch")
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, ItemReader<Person> reader, ItemWriter<Message> writer) {
        return stepBuilderFactory.get("step")
                .<Person, Message>chunk(10)
                .reader(reader)
                .processor(new PersonItemProcessor(batchConfig()))
                .writer(writer)
                .build();
    }

    @Bean
    @ConfigurationProperties("configuration")
    public BatchConfig batchConfig() {
        return new BatchConfig();
    }

    @Bean
    public Job job(JobBuilderFactory factory, JobNotificationListener listener, Step step) {
        return factory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step)
                .end()
                .build();
    }

}