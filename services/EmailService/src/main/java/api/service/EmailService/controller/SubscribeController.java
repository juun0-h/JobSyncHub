package api.service.EmailService.controller;

import api.service.EmailService.dto.subscribe.SubRequestDto;
import api.service.EmailService.dto.subscribe.SubResponseDto;
import api.service.EmailService.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    /**
     * 이메일 구독 신청
     *
     * @param subRequestDto 이메일 주소, 구독 태그, 구독 여부
     * @return ResponseEntity<SubResponseDto> 구독 결과 반환
     */
    @PostMapping("/subscribe")
    public ResponseEntity<SubResponseDto> applySubscribe(@RequestBody SubRequestDto subRequestDto) {
        log.info("이메일 구독 신청: {}", subRequestDto.getEmail());

        try {
            //이미 구독 중인 경우
            if(subRequestDto.getSubscribed()) {
                return new ResponseEntity<>(SubResponseDto.builder()
                        .statusCode(400)
                        .message("already subscribed")
                        .build(), HttpStatus.BAD_REQUEST);
            }

            //구독 신청
            subscribeService.subscribe(subRequestDto.getEmail(), subRequestDto.getTags());

            return new ResponseEntity<>(SubResponseDto.builder()
                    .statusCode(200)
                    .message("success subscribe email")
                    .subscribed(true)
                    .build(), HttpStatus.OK);

        } catch(Exception e) {
            return new ResponseEntity<>(SubResponseDto.builder()
                    .statusCode(400)
                    .message("failed subscribe email")
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }
}
