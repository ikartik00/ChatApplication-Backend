package in.kr.main.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_tbl")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String userId;
	
	private String name;
	private String email;
	private String password;
	private String imgUrl;
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@OneToMany(mappedBy = "user")
	private List<Messages> messages = new ArrayList<Messages>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "createdBy")
	private List<Room> rooms = new ArrayList<Room>();
}
