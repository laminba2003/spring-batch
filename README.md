# Spring Batch

Many applications within the enterprise domain require bulk processing to perform business operations in mission-critical environments. These business operations include:

* Automated, complex processing of large volumes of information that is most efficiently processed without user interaction. These operations typically include time-based events (such as month-end calculations, notices, or correspondence).

* Periodic application of complex business rules processed repetitively across very large data sets (for example, insurance benefit determination or rate adjustments).

* Integration of information that is received from internal and external systems that typically requires formatting, validation, and processing in a transactional manner into the system of record. Batch processing is used to process billions of transactions every day for enterprises.

Spring Batch is a lightweight, comprehensive batch framework designed to enable the development of robust batch applications that are vital for the daily operations of enterprise systems. Spring Batch builds upon the characteristics of the Spring Framework that people have come to expect (productivity, POJO-based development approach, and general ease of use), while making it easy for developers to access and use more advanced enterprise services when necessary. Spring Batch is not a scheduling framework. There are many good enterprise schedulers (such as Quartz, Tivoli, Control-M, and others) available in both the commercial and open source spaces. Spring Batch is intended to work in conjunction with a scheduler rather than replace a scheduler.

Spring Batch provides reusable functions that are essential in processing large volumes of records, including logging and tracing, transaction management, job processing statistics, job restart, skip, and resource management. It also provides more advanced technical services and features that enable extremely high-volume and high performance batch jobs through optimization and partitioning techniques. You can use Spring Batch in both simple use cases (such as reading a file into a database or running a stored procedure) and complex, high volume use cases (such as moving high volumes of data between databases, transforming it, and so on). High-volume batch jobs can use the framework in a highly scalable manner to process significant volumes of information.

## Business Scenarios

Spring Batch supports the following business scenarios:

* Commit batch process periodically.

* Concurrent batch processing: parallel processing of a job.

* Staged, enterprise message-driven processing.

* Massively parallel batch processing.

* Manual or scheduled restart after failure.

* Sequential processing of dependent steps (with extensions to workflow-driven batches).

* Partial processing: skip records (for example, on rollback).

* Whole-batch transaction, for cases with a small batch size or existing stored procedures or scripts.

## ItemReaders and ItemWriters

All batch processing can be described in its most simple form as reading in large amounts of data, performing some type of calculation or transformation, and writing the result out. Spring Batch provides three key interfaces to help perform bulk reading and writing: **ItemReader**, **ItemProcessor**, and **ItemWriter**.

## Messaging Readers and Writers

Spring Batch offers the following readers and writers for commonly used messaging systems:

* AmqpItemReader
* AmqpItemWriter
* JmsItemReader
* JmsItemWriter
* KafkaItemReader
* KafkaItemWriter

## Setup
```java
@Configuration
public class ApplicationConfig {

    static final String KAFKA_TOPIC = "javainuse";

    @Bean
    public ItemReader<Person> reader(DataSource dataSource) {
        JdbcCursorItemReader itemReader = new JdbcCursorItemReader();
        itemReader.setDataSource(dataSource);
        itemReader.setSaveState(false);
        itemReader.setSql("select * from persons");
        itemReader.setRowMapper(new PersonRowMapper());
        return itemReader;
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
```

## Start the Kafka broker

run this command to start all services in the correct order.

```bash
$ docker-compose up -d
```

## Create a topic

Run this command to create a new topic into which we’ll write the messages.

```bash
$ docker exec broker kafka-topics --bootstrap-server broker:9092 --create --topic javainuse
```

## Read messages from the topic

Now that we’ve written messages to the topic, we’ll read those messages back. Run this command to launch the kafka-console-consumer. The --from-beginning argument means that messages will be read from the start of the topic.

```bash
$ docker exec --interactive --tty broker kafka-console-consumer --bootstrap-server broker:9092 --topic javainyse --from-beginning
```

## Stop the Kafka broker

```bash
docker-compose down
```
