package api.service.EmailService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AwsClientConfig {

    private final Region region = Region.AP_NORTHEAST_2;

    /**
     * SES Client 생성 및 Bean 등록
     *
     * @return SesClient
     */
    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create("ses-profile"))
                .build();
    }

    /**
     * DynamoDB Client 생성 및 Bean 등록
     *
     * @return DynamoDbClient
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create("dynamodb-profile"))
                .build();
    }
}
