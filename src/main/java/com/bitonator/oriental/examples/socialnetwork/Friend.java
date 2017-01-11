package com.bitonator.oriental.examples.socialnetwork;

import java.util.Date;

import com.bitonator.oriental.EdgeEntity;
import com.bitonator.oriental.EntityCreationException;

public class Friend extends EdgeEntity {
    public Date getDateOfFriendship() {
    	return this.getProperty("date_of_friendship");
    }
    
    public void setDateOfFriendship(Date d) {
        this.setProperty("date_of_friendship", d);
    }
    
    public User getFromUser() throws EntityCreationException {
        return this.getInVertex(User.class);
    }
    
    public User getToUser() throws EntityCreationException {
        return this.getOutVertex(User.class);
    }
}
