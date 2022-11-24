package com.spring.training.batch;

import com.spring.training.model.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {

    static final String ID = "id";
    static final String FIRST_NAME = "first_name";
    static final String LAST_NAME = "last_name";
    static final String EMAIL = "email";

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong(ID));
        person.setFirstName(rs.getString(FIRST_NAME));
        person.setLastName(rs.getString(LAST_NAME));
        person.setEmail(rs.getString(EMAIL));
        return person;
    }

}
