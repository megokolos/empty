package ru.kolosov.empty.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kolosov.empty.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestProducts {

    @Valid
    private List<ProductDTO> productDTOList = new ArrayList<>();
}
