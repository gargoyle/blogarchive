package uk.co.paulcourt.blog;

import io.helidon.common.http.MediaType;
import io.helidon.webserver.ServerRequest;
import java.util.HashMap;
import java.util.Map;

public class Page implements TemplateModel
{
    private String layout = "layouts/defaultLayout.html";
    private Map root = new HashMap();

    private Map global = new HashMap();
    private Map meta = new HashMap();
    private Map hero = new HashMap();
    
    public Page(String bodyTemplate,
            String title, ServerRequest request)
    {
        root.put("bodyTemplate",
                bodyTemplate);

        setDefaultGlobalData();
        setDefaultMetaData(title);

        root.put("global", global);
        root.put("meta", meta);
        root.put("hero", hero);
        
        String remoteIp = request.headers().value("X-Real-IP").orElse("Unknown");
        meta.put("remoteIp", remoteIp);
        if ("81.187.137.228".equals(remoteIp)) {
            meta.put("disableStats", true);
        } else {
            meta.put("disableStats", false);
        }
    }

    public static Page error(ServerRequest request)
    {
        return new Page("/error.html", "Oops", request);
    }
    
    private void setDefaultGlobalData()
    {
        global.put("appName", "Paul Court");
        global.put("authorName", "Paul Court");
        global.put("legalName", "Paul Court");
        global.put("twitterHandle", "paulcourt101");
        global.put("enableMDEditor", false);
    }

    private void setDefaultMetaData(String title)
    {
        meta.put("title", title);
        meta.put("description", "Paul Court");
        meta.put("keywords", "java, helidon, freemarker");
        meta.put("ogImage", "https://files.rgoyle.com/images/gargoyle_avatar.png");
    }
    
    private void setDefaultHero()
    {
        hero.put("imgUrl", "/img/hero-bg.jpg");
        hero.put("title", "Welcome");
        hero.put("tagline", "There is no spoon");
    }

    public void setBodyTemplate(String templateFilename)
    {
        root.put("bodyTemplate", templateFilename);
    }
    
    public void setErrorMessage(String msg)
    {
        root.put("error", msg);
    }
    
    public String getLayout()
    {
        return this.layout;
    }

    public Map getRoot()
    {
        return root;
    }

    public MediaType getMediaType()
    {
        return MediaType.TEXT_HTML;
    }
    
    public void setHero(
            String imgUrl,
            String heroTitle,
            String heroTagline)
    {
        hero.put("imgUrl", imgUrl);
        hero.put("title", heroTitle);
        hero.put("tagline", heroTagline);
    }
    
    public void setTitle(String title)
    {
        if (title != null) {
            meta.put("title", title);
        }
    }
    
    public void setDescription(String description)
    {
        if (description != null) {
            meta.put("description", description);
        }
    }
    
    public void setKeywords(String keywords)
    {
        if (keywords != null) {
            meta.put("keywords", keywords);
        }
    }
    
    public void setOpenGraphImageUrl(String url)
    {
        if ((url != null) && (url.length() > 0)) {
            meta.put("ogImage", url);
        }
    }
    
    public void enableMDEditor()
    {
        global.put("enableMDEditor", true);
    }
}
