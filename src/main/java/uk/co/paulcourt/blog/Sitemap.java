package uk.co.paulcourt.blog;

import io.helidon.common.http.MediaType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


public class Sitemap implements TemplateModel
{
    private String layout = "layouts/sitemapLayout.xml";
    
    private Map urls = new HashMap();
    
    
    public Sitemap addUrl(String url, LocalDateTime lastUpdated)
    {
        this.urls.put(url, lastUpdated);
        return this;
    }
    
    public String getLayout()
    {
        return this.layout;
    }
    
    public Map getRoot()
    {
        Map root = new HashMap();
        root.put("urls", this.urls);
        return root;
    }
    
    
    public MediaType getMediaType()
    {
        return MediaType.TEXT_XML;
    }
}
