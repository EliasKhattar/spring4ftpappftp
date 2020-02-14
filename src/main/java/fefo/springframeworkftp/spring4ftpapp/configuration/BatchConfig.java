package fefo.springframeworkftp.spring4ftpapp.configuration;

import fefo.springframeworkftp.spring4ftpapp.batchreaders.OrderItemProcessingPeekReader;
import fefo.springframeworkftp.spring4ftpapp.components.JobCompletionNotificationListener;
import fefo.springframeworkftp.spring4ftpapp.model.ItemDetail;
import fefo.springframeworkftp.spring4ftpapp.model.Order;
import fefo.springframeworkftp.spring4ftpapp.model.OrderDetail;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private EntityManagerFactory emf;

    @Bean
    public Step orderStep() throws Exception {
        return stepBuilderFactory.get("orderStep")
                .<Order,Order>chunk(5)
                //.reader(reader(null))
                .reader(readerPeeks(null))
                //.writer(writer(null))
                .writer(jpaItemWriter())
                .build();

    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step sampleStep) {
        return jobBuilderFactory.get("orderJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(sampleStep)
                .end()
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader readers(@Value("#{jobParameters[file_path]}") String filePath) throws Exception {

        final FileSystemResource fileResource = new FileSystemResource(filePath);
        return new FlatFileItemReaderBuilder()
                .name("ordersItemReader")
                .resource(fileResource)
                .lineMapper(orderLineMapper())
                .build();
    }

    @Bean
    public SingleItemPeekableItemReader<Order> readerPeek() throws Exception {
        SingleItemPeekableItemReader<Order> reader = new SingleItemPeekableItemReader<Order>() {{
            setDelegate(readers(null));
        }};
        return reader;
    }
/*
    @Bean
    @StepScope
    public OrderItemProcessingItemReader reader(@Value("#{jobParameters[file_path]}") String filePath) throws Exception {

        final FileSystemResource fileResource = new FileSystemResource(filePath);
        String decNo = fileResource.getFilename().substring(0,12);
        OrderItemProcessingItemReader orderItemProcessingItemReader = new OrderItemProcessingItemReader(decNo);
        orderItemProcessingItemReader.setFieldSetReader(readers(null));
        orderItemProcessingItemReader.setDelegate(readerPeek());
        //orderItemProcessingItemReader.setDelegate(orderItemProcessingItemReader.getFieldSetReader());
        //orderItemProcessingItemReader.setDelegate(readers(null));

        return orderItemProcessingItemReader;
    }*/

    @Bean
    @StepScope
    public OrderItemProcessingPeekReader readerPeeks(@Value("#{jobParameters[file_path]}") String filePath) throws Exception {

        final FileSystemResource fileResource = new FileSystemResource(filePath);
        String branchCode = fileResource.getFilename().substring(0,12);
        OrderItemProcessingPeekReader orderItemProcessingPeekReader = new OrderItemProcessingPeekReader(readers(null),branchCode);

        return orderItemProcessingPeekReader;
    }

    @Bean
    @Primary
    public JpaItemWriter jpaItemWriter(){
        JpaItemWriter writer = new JpaItemWriter();
        writer.setEntityManagerFactory(emf);
        return writer;
    }


   /* @Bean
    @StepScope
    public ItemWriter<Order> writer(@Value("#{jobParameters[file_path]}") String filePath) throws Exception {
        final FileSystemResource fileResource = new FileSystemResource(filePath);
        String branchCode = fileResource.getFilename().substring(5,8);
        return new JpaOrderItemWriter(branchCode);
    }*/


    @Bean
    public PatternMatchingCompositeLineMapper orderLineMapper() throws Exception {

        PatternMatchingCompositeLineMapper mapper = new PatternMatchingCompositeLineMapper();

        Map<String, LineTokenizer> tokenizers = new HashMap<String, LineTokenizer>();
        tokenizers.put("ORD*",orderTokenizer() );
        tokenizers.put("DET*",orderItemTokenizer());

        mapper.setTokenizers(tokenizers);

        Map<String, FieldSetMapper> mappers = new HashMap<String, FieldSetMapper>();
        mappers.put("ORD*", orderFieldSetMapper());
        mappers.put("DET*", orderLineFieldSetMapper());

        mapper.setFieldSetMappers(mappers);

        return mapper;

    }


    @Bean
    public LineTokenizer orderTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames(new String[] { "ord", "eiclientNumber", "clientOrderNumber", "consigneeP", "paymentTerms", "earliestShipDate", "latestShipDate", "consigneeCode", "billToCode", "thirdPartyBillToCustomerNo", "preferredCarrier", "usd1","usd2"});
        return tokenizer;
    }

    @Bean
    public FieldSetMapper<OrderDetail> orderFieldSetMapper() throws Exception {
        BeanWrapperFieldSetMapper<OrderDetail> mapper =
                new BeanWrapperFieldSetMapper<OrderDetail>();

        mapper.setPrototypeBeanName("orderDetail");
        mapper.afterPropertiesSet();
        return mapper;
    }


    @Bean
    @Scope("prototype")
    public OrderDetail orderDetail() {
        return new OrderDetail();
    }

    @Bean
    public LineTokenizer orderItemTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames(new String[] { "detail", "lineNumber", "skuNumber", "qty", "lotNo", "receiptPo", "usd1", "usd2", "usd3", "usd4", "usd5"});
        return tokenizer;
    }

    @Bean
    public FieldSetMapper<ItemDetail> orderLineFieldSetMapper() throws Exception {
        BeanWrapperFieldSetMapper<ItemDetail> mapper =
                new BeanWrapperFieldSetMapper<ItemDetail>();

        mapper.setPrototypeBeanName("itemDetail");
        mapper.afterPropertiesSet();
        return mapper;
    }

    @Bean
    @Scope("prototype")
    public ItemDetail itemDetail() {
        return new ItemDetail();
    }
    
}
