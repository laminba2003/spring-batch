package com.spring.training.batch;

import com.spring.training.model.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {

    static final String ID_COLUMN = "id";
    static final String FIRST_NAME_COLUMN = "first_name";
    static final String LAST_NAME_COLUMN = "last_name";
    static final String EMAIL_COLUMN = "email";

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong(ID_COLUMN));
        person.setFirstName(rs.getString(FIRST_NAME_COLUMN));
        person.setLastName(rs.getString(LAST_NAME_COLUMN));
        person.setEmail(rs.getString(EMAIL_COLUMN));
        return person;
    }

}
