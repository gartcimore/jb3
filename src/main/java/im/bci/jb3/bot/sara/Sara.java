package im.bci.jb3.bot.sara;

import im.bci.jb3.bot.Bot;
import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.logic.Tribune;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class Sara implements Bot {

	public static final String NAME = "sara";

	@Autowired
	private Tribune tribune;

	@Autowired
	private SaraAction[] actions;

	@Override
	public void handle(final Post post, UriComponentsBuilder uriBuilder) {
		try {
			if (tribune.isBotCall(post, NAME)) {
				List<SaraAction> musts = new ArrayList<>();
				List<SaraAction> cans = new ArrayList<>();
				for (SaraAction action : actions) {
					switch (action.match(post)) {
					case MUST:
						musts.add(action);
					case CAN:
						cans.add(action);
						break;
					case NO:
						break;
					}

				}
				Collections.shuffle(musts);
				for (SaraAction action : musts) {
					if (action.act(post, uriBuilder)) {
						return;
					}
				}
				Collections.shuffle(cans);
				for (SaraAction action : cans) {
					if (action.act(post, uriBuilder)) {
						return;
					}
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(Sara.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
