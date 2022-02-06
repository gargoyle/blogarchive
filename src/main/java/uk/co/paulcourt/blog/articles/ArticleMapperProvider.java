package uk.co.paulcourt.blog.articles;

import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.spi.DbMapperProvider;
import java.util.Optional;

public class ArticleMapperProvider
        implements DbMapperProvider
{
    private static final ArticleMapper MAPPER = new ArticleMapper();
    
    @Override
    public <T> Optional<DbMapper<T>> mapper(Class<T> type)
    {
        if (type.equals(Article.class)) {
            return Optional.of((DbMapper<T>) MAPPER);
        }
        
        return Optional.empty();
    }    
}
