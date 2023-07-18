package com.example.springbatchexample.entity;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;


@Component
public class PersonProcessor implements ItemProcessor<Person,Person> {

    @Override
    public Person process(Person item) throws Exception {
        return item;
    }
}
