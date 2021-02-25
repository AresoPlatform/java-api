

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ConfigBO {

    private Integer id;

    private String aschvalue;

    private String description;
}
