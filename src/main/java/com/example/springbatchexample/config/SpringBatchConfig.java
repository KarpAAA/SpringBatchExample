package com.example.springbatchexample.config;


import com.example.springbatchexample.entity.Person;
import com.example.springbatchexample.entity.PersonProcessor;
import com.example.springbatchexample.repo.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SpringBatchConfig {
    private final PersonRepository personRepository;
    private final PersonProcessor processor;

    @Bean
    public Job Job(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importUserJob", jobRepository)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Person, Person>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(processor)
                .writer(itemWriter())
                .build();
    }

    @Bean
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }


    @Bean(name = "jobLauncher")
    public JobLauncher getJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public FlatFileItemReader<Person> itemReader() {
        FlatFileItemReader<Person> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("C:\\Users\\ivank\\IdeaProjects\\SpringBatchExample\\src\\main\\resources\\people-1000.csv"));
        itemReader.setName("people-reader");
        itemReader.setLinesToSkip(2);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    @Bean
    public DefaultLineMapper<Person> lineMapper() {
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setNames("Index", "User Id", "First Name", "Last Name", "Sex", "Email", "Phone", "Date of birth", "Job Title");

//        BeanWrapperFieldSetMapper<Person> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
//        beanWrapperFieldSetMapper.setTargetType(Person.class);

        lineMapper.setLineTokenizer(delimitedLineTokenizer);
        lineMapper.setFieldSetMapper((fieldSet) -> {
            Person person = new Person();
            person.setId(null);
            person.setFirstName(fieldSet.readString("First Name"));
            person.setLastName(fieldSet.readString("Last Name"));
            person.setSex(fieldSet.readString("Sex"));
            person.setEmail(fieldSet.readString("Email"));
            person.setPhone(fieldSet.readString("Phone"));
            person.setBirthday(fieldSet.readDate("Date of birth"));
            person.setJob(fieldSet.readString("Job Title"));
            return person;
        });

        return lineMapper;
    }
    @Bean
    public RepositoryItemWriter<Person> itemWriter() {
        RepositoryItemWriter<Person> itemWriter = new RepositoryItemWriter<>();
        itemWriter.setRepository(personRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }


    //    @Bean(name = "transactionManager")
//    public PlatformTransactionManager getTransactionManager() {
//        return new ResourcelessTransactionManager();
//    }
//    @Bean(name = "taskExecutor1")
//    public TaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(5);
//        taskExecutor.setMaxPoolSize(10);
//        taskExecutor.afterPropertiesSet();
//        return taskExecutor;
//    }
}
