package com.bitonator.oriental.examples.socialnetwork;

import com.bitonator.oriental.VertexEntity;

public class User extends VertexEntity {
    public String getName(String name) {
        return this.getProperty("name");
    }
    
    public void setName(String name) {
        this.setProperty("name", name);
    }
    
    public String getEmail() {
    	return this.getProperty("email");
    }
    
    public void setEmail(String email) {
	    this.setProperty("email", email);
    }
}