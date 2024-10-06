package api.service.EmailService.service;

import api.service.EmailService.repository.DynamoDbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
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
}
