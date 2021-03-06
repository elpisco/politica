package co.inc.twitterStreamCrawler;

import co.inc.twitterStreamCrawler.domain.entities.TwitterId;
import co.inc.twitterStreamCrawler.domain.entities.TwitterTarget;
import co.inc.twitterStreamCrawler.domain.workers.TwitterConsumerWorker;
import co.inc.twitterStreamCrawler.persistence.daos.TargetDAO;
import co.inc.twitterStreamCrawler.persistence.daos.TweetDAO;
import co.inc.twitterStreamCrawler.utils.PolarityClassifier;
import co.inc.twitterStreamCrawler.utils.constants.GlobalConstants;
import co.inc.twitterStreamCrawler.utils.stopwords.classification.StopwordsSpanish;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.sun.jersey.api.client.WebResource;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import weka.classifiers.meta.FilteredClassifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterStreamCrawler {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterStreamCrawler.class);

	public final static String CONSUMER_KEY = "wxzRapS5chgLj2mk4I4A";
	public final static String CONSUMER_SECRET = "FVc5dgZ05j288pf0mKUQuvqeJsP550nnVvxUqINdI";
	public final static String ACCESS_TOKEN = "263623229-NEcwbcSBdDnYxtnoFFdbi4VOPCtdTjBpwnSc5a8b";
	public final static String ACCESS_TOKEN_SECRET = "eWXxlbezHqQN4NFyPZe5TGxwinhOVEeERDhU9irT5cXTc";

	public final static String MONGO_IP = "";
	public final static String MONGO_DB = "boarddb";

	private final BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
	private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

	private final TargetDAO targetsDAO;
	private final TweetDAO tweetDAO;
	private final PolarityClassifier polarityClassifier;
	private final FilteredClassifier classifier;

	public TwitterStreamCrawler(String nrcFile, String translateFile, String twitterModel) throws ClassNotFoundException, IOException {
		MongoClient mongoClient = new MongoClient(MONGO_IP);
		MongoDatabase mongoDatabase = mongoClient.getDatabase(MONGO_DB);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		targetsDAO = new TargetDAO(mongoDatabase, objectMapper);
		tweetDAO = new TweetDAO(mongoDatabase);
		polarityClassifier = new PolarityClassifier(nrcFile, translateFile);
		classifier = loadClassifier(twitterModel);
		init();
	}

	public void init() {
		initTargets();
	}

	public void initTargets() {
		List<TwitterId> ids = targetsDAO.getAllIds();
		Twitter twitter = getTwitter();
		for (TwitterId id : ids) {
			String stringId = id.getId();
			if (targetsDAO.getTarget(stringId) == null) {
				try {
					User user = twitter.showUser(stringId);
					TwitterTarget target = new TwitterTarget(id, user.getScreenName(), user.getProfileImageURL());
					targetsDAO.insertTwitterTarget(target);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Twitter getTwitter() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(ACCESS_TOKEN).setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		return twitter;
	}

	private Client getTwitterClient() {
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
		hosebirdEndpoint.trackTerms(getTerms());
		Authentication hosebirdAuth = new OAuth1(TwitterStreamCrawler.CONSUMER_KEY,
				TwitterStreamCrawler.CONSUMER_SECRET, TwitterStreamCrawler.ACCESS_TOKEN,
				TwitterStreamCrawler.ACCESS_TOKEN_SECRET);

		ClientBuilder builder = new ClientBuilder().name("Hosebird-Client-01").hosts(hosebirdHosts)
				.authentication(hosebirdAuth).endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue)).eventMessageQueue(eventQueue);
		Client hosebirdClient = builder.build();
		return hosebirdClient;
	}

	private List<String> getTerms() {
		List<TwitterId> ids = targetsDAO.getAllIds();
		List<String> relatedWords = new ArrayList<>();
		for (TwitterId id : ids) {
			relatedWords.addAll(id.getRelatedWords());
		}
		return relatedWords;
	}
	
	private FilteredClassifier loadClassifier(String filename) throws ClassNotFoundException, IOException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
		Object tmp = in.readObject();
		in.close();
		return (FilteredClassifier) tmp;
	}

	public void crawl() {
		Client hosebirdClient = getTwitterClient();
		hosebirdClient.connect();
		//ExecutorService threadPool = Executors.newCachedThreadPool();
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		StopwordsSpanish stopwordsSpanish = new StopwordsSpanish();

		com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
		WebResource webResource = client.resource(GlobalConstants.BOARD_URL);

		while (!hosebirdClient.isDone()) {
			try {
				String stringTweet = msgQueue.take();
				TwitterConsumerWorker worker = new TwitterConsumerWorker(targetsDAO, tweetDAO, stringTweet,
						polarityClassifier, stopwordsSpanish, classifier, webResource);
				threadPool.submit(worker);
			} catch (InterruptedException e) {
				LOGGER.error("run", e);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
		TwitterStreamCrawler crawler = new TwitterStreamCrawler(args[0], args[1], args[2]);
		crawler.crawl();
	}
}
