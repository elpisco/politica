package co.inc.board.domain.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TwitterTarget {

	private final TwitterId twitterId;
	private final String screenName;
	private final List<String> relatedWords;
	private final String profileImageUrl;

	@JsonCreator
	public TwitterTarget(@JsonProperty("twitterId") TwitterId twitterId, @JsonProperty("screenName") String screenName,
			@JsonProperty("relatedWords") List<String> relatedWords, @JsonProperty("profileImageUrl") String profileImageUrl) {
		this.twitterId = twitterId;
		this.screenName = screenName;
		this.relatedWords = relatedWords;
		this.profileImageUrl = profileImageUrl;
	}

	public TwitterId getTwitterId() {
		return twitterId;
	}

	public String getScreenName() {
		return screenName;
	}

	public List<String> getRelatedWords() {
		return relatedWords;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
}
