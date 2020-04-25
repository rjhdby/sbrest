package sb.test.rest.rest.pagination;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PagedListDTO<T> {
    private final List<T> data;
    private final PaginationMeta meta;
    private final Links links;

    public PagedListDTO(Page<T> page) {
        data = page.toList();
        meta = new PaginationMeta(page);
        links = new Links(page);
    }
}
