package com.anbu.batch.start;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


/**
 * Created by anbganapathy on 8/23/2016.
 */

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public FlatFileItemReader<Person> reader()
    {

        final FlatFileItemReader<Person> flatFileItemReader =new FlatFileItemReader<Person>();
        flatFileItemReader.setResource(new ClassPathResource("sample.csv"));
        flatFileItemReader.setLineMapper(new DefaultLineMapper<Person>(){{
            setLineTokenizer(new DelimitedLineTokenizer(){{
                setNames(new String[] {"firstName","lastName"});
            }});

            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>(){{
                setTargetType(Person.class);
            }});

        }});
        return flatFileItemReader;
    }

    @Bean
    public PersonItemProcessor processor()
    {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer()
    {
        JdbcBatchItemWriter<Person> jdbcBatchItemWriter=new JdbcBatchItemWriter<Person>();
        jdbcBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
        jdbcBatchItemWriter.setSql("Insert into people (first_Name,last_Name) values (:firstName, :lastName)");
        jdbcBatchItemWriter.setDataSource(dataSource);
        return jdbcBatchItemWriter;
    }

    @Bean
    public JobExecutionListener listener()
    {
        return new JobCompletionNotificationListener(new JdbcTemplate(dataSource));
    }

    @Bean
    public Job importUserJob()
    {

        return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener()).flow(step1()).end().build();
    }

    @Bean
    public Step step1()
    {
        return stepBuilderFactory.get("step1").<Person,Person>chunk(10).reader(reader()).processor(processor()).writer(writer()).build();
    }
}
