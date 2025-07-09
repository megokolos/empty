package ru.kolosov.empty.dto.response;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.kolosov.empty.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProducts {

    @Valid
    private List<ProductDTO> productDTOList = new ArrayList<>();
}