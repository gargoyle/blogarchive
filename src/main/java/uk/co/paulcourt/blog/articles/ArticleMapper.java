package uk.co.paulcourt.blog.articles;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ArticleMapper
        implements DbMapper<Article>
{

    @Override
    public Article read(DbRow dbrow)
    {
        String tags = dbrow.column("tags").as(Optional.of(String.class).orElse(null));
        List<String> tagList = new ArrayList<String>();
        if (tags != null) {
            tagList.addAll(List.of(tags.split(",")));
        }
        
        Article article = new Article(
                UUID.fromString(dbrow.column("id").as(String.class)),
                dbrow.column("createdAt").as(LocalDateTime.class),
                dbrow.column("lastUpdatedAt").as(LocalDateTime.class),
                dbrow.column("publishedAt").as(Optional.of(LocalDateTime.class).orElse(null)),
                dbrow.column("openGraphImageUrl").as(Optional.of(String.class).orElse(null)),
                dbrow.column("openGraphSummary").as(Optional.of(String.class).orElse(null)),
                dbrow.column("title").as(String.class),
                dbrow.column("heroImageUrl").as(Optional.of(String.class).orElse(null)),
                dbrow.column("slug").as(String.class),
                dbrow.column("summary").as(String.class),
                dbrow.column("body").as(String.class),
                tagList
        );
        return article;
    }

    @Override
    public Map<String, ?> toNamedParameters(Article article)
    {
        Map<String, Object> map = new HashMap<>(); 
        
        map.put("id", article.getId().toString());
        map.put("createdAt", article.getCreated());
        map.put("publishedAt", article.getPublishedDate());
        map.put("lastUpdatedAt", article.getLastUpdated());
        map.put("openGraphImageUrl", article.getOpenGraphImageUrl());
        map.put("openGraphSummary", article.getOpenGraphSummary());
        map.put("title", article.getTitle());
        map.put("heroImageUrl", article.getHeroImageUrl());
        map.put("slug", article.getSlug());
        map.put("summary", article.getSummary());
        map.put("body", article.getBody());
        map.put("tags", String.join(",", article.getTags()));
        
        return map;
    }

    @Override
    public List<?> toIndexedParameters(Article article)
    {
        return List.of(
                article.getId().toString(),
                article.getCreated(),
                article.getLastUpdated(),
                article.getPublishedDate(),
                article.getOpenGraphImageUrl(),
                article.getOpenGraphSummary(),
                article.getTitle(),
                article.getHeroImageUrl(),
                article.getSlug(),
                article.getSummary(),
                article.getBody(),
                String.join(",", article.getTags())
        );
    }

}
