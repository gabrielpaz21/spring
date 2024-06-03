package com.example.config;

import com.example.model.Customer;
import com.example.processor.CustomerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BatchConfig.class);

    public final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    @Value("${app.file.input}")
    private String customersFile;

    public BatchConfig(DataSource dataSource,
                       JdbcTemplate jdbcTemplate,
                       JobRepository jobRepository,
                       PlatformTransactionManager platformTransactionManager) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.jobRepository=jobRepository;
        this.platformTransactionManager=platformTransactionManager;
    }

    // reading customers.csv
    @Bean
    public FlatFileItemReader<Customer> reader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .resource(new ClassPathResource(customersFile))
                .delimited()
                .names("first_name", "last_name", "email", "gender", "ip_address")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Customer.class);
                }})
                .linesToSkip(1) // skips the first line of the CSV, which is the header
                .build();
    }

    @Bean
    CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    // loading data into customers table
    @Bean
    public JdbcBatchItemWriter<Customer> writer(){
        return new JdbcBatchItemWriterBuilder<Customer>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("""
                        INSERT INTO customers
                        (first_name, last_name, email, gender, ip_address)
                        VALUES
                        (:firstName, :lastName, :email, :gender, :ipAddress)
                        """)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job job1(){
        return new JobBuilder("job1", jobRepository)
                .incrementer(new RunIdIncrementer())
//                .listener(customerListener())
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    // Delete data from the customer table
    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LOG.info("Deleting data from the customers table");
                    jdbcTemplate.update("DELETE FROM customers");
                    return RepeatStatus.FINISHED;
                },platformTransactionManager)
                .build();
    }

    // read customers and insert into customer table
    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .<Customer, Customer> chunk(10, platformTransactionManager)
                .reader(reader()) // read csv
                .processor(processor()) // process
                .writer(writer()) // save to db
                .build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .<Customer, Customer> chunk(10, platformTransactionManager)
                .reader(reader()) // read csv
                .processor(processor()) // process
                .writer(writer()) // save to db
                .build();
    }

}
