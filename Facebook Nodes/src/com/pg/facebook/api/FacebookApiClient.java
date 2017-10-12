package com.pg.facebook.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.NodeLogger;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.CustomAudience.EnumSubtype;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.types.Account;
import com.restfb.types.Comment;
import com.restfb.types.Insight;
import com.restfb.types.Post;
import com.restfb.types.ads.AdAccount;
import com.restfb.types.ads.CustomAudience;

public class FacebookApiClient {

	private FacebookClient client;
	private APIContext apiContext;
	
	private String impersonationAccountId;
	private String impersonationAccessToken;
	
	private Boolean tokenConfigured = false;
	
	private static int MAX_ATTEMPTS = 3;
	private static NodeLogger LOGGER = NodeLogger.getLogger(FacebookApiClient.class);
	
	public FacebookApiClient() {
		client = new DefaultFacebookClient(Version.VERSION_2_9);
	}
	
	public FacebookApiClient(String accessToken, String appSecret) {
		client = new DefaultFacebookClient(accessToken, appSecret, Version.VERSION_2_9);
		apiContext = new APIContext(accessToken, appSecret).enableDebug(false);
		
		tokenConfigured = true;
	}
	
	public boolean isConfigured() {
		return tokenConfigured;
	}
	
	public String getImpersonationAccountId() {
		return impersonationAccountId;
	}

	public void setImpersonationAccountId(String accountId) {
		this.impersonationAccountId = accountId;
	}

	public String getImpersonationAccessToken() {
		return impersonationAccessToken;
	}

	public void setImpersonationAccessToken(String impersonationAccessToken) {
		this.impersonationAccessToken = impersonationAccessToken;
	}

	public List<Account> getAccounts() {
		Connection<Account> accounts = getConnection( client, "me/accounts", Account.class );
		
		List<Account> accountList = new ArrayList<Account>();
		
		// Get all results
		for ( List<Account> accountConnection : accounts ) {
			for (Account account : accountConnection ) 
				accountList.add(account);
		}
		
		return accountList;
	}
	
	public Connection<Post> getPosts() {
		
		DefaultFacebookClient impersonationClient = new DefaultFacebookClient(getImpersonationAccessToken(),Version.VERSION_2_9);
		
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(Parameter.with("fields", "id,created_time,message,from,link,type,application,shares,likes.limit(1).summary(true),comments.limit(1).summary(true)"));
		Parameter[] parameters = params.toArray(new Parameter[0]);
		
		
		Connection<Post> posts = getConnection( impersonationClient, "me/posts", Post.class, parameters );
		
		return posts;
	}
	
	public Connection<Comment> getComments() {
		
		DefaultFacebookClient impersonationClient = new DefaultFacebookClient(getImpersonationAccessToken(),Version.VERSION_2_9);
		
		Connection<Comment> comments = getConnection( impersonationClient, getImpersonationAccountId() + "/comments", Comment.class );
		
		return comments;
	}
	
	public Connection<Insight> getInsights(String period, String[] metrics, String startDate, String endDate ) {
		
		DefaultFacebookClient impersonationClient = new DefaultFacebookClient(getImpersonationAccessToken(),Version.VERSION_2_9);
		
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(Parameter.with("metric", StringUtils.join(metrics,",")));
		if ( period != null ) params.add( Parameter.with("period", period));
		if ( startDate != null && !startDate.isEmpty()) params.add(Parameter.with("since", startDate));
		if ( endDate != null && !endDate.isEmpty()) params.add(Parameter.with("until", endDate));
		
		Parameter[] parameters = params.toArray(new Parameter[0]);
		
		return getConnection(
									impersonationClient,
									getImpersonationAccountId() +"/insights", 
									Insight.class, 
									parameters );
		
		
	}
	
	public Connection<AdAccount> getAdAccounts ()  {
		
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(Parameter.with("fields", "account_status,amount_spent,account_id"));
		Parameter[] parameters = params.toArray(new Parameter[0]);
		
		Connection<AdAccount> accounts = getConnection(client, "me/adaccounts", AdAccount.class, parameters);
		
		return accounts;
		
	}
	
	public Connection<CustomAudience> getCustomAudiences (String accountId) {
		
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(Parameter.with("fields", "name,delivery_status,description,approximate_count"));
		Parameter[] parameters = params.toArray(new Parameter[0]);
	
		Connection<CustomAudience> audiences = getConnection(client, accountId + "/customaudiences", CustomAudience.class, parameters);
		
		return audiences;
	}
	
	public String createCustomAudience ( String accountId, String name, List<String> schema, List<List<String>> people ) throws APIException {
		
		// TODO: Add Rate Limit Exception checking
		com.facebook.ads.sdk.AdAccount account = new com.facebook.ads.sdk.AdAccount(accountId, apiContext);
		com.facebook.ads.sdk.CustomAudience audience = account.createCustomAudience()
														.setName(name)
														.setDescription("Created via KNIME " + new Date())
														.setSubtype(EnumSubtype.VALUE_CUSTOM)
														.execute();
		
		// Build Schema
	
		JsonArray schema_array = new JsonArray();
		for ( String schema_item : schema ) {
			schema_array.add(new JsonPrimitive(schema_item));
		}
		
		// Add People
		JsonArray people_array = new JsonArray();
		for (List<String> person_values : people ) {
			
			JsonArray person = new JsonArray();
			for ( String value : person_values ) {
				person.add(new JsonPrimitive(value));
			}
			people_array.add(person);
		}
		
		JsonObject payload = new JsonObject();
		payload.add("schema", schema_array);
		payload.add("data", people_array);
		
		audience.createUser().setPayload(payload.toString()).execute();
		
		return audience.getId();
		
	}
	
	
	
	private <T> Connection<T> getConnection(FacebookClient client, String connection, Class<T> clz, Parameter... parameters ) {
		
		// Guard Statement:
		if ( client == null ) return null;
		
		int attempts = 1;
		Connection<T> cnxn = null;
		
		do {
			LOGGER.debug( "Get connection: " + connection + " (attempt: " + attempts + ")" );
			try { cnxn = client.fetchConnection( connection, clz, parameters ); }
			catch ( FacebookException fexc ) { 
				LOGGER.error( "Facebook Error: " + fexc.getMessage() );
				try { Thread.sleep(attempts++ * 1000); } 
				catch (InterruptedException iexc ) {} 
			}
		} while (cnxn == null && attempts++ <= MAX_ATTEMPTS );
		
		return cnxn;
	}
}
