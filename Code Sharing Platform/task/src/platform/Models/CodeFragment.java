package platform.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeFragment {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private String id;

    private String code;
    private LocalDateTime date;

    @JsonIgnore
    private LocalDateTime expirationDate;

    private long time;
    private long views;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public Long getTime() {
        return this.time;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


}
