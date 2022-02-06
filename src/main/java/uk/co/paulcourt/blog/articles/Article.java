package uk.co.paulcourt.blog.articles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class Article {
    private final UUID id;
    private final LocalDateTime created;
    private LocalDateTime lastUpdated;
    private LocalDateTime published;
    private String openGraphImageUrl;
    private String openGraphSummary;
    private String title;
    private String heroImageUrl;
    private String slug;
    private String summary;
    private String body;
    private List<String> tags;
    
    public Article(UUID id,
            LocalDateTime created,
            LocalDateTime lastUpdated,
            LocalDateTime published,
            String openGraphImageUrl,
            String openGraphSummary,
            String title,
            String heroImageUrl,
            String slug,
            String summary,
            String body,
            List<String> tags)
    {
        this.id = id;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.published = published;
        this.openGraphImageUrl = openGraphImageUrl;
        this.openGraphSummary = openGraphSummary;
        this.title = title;
        this.heroImageUrl = heroImageUrl;
        this.slug = slug;
        this.summary = summary;
        this.body = body;
        this.tags = tags;
    }

    public Article()
    {
        this.id = UUID.randomUUID();
        this.created = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.openGraphImageUrl = "";
        this.openGraphSummary = "";
        this.title = "Untitled";
        this.heroImageUrl = "";
        this.slug = UUID.randomUUID().toString();
        this.summary = "";
        this.body = "";
        this.tags = List.of();
    }
    
    public UUID getId()
    {
        return id;
    }

    public LocalDateTime getCreated()
    {
        return created;
    }

    public LocalDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public boolean isPublished()
    {
        return (published != null);
    }

    public String getOpenGraphSummary()
    {
        return openGraphSummary;
    }

    public String getHeroImageUrl()
    {
        return heroImageUrl;
    }
    
    public LocalDateTime getPublishedDate()
    {
        return published;
    }
    
    public long daysSincePublished()
    {
        if (!this.isPublished()) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        var diff = published.until(now, ChronoUnit.DAYS);
        return diff;
    }

    public long daysToWrite()
    {
        if (!this.isPublished()) {
            return 0;
        }
        
        var diff = created.until(published, ChronoUnit.DAYS);
        return diff;
    }

    
    
    public String getOpenGraphImageUrl()
    {
        return openGraphImageUrl;
    }

    public String getTitle()
    {
        return title;
    }

    public String getSlug()
    {
        return slug;
    }

    public String getSummary()
    {
        return summary;
    }

    public String getBody()
    {
        return body;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setOpenGraphSummary(String openGraphSummary)
    {
        this.lastUpdated = LocalDateTime.now();
        this.openGraphSummary = openGraphSummary;
    }

    public void setHeroImageUrl(String heroImageUrl)
    {
        this.lastUpdated = LocalDateTime.now();
        this.heroImageUrl = heroImageUrl;
    }


    public void setOpenGraphImageUrl(String openGraphImageUrl)
    {
        this.lastUpdated = LocalDateTime.now();
        this.openGraphImageUrl = openGraphImageUrl;
    }

    public void setTitle(String title)
    {
        this.lastUpdated = LocalDateTime.now();
        this.title = title;
    }

    public void setSlug(String slug)
    {
        this.lastUpdated = LocalDateTime.now();
        this.slug = slug;
    }

    public void setSummary(String summary)
    {
        this.lastUpdated = LocalDateTime.now();
        this.summary = summary;
    }

    public void setBody(String body)
    {
        this.lastUpdated = LocalDateTime.now();
        this.body = body;
    }

    public void setTags(List<String> tags)
    {
        this.lastUpdated = LocalDateTime.now();
        this.tags = tags.stream().map(String::trim).collect(Collectors.toList());
    }
    
    public void publish()
    {
        if (this.published == null) {
            this.lastUpdated = LocalDateTime.now();
            this.published = LocalDateTime.now();
        }
    }
    
    public void unpublish()
    {
        if (this.published != null) {
            this.lastUpdated = LocalDateTime.now();
            this.published = null;
        }
    }
}
