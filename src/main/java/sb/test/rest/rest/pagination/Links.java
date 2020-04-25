package sb.test.rest.rest.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Links {
    private final String current;
    private final String first;
    private final String last;
    private String next = null;
    private String prev = null;

    public Links(Page<?> page) {
        var components = ServletUriComponentsBuilder.fromCurrentRequest().build();

        current = makeUriString(components, page.getNumber(), page.getSize());
        first = makeUriString(components, 0, page.getSize());
        last = makeUriString(components, page.getTotalPages() - 1, page.getSize());

        if (!page.isLast()) {
            next = makeUriString(components, page.getNumber() + 1, page.getSize());
        }

        if (!page.isFirst()) {
            prev = makeUriString(components, page.getNumber() - 1, page.getSize());
        }
    }

    private String makeUriString(UriComponents components, int page, int size) {
        var host = components.getHost();
        var port = components.getPort();
        var path = components.getPath();
        MultiValueMap<String, String> params = components.getQueryParams();

        var newParams = new LinkedMultiValueMap<>(params);
        newParams.remove("page");
        newParams.remove("size");
        newParams.add("page", String.valueOf(page));
        newParams.add("size", String.valueOf(size));

        return UriComponentsBuilder.newInstance()
            .scheme("https")
            .host(host)
            .port(port)
            .path(path)
            .queryParams(newParams)
            .build()
            .toUriString();
    }
}
