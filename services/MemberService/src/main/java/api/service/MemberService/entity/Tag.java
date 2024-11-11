package api.service.MemberService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 회원별 태그 정보를 담은 엔티티
 *
 * @author jinhyeok
 */
@Getter
@Builder
@Entity
@Table(name = "tag")
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("태그 고유 번호")
    @Column(name="id")
    private Integer id;

    @Comment("태그 이름")
    @Column(name="name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @Comment("회원 고유 번호")
    private Member member;
}
