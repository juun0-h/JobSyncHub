package api.service.EmailService.service;

import api.service.EmailService.repository.DynamoDbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {

    @Value("${aws.ses.sender}")
    private String sender;
    @Value("${aws.dynamodb.table_1}")
    private String codeTable;

    private final SesClient sesClient;
    private final DynamoDbRepository dynamoDbRepository;
    private final SpringTemplateEngine springTemplateEngine;

    private static final Map<String, String> expressionAttributeNames = ImmutableMap.of(
            "#pk", "email",
            "#ea", "expireAt"
    );

    /**
     * 인증 이메일 전송(비동기)
     * 인증 코드 생성 후 DynamoDB에 저장
     * 이메일 템플릿 생성 후 전송
     *
     * @param email 수신자 이메일
     * @param title 이메일 제목
     * @return CompletableFuture<String> 비동기 처리 결과
     */
    @Async
    public CompletableFuture<String> sendAuthEmail(String email, String title) {
        String code = createRandomCode();
        Map<String, AttributeValue> item = new ConcurrentHashMap<>();

        item.put("email", AttributeValue.builder().s(email).build());
        item.put("code", AttributeValue.builder().s(code).build());
        item.put("expireAt", AttributeValue.builder().n(String.valueOf(Instant.now().getEpochSecond() + 300)).build());

        PutItemResponse response = dynamoDbRepository.putItem(PutItemRequest.builder()
                .tableName(codeTable)
                .item(item)
                .build());

        // 이메일 템플릿 생성
        String emailContent = springTemplateEngine.process("verify-email-template.html", setContext(code));
        // 이메일 전송
        try {
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .source(sender)
                    .destination(Destination.builder().toAddresses(email).build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .data(title)
                                    .charset("UTF-8")
                                    .build())
                            .body(Body.builder()
                                    .html(Content.builder()
                                            .data(emailContent)
                                            .charset("UTF-8")
                                            .build())
                                    .build())
                            .build())
                    .build();
            sesClient.sendEmail(emailRequest);
            log.info("verification email sent: [sender] {} -> [receiver] {}", sender, email);
        } catch (SesException e) {
            log.error("failed to send authentication email: {}", e.awsErrorDetails().errorMessage());
            throw e;
        }

        return CompletableFuture.completedFuture("verification email sent successfully");
    }

    /**
     * 인증 코드 검증
     * DynamoDB에서 해당 이메일의 인증 코드 조회 후 검증
     *
     * @param inputCode 사용자 입력 코드
     * @param email 사용자 이메일
     * @return boolean 인증 코드 검증 결과
     */
    public boolean verifyCode(String inputCode, String email) {

        Map<String, AttributeValue> expressionAttributeValues = ImmutableMap.of(
                ":pk", AttributeValue.builder().s(email).build(),
                ":currentTime", AttributeValue.builder().n(String.valueOf(Instant.now().getEpochSecond())).build()
        );

        try {
            QueryResponse response = dynamoDbRepository.getItem(QueryRequest.builder()
                    .tableName(codeTable)
                    .keyConditionExpression("#pk = :pk")
                    .filterExpression("#ea > :currentTime")
                    .expressionAttributeNames(expressionAttributeNames)
                    .expressionAttributeValues(expressionAttributeValues)
                    .build());
            Map<String, AttributeValue> item = response.items().get(0);

            return item.get("code").s().equals(inputCode);
        } catch (Exception e) {
            log.error("Dynamo DB search failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Thymeleaf 템플릿 생성
     * 전송할 인증 이메일 템플릿 설정
     *
     * @param code 인증 코드
     * @return Context Thymeleaf 템플릿 컨텍스트
     */
    private Context setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return context;
    }

    /**
     * 랜덤 인증 코드 생성
     *
     * @return String 랜덤 인증 코드
     */
    private String createRandomCode() {
        return UUID.randomUUID().toString().substring(4, 18);
    }
}
