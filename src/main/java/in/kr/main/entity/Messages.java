package in.kr.main.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "message_tbl")
public class Messages {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(columnDefinition = "TEXT")
	private String content;
	private String iv;
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime sentAt;
	private String type;
	private String imageUrl;
	@OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MessageKey> messageKeys = new ArrayList<MessageKey>();
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = true)
	private UserEntity user;
	
	@ManyToOne
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;
}
