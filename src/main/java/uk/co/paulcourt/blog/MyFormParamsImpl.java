package uk.co.paulcourt.blog;

import io.helidon.common.http.FormParams;
import io.helidon.common.http.MediaType;
import io.helidon.common.http.ReadOnlyParameters;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class MyFormParamsImpl extends ReadOnlyParameters implements FormParams {

    /*
     * For form params represented in text/plain (uncommon), newlines appear between name=value
     * assignments. When urlencoded, ampersands separate the name=value assignments.
     */
    private static final Map<MediaType, Pattern> PATTERNS = Map.of(
            MediaType.APPLICATION_FORM_URLENCODED, preparePattern("&"),
            MediaType.TEXT_PLAIN, preparePattern("\n"));

    private static Pattern preparePattern(String assignmentSeparator) {
        return Pattern.compile(String.format("([^=%1$s]+)=([^%1$s]+)%1$s?", assignmentSeparator));
    }

    static MyFormParamsImpl create(String paramAssignments, MediaType mediaType) {
        final Map<String, List<String>> params = new HashMap<>();
        Matcher m = PATTERNS.get(mediaType).matcher(paramAssignments);
        while (m.find()) {
            final String key = m.group(1);
            final String value = java.net.URLDecoder.decode(m.group(2), StandardCharsets.UTF_8);
            List<String> values = params.compute(key, (k, v) -> {
                        if (v == null) {
                            v = new ArrayList<>();
                        }
                        v.add(value);
                        return v;
            });
        }
        return new MyFormParamsImpl(params);
    }

    public MyFormParamsImpl(Map<String, List<String>> data)
    {
        super(data);
    }

}
