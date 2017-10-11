package com.pg.facebook.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.NodeLogger;

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
	private String impersonationAccountId;
	private String impersonationAccessToken;
	
	private Boolean tokenConfigured = false;
	
	private static int MAX_ATTEMPTS = 3;
	private static NodeLogger LOGGER = NodeLogger.getLogger(FacebookApiClient.class);
	
	public FacebookApiClient() {
		client = new DefaultFacebookClient(Version.VERSION_2_5);
	}
	
	public FacebookApiClient(String accessToken, String appSecret) {
		client = new DefaultFacebookClient(accessToken, appSecret, Version.VERSION_2_9);
		
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
		params.add(Parameter.with("fields", "account_status,amount_spent"));
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
