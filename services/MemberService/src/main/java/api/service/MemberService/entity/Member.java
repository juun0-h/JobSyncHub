package api.service.MemberService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

/**
 * 회원 정보를 담는 Entity 클래스
 *
 * @author jinhyeok
 */
@Data
@Builder
@Entity
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("회원 고유 번호")
    @Column(name="id")
    private Integer id;

    @Comment("회원 이메일")
    @Column(name="email", nullable = false)
    private String email;

    @Comment("회원 비밀번호")
    @Column(name="password", nullable = false)
    private String password;

    @Comment("회원 이름")
    @Column(name="name", nullable = false)
    private String name;

    @Comment("회원 태그")
    @Column(name="tags")
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags;

    @CreationTimestamp
    @Comment("회원 가입일")
    @Column(name="created")
    private Timestamp created;

    @CreationTimestamp
    @Comment("회원 정보 수정일")
    @Column(name="updated")
    private Timestamp updated;

    @Comment("구독 여부")
    @Column(name="subscribed")
    private Boolean subscribed;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private Role role;
}
