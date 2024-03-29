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
        JdbcCursorItemReader reader = new JdbcCursorItemReader();
        reader.setDataSource(dataSource);
        reader.setSaveState(false);
        reader.setSql("select * from persons");
        reader.setRowMapper(new PersonRowMapper());
        return reader;
    }

    @Bean
    public ItemWriter<Message> writer(KafkaTemplate<String, Message> kafkaTemplate) {
        kafkaTemplate.setDefaultTopic(KAFKA_TOPIC);
        return new KafkaItemWriterBuilder<String, Message>().
                kafkaTemplate(kafkaTemplate).
                itemKeyMapper(Message::getTo)
                .build();
    }

    @Bean
    public Step step(StepBuilderFactory factory, ItemReader<Person> reader, ItemWriter<Message> writer) {
        return factory.get("step")
                .<Person, Message>chunk(10)
                .reader(reader)
                .processor(new PersonItemProcessor(batchConfig()))
                .writer(writer)
                .build();
    }

    @Bean
    @ConfigurationProperties("batch.configuration")
    public BatchConfig batchConfig() {
        return new BatchConfig();
    }

    @Bean
    public Job job(JobBuilderFactory factory, Step step) {
        return factory.get("job")
                .incrementer(new RunIdIncrementer())
                .listener(new JobNotificationListener())
                .flow(step)
                .end()
                .build();
    }

}