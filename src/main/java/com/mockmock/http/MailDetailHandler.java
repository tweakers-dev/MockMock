package com.mockmock.http;

import com.mockmock.htmlbuilder.FooterHtmlBuilder;
import com.mockmock.htmlbuilder.HeaderHtmlBuilder;
import com.mockmock.htmlbuilder.MailViewHtmlBuilder;
import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailDetailHandler extends BaseHandler
{
    private String pattern = "^/view/([0-9]+)/?$";

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse response) throws IOException, ServletException
    {
        if(!isMatch(target))
        {
            return;
        }

        long mailId = getMailId(target);
        if(mailId == 0)
        {
            return;
        }

        MockMail mockMail = MailQueue.getById(mailId);
        if(mockMail == null)
        {
            return;
        }

        setDefaultResponseOptions(response);

        HeaderHtmlBuilder headerHtmlBuilder = new HeaderHtmlBuilder();
        String header = headerHtmlBuilder.build();

        MailViewHtmlBuilder mailViewHtmlBuilder = new MailViewHtmlBuilder();
        mailViewHtmlBuilder.setMockMail(mockMail);
        String body = mailViewHtmlBuilder.build();

        FooterHtmlBuilder footerHtmlBuilder = new FooterHtmlBuilder();
        String footer = footerHtmlBuilder.build();

        response.getWriter().print(header + body + footer);

        request.setHandled(true);
    }

    /**
     * Checks if this handler should be used for the given target
     * @param target String
     * @return boolean
     */
    private boolean isMatch(String target)
    {
        return target.matches(pattern);
    }

    /**
     * Returns the mail id if it is part of the target
     * @param target String
     * @return long
     */
    private long getMailId(String target)
    {
        Pattern compiledPattern = Pattern.compile(pattern);

        Matcher matcher = compiledPattern.matcher(target);
        if(matcher.find())
        {
            String result = matcher.group(1);
            try
            {
                return Long.valueOf(result);
            }
            catch (NumberFormatException e)
            {
                return 0;
            }
        }

        return 0;
    }
}
