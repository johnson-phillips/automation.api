package qa.automation.security;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuth10aServiceImpl;
import org.scribe.oauth.OAuthService;

public class SimpleOauth10 extends DefaultApi10a {

    public static Verb verb;


    @Override
    public OAuthService createService(OAuthConfig config)
    {
        return new OAuth10aServiceImpl(this, config);
    }

    @Override
    public Verb getRequestTokenVerb()
    {
        if(verb == null)
            verb = Verb.GET;
        return verb;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return "none";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "none";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return "none";
    }
}