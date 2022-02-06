package uk.co.paulcourt.blog;

import freemarker.template.Configuration;
import io.helidon.common.http.FormParams;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.security.integration.webserver.WebSecurity;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import java.util.List;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import uk.co.paulcourt.blog.articles.Article;
import uk.co.paulcourt.blog.articles.ArticleRepository;

public class BlogService extends AbstractService
{
    private final ArticleRepository articleRepo;

    BlogService(
            Config mainConfig,
            Configuration fmConfig,
            DbClient dbClient)
    {
        super(fmConfig);
        this.articleRepo = new ArticleRepository(dbClient);
    }
    
    @Override
    public void update(
            Routing.Rules rules)
    {
        rules.get("/", this::blogIndexHandler);
        // rules.get("/article/{slug}", WebSecurity.rolesAllowed("admin"), this::blogArticleHandler);
        rules.get("/article/{slug}", this::viewArticleHandler);
        rules.get("/article/{slug}/edit", WebSecurity.rolesAllowed("admin"),
                this::editArticleHandler);
        rules.post("/article/{slug}/edit", WebSecurity.rolesAllowed("admin"),
                this::saveArticleHandler);
        
        rules.get("/createarticle", WebSecurity.rolesAllowed("admin"),
                this::createArticleHandler);
        rules.post("/createarticle", WebSecurity.rolesAllowed("admin"),
                this::saveNewArticleHandler);
        
        rules.get("/fulllist", WebSecurity.rolesAllowed("admin"),
                this::fullListHandler);
        
        
    }

    private void blogIndexHandler(
            ServerRequest request,
            ServerResponse response)
    {
        Page page = new Page("/homepage.html", "Paul Court", request);
        page.setHero(
                "/img/hero-me-bg_2.jpg",
                "Paul Court",
                "");

        this.articleRepo.publishedOnly()
                .thenAccept(articleList -> {
                    page.getRoot().put("articleList", articleList);
                })
                .thenRunAsync(() -> renderAndSendResponse(response, page))
                .exceptionally(t -> {
                    t.printStackTrace();
                    return null;
                });
    }

    private void fullListHandler(
            ServerRequest request,
            ServerResponse response)
    {
        Page page = new Page("/fulllist.html", "All Articles", request);

        this.articleRepo.all()
                .thenAccept(articleList -> {
                    page.getRoot().put("articleList", articleList);
                })
                .thenRunAsync(() -> renderAndSendResponse(response, page))
                .exceptionally(t -> {
                    response.status(500);
                    renderAndSendResponse(response, Page.error(request));
                    return null;
                });
    }
    
    private void viewArticleHandler(
            ServerRequest request,
            ServerResponse response)
    {
        String slug = request.path().param("slug");
        Page page = new Page("/article.html", "Paul Court", request);

        this.articleRepo.oneBySlug(slug)
                .thenApply(article -> {
                    page.setTitle(article.getTitle());
                    page.setHero(
                            article.getHeroImageUrl() != null ? article.getHeroImageUrl() : "/img/hero-me-bg.png",
                            article.getTitle(),
                            article.getSummary());

                    page.setDescription(article.getOpenGraphSummary());
                    page.setOpenGraphImageUrl(article.getOpenGraphImageUrl());
                    page.setKeywords(String.join(", ", article.getTags()));
                    
                    page.getRoot().put("article", article);
                    page.getRoot().put("parsedBody", this.markdownToHtml(
                            article.getBody()));
                    return page;
                })
                .thenRunAsync(() -> renderAndSendResponse(response, page))
                .exceptionally(t -> {
                    response.status(404);
                    renderAndSendResponse(response, Page.error(request));
                    return null;
                });
    }

    private void editArticleHandler(
            ServerRequest request,
            ServerResponse response)
    {
        String slug = request.path().param("slug");
        Page page = new Page("/editArticle.html", "Paul Court", request);
        page.enableMDEditor();

        this.articleRepo.oneBySlug(slug)
                .thenApply(article -> {
                    page.setTitle(article.getTitle());

                    page.getRoot().put("article", article);
                    page.getRoot().put("parsedBody", this.markdownToHtml(
                            article.getBody()));
                    return page;
                })
                .thenRunAsync(() -> renderAndSendResponse(response, page))
                .exceptionally(t -> {
                    response.status(500);
                    renderAndSendResponse(response, Page.error(request));
                    return null;
                });
    }
    
    private void saveArticleHandler(
            ServerRequest request,
            ServerResponse response)
    {
        String slug = request.path().param("slug");
        Page page = new Page("/editArticle.html", "Paul Court", request);
        page.enableMDEditor();

        this.articleRepo.oneBySlug(slug)
                .thenApply(article -> {
                    //FormParams params = request.content().as(MyFormParamsImpl.class).await();
                    FormParams params = request.content().as(MyFormParamsImpl.class).await();
                    updateArticleFromParams(params, article);
                    
                    return article;
                })
                .thenAccept(article -> {
                    this.articleRepo.save(article);
                    response.status(302).headers().add("Location", "/article/" + article.getSlug());
                    response.send();
                })
                .exceptionally(t -> {
                    response.status(500);
                    renderAndSendResponse(response, Page.error(request));
                    return null;
                });
    }

    private void createArticleHandler(
            ServerRequest request,
            ServerResponse response)
    {
        Page page = new Page("/createArticle.html", "Paul Court", request);
        page.enableMDEditor();
        
        renderAndSendResponse(response, page);
    }
    
    private void saveNewArticleHandler(
            ServerRequest request,
            ServerResponse response)
    {
        request.content().as(MyFormParamsImpl.class)
                .thenAccept(params -> {
                    Article newArticle = new Article();
                    updateArticleFromParams(params, newArticle);
                    this.articleRepo.saveNew(newArticle);

                    response.status(302).headers().add("Location", "/article/" + newArticle.getSlug());
                    response.send();
                })
                .exceptionally(t -> {
                    response.status(500);
                    renderAndSendResponse(response, Page.error(request));
                    return null;
                });
    }
    
    private void updateArticleFromParams(FormParams params, Article article)
    {
        params.first("title").ifPresent(title -> {
            article.setTitle(title);
        });

        params.first("heroImageUrl").ifPresent(heroUrl -> {
            article.setHeroImageUrl(heroUrl);
        });

        params.first("openGraphImageUrl").ifPresent(url -> {
            article.setOpenGraphImageUrl(url);
        });

        params.first("openGraphSummary").ifPresent(ogSummary -> {
            article.setOpenGraphSummary(ogSummary);
        });

        params.first("body").ifPresent(body -> {
            article.setBody(body);
        });

        params.first("slug").ifPresent(newSlug -> {
            article.setSlug(newSlug);
        });

        params.first("summary").ifPresent(summary -> {
            article.setSummary(summary);
        });

        params.first("tags").ifPresent(tags -> {
            article.setTags(List.of(tags.split(",")));
        });

        params.first("published").ifPresentOrElse(
                pub -> {
                    article.publish();
                },
                () -> {
                    article.unpublish();
                }
        );
    }
    
    
    private String markdownToHtml(String markdown)
    {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    
}
