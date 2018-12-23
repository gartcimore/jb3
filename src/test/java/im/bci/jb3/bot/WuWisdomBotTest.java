package im.bci.jb3.bot;


import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class WuWisdomBotTest {


    @Test
    void shouldExtractQuoteFromString() {
        String expected = "Don't compare. Be unique. Don't fight your misfortune. Transform it.";
        String content = "<article id=\"post-34\" class=\"post-34 page type-page status-publish hentry\">\n" +
                "\t<header class=\"entry-header\">\n" +
                "\t\t<h1 class=\"entry-title\">Wu Wisdom</h1>\t</header><!-- .entry-header -->\n" +
                "\n" +
                "\t<div class=\"entry-content\">\n" +
                "\t\t<p>Life is always better when you are in tune with yourself. Here is a Wutang wisdom quote that may help you during your day.</p>\n" +
                "<blockquote class=\"wu-wisdom\"><h1>Don't compare. Be unique. Don't fight your misfortune. Transform it.</h1></blockquote>\n" +
                "\t</div><!-- .entry-content -->\n" +
                "\n" +
                "\t</article><!-- #post-## -->\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</section>";

        String quote = WuWisdomBot.extractWisdom(content);
        assertThat(quote, is(expected));
    }
}