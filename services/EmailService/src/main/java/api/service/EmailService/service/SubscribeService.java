package api.service.EmailService.service;

import api.service.EmailService.repository.DynamoDbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeService {

    @Value("${aws.dynamodb.subscribe.table}")
    private String subscribeTable;
    private final DynamoDbRepository dynamoDbRepository;

    /**
     * 이메일 구독 저장
     *
     * @param email 구독할 이메일
     * @param tags  구독할 태그 리스트
     */
    public void subscribe(String email, List<String> tags) {
        for (String tag : tags) {
            try {
                Map<String, AttributeValue> item = new ConcurrentHashMap<>();
                item.put("tag", AttributeValue.builder().s(tag).build());
                item.put("email", AttributeValue.builder().s(email).build());
                item.put("subscribeAT", AttributeValue.builder().n(String.valueOf(Instant.now().getEpochSecond())).build());

                PutItemResponse response = dynamoDbRepository.putItem(PutItemRequest.builder()
                        .tableName(subscribeTable)
                        .item(item)
                        .build());

            } catch (ConditionalCheckFailedException e) {
                log.warn("이미 구독된 항목 - tag: {}, email: {}", tag, email);
            } catch (Exception e) {
                log.error("구독 저장 중 오류 발생 - tag: {}, email: {}, 오류: {}", tag, email, e.getMessage());
                throw e;
            }
            log.info("이메일 구독 저장 - tag: {}, email: {}", tag, email);
        }
    }

    /**
     * 이메일 구독 삭제
     *
     * @param email 삭제할 이메일
     */
    public void deleteSubscribe(String email) {
        List<Map<String, AttributeValue>> itemsToDelete = scanItemsByEmail(email);

        if (itemsToDelete.isEmpty()) {
            log.info("no subscriptions found for email: {}", email);
            return;
        }

        for (Map<String, AttributeValue> item : itemsToDelete) {
            String tag = item.get("tag").s();
            String emailValue = item.get("email").s();

            Map<String, AttributeValue> deleteKey = ImmutableMap.of(
                    "tag", AttributeValue.builder().s(tag).build(),
                    "email", AttributeValue.builder().s(emailValue).build()
            );

            DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                    .tableName(subscribeTable)
                    .key(deleteKey)
                    .build();

            try {
                DeleteItemResponse deleteItemResponse = dynamoDbRepository.deleteItem(deleteRequest);
                log.info("deleted tag: {}, email: {}", tag, emailValue);
            } catch (Exception e) {
                log.error("failed to delete tag: {}, email: {}, error: {}", tag, emailValue, e.getMessage());
            }
        }
    }

    /**
     * 특정 이메일에 해당하는 모든 항목을 스캔
     *
     * @param email     조회할 이메일 주소
     * @return 조회된 항목 목록
     */
    private List<Map<String, AttributeValue>> scanItemsByEmail(String email) {
        Map<String, String> expressionAttributeNames = ImmutableMap.of(
                "#email", "email"
        );
        Map<String, AttributeValue> expressionAttributeValues = ImmutableMap.of(
                ":email", AttributeValue.builder().s(email).build()
        );

        // 스캔 요청 생성
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(subscribeTable)
                .filterExpression("#email = :email")
                .expressionAttributeNames(expressionAttributeNames)
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        try {
            ScanResponse scanResponse = dynamoDbRepository.scan(scanRequest);
            return scanResponse.items();
        } catch (Exception e) {
            log.error("dynamoDB scan error: {}", e.getMessage());
            throw e;
        }
    }
}
