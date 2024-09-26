package api.service.EmailService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Repository
@RequiredArgsConstructor
public class DynamoDbRepository {

    private final DynamoDbClient dynamoDbClient;

    public PutItemResponse putItem(PutItemRequest putItemRequest){
        return dynamoDbClient.putItem(putItemRequest);
    }

    public QueryResponse getItem(QueryRequest queryRequest){
        return dynamoDbClient.query(queryRequest);
    }
}
