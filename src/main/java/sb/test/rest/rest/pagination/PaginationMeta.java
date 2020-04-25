package sb.test.rest.rest.pagination;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PaginationMeta {
    private final int pageSize;
    private final int page;
    private final int pages;
    private final long totalElements;
    private final int elements;

    public PaginationMeta(Page<?> page) {
        this.page = page.getNumber();
        pages = page.getTotalPages();
        totalElements = page.getTotalElements();
        pageSize = page.getSize();
        elements = page.getNumberOfElements();
    }
}
