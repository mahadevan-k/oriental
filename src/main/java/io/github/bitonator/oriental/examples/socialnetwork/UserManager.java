package io.github.bitonator.oriental.examples.socialnetwork;

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import io.github.bitonator.oriental.EntityCreationException;
import io.github.bitonator.oriental.EntityManager;
import io.github.bitonator.oriental.EntityNotFoundException;
import io.github.bitonator.oriental.VertexEntity;

public class UserManager extends EntityManager {
    public UserManager(OrientGraphFactory factory) {
        super(factory);
    }
    
	@Override
	public void registerEntityTypes() {
		VertexEntity.createVertexType(factory, User.class.getSimpleName());
	}
    
    public User createUser(String name, String email) throws Exception {
        User u=this.findVertexEntity(User.class, "name", name);
        if(u!=null) 
            throw new Exception("user with that name already exists!");
        u=this.findVertexEntity(User.class, "email", email);
        if(u!=null)
            throw new Exception("user with that email already exists!");
        
        u=this.newVertexEntity(User.class);
        u.setName(name);
        u.setEmail(email);
        this.commit();
        
        return u;
    }
    
    public User updateUser(Object id, String name, String email) throws EntityCreationException, EntityNotFoundException {
        User u=this.getVertexEntity(id, User.class);
        u.setName(name);
        u.setEmail(email);
        this.commit();
        
        return u;
    }
    
    public void deleteUser(Object id) throws EntityCreationException, EntityNotFoundException {
        User u=this.getVertexEntity(id, User.class);
        u.remove();
        this.commit();
    }
}
