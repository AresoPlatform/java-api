
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Config {
    private Integer id;

    private String  aschkey;

    private String aschvalue;

    private String description;
}
