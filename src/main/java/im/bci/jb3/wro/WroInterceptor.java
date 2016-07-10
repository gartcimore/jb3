package im.bci.jb3.wro;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class WroInterceptor extends HandlerInterceptorAdapter {
	@Value("${jb3.wro}")
	private boolean useWro;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (null != modelAndView) {
			modelAndView.addObject("wro", getWroForGroup(modelAndView.getViewName()));
		}
	}

	private class GroupLoader {
		WroMV wro = new WroMV();
		Document wroDoc;
		HashSet<String> doneGroups = new HashSet<String>();

		void loadGroup(String groupName) {
			if (!doneGroups.contains(groupName)) {
				for (Element group : wroDoc.select("group[name='" + groupName + "']")) {
					for (Element child : group.children()) {
						if ("css".equals(child.tagName())) {
							wro.addCss(removePrefix(child.text()));
						} else if ("js".equals(child.tagName())) {
							wro.addJs(removePrefix(child.text()));
						} else if ("group-ref".equals(child.tagName())) {
							loadGroup(child.text());
						}
					}
					doneGroups.add(groupName);
					break;
				}
			}

		}

		private String removePrefix(String text) {
			return StringUtils.replace(text, "classpath:META-INF/resources", "");
		}
	}

	public WroMV getWroForGroup(String groupName) throws IOException {
		if (useWro) {
			WroMV wro = new WroMV();
			wro.addCss("/wro/" + groupName + ".css");
			wro.addJs("/wro/" + groupName + ".js");
			return wro;
		} else {
			GroupLoader loader = new GroupLoader();
			InputStream in = getClass().getResourceAsStream("/wro/wro.xml");
			try {
				loader.wroDoc = Jsoup.parse(in, "UTF-8", "", Parser.xmlParser());
				loader.loadGroup(groupName);
			} finally {
				in.close();
			}
			return loader.wro;
		}
	}

}
