package com.pg.facebook.api.removefromcustomaudience;

import java.util.List;

import com.facebook.ads.sdk.APIException;
import com.pg.facebook.api.FacebookApiClient;
import com.pg.facebook.api.updatecustomaudience.UpdateCustomAudienceNodeModel;

/**
 * This is the model implementation of RemoveFromCustomAudience.
 * 
 *
 * @author P&G Data Science
 */
public class RemoveFromCustomAudienceNodeModel extends UpdateCustomAudienceNodeModel {
    
    /**
     * Constructor for the node model.
     */
    protected RemoveFromCustomAudienceNodeModel() {
        super();
    }

   @Override
   public String executeAction(String audienceId, FacebookApiClient client, List<String> schema, List<List<String>> people) throws APIException {
	   client.removeFromCustomAudience(audienceId, schema, people);
	   return "Removed Users from Audience";
   }

}

