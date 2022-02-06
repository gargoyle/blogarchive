package uk.co.paulcourt.blog;

import io.helidon.common.http.MediaType;
import java.util.Map;


public interface TemplateModel {
    public String getLayout();
    public Map getRoot();
    public MediaType getMediaType();
}
