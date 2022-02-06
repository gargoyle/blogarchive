package uk.co.paulcourt.blog.articles;

import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import java.util.List;
import java.util.Map;

public class ArticleRepository
{

    private DbClient dbClient;

    public ArticleRepository(DbClient dbClient)
    {
        this.dbClient = dbClient;
    }

    public Single<List<Article>> all()
    {
        return this.dbClient
                .execute(exe -> exe.namedQuery("select-all-articles"))
                .map(it -> it.as(Article.class)).collectList();
    }

    public Single<List<Article>> publishedOnly()
    {
        return this.dbClient
                .execute(exe -> exe.namedQuery("select-published-articles"))
                .map(it -> it.as(Article.class)).collectList();
    }
    
    public Single<Article> oneBySlug(String slug)
    {
        return this.dbClient
                .execute(exe -> exe.namedGet("get-article-by-slug", slug))
                .map(optMapper -> optMapper
                        .map(row -> row.as(Article.class))
                        .orElseThrow());
    }
    
    public void save(Article article)
    {
        this.dbClient
                .execute(exe -> exe
                        .createNamedUpdate("save-existing-article")
                        .namedParam(article)
                        .execute()).await();
                        
    }
    
    public void saveNew(Article article)
    {
        this.dbClient
                .execute(exe -> exe
                        .createNamedUpdate("save-new-article")
                        .namedParam(article)
                        .execute()).await();
                        
    }
}
