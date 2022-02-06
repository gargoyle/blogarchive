package uk.co.paulcourt.blog;

import freemarker.template.Configuration;
import io.helidon.dbclient.DbClient;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import java.time.LocalDateTime;
import uk.co.paulcourt.blog.articles.Article;
import uk.co.paulcourt.blog.articles.ArticleRepository;


public class SitemapService extends AbstractService {

    private final ArticleRepository articleRepo;

    public SitemapService(Configuration fmConfig, DbClient dbClient)
    {
        super(fmConfig);
        this.articleRepo = new ArticleRepository(dbClient);
    }

    @Override
    public void update(Routing.Rules rules)
    {
        rules.get("/sitemap.xml", this::sitemapHandler);
    }

    private void sitemapHandler(ServerRequest request, ServerResponse response)
    {
        Sitemap sitemap = new Sitemap();
        
        this.articleRepo.publishedOnly().thenAccept(articles -> {
            LocalDateTime latestUpdate = LocalDateTime.MIN;
            for(Article article : articles) {
                sitemap.addUrl("https://www.paulcourt.co.uk/article/" + article.getSlug(), article.getLastUpdated());
                if (article.getLastUpdated().compareTo(latestUpdate) > 0) {
                    latestUpdate = article.getLastUpdated();
                }
            }
            sitemap.addUrl("https://www.paulcourt.co.uk/", latestUpdate);
            
            response.headers().add("Content-type", "application/xml");
            this.renderAndSendResponse(response, sitemap);
        });
    }
}
