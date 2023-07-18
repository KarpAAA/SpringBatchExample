package com.example.springbatchexample;

import com.example.springbatchexample.entity.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringBatchExampleApplication {

    private final Job job;
    private final JobLauncher jobLauncher;

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchExampleApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addDate("startAt", new Date(System.currentTimeMillis()))
                    .toJobParameters();
            jobLauncher.run(job,jobParameters);
        };
    }

}
