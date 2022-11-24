package com.spring.training.batch;

import com.spring.training.config.BatchConfig;
import com.spring.training.model.Message;
import com.spring.training.model.Person;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@AllArgsConstructor
public class PersonItemProcessor implements ItemProcessor<Person, Message> {

    final BatchConfig config;

    @Override
    public Message process(Person person) {
        Message message = new Message(config.getEmail(), person.getEmail(), config.getMessage());
        log.info("Converting (" + person + ") into (" + message + ")");
        return message;
    }

}
