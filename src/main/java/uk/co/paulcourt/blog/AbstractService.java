package uk.co.paulcourt.blog;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


abstract public class AbstractService implements Service {
    
    private static final Logger LOGGER = Logger.getLogger(
            AbstractService.class.getName());
    
    protected final Configuration fmConfig;

    public AbstractService(Configuration fmConfig)
    {
        this.fmConfig = fmConfig;
    }
    
    
    
    protected void renderAndSendResponse(
            ServerResponse response,
            TemplateModel page)
    {

        try {
            Template tpl = fmConfig.getTemplate(page.getLayout());
            StringWriter out = new StringWriter();
            tpl.process(page.getRoot(), out);
            out.close();

            response.headers().contentType(page.getMediaType());
            response.send(out.toString());

        } catch (MalformedTemplateNameException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            response.status(500).send("Unable to find template!");
        } catch (ParseException | TemplateException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            response.status(500).send("Failed to parse template!");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            response.status(500).send("IO Failure while parsing template!");
        }
    }
}
