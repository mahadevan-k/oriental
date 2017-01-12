package io.github.bitonator.oriental.examples.socialnetwork;

import java.util.Date;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import io.github.bitonator.oriental.EdgeEntity;
import io.github.bitonator.oriental.EntityCreationException;
import io.github.bitonator.oriental.EntityManager;
import io.github.bitonator.oriental.EntityNotFoundException;
import io.github.bitonator.oriental.VertexEntityIterable;

class FriendManager extends EntityManager {
    public FriendManager(OrientGraphFactory factory) {
        super(factory);
    }
    
	@Override
	public void registerEntityTypes() {
		EdgeEntity.createEdgeType(factory, Friend.class.getSimpleName());
	}
    
    public Friend createFriendship(User u1, User u2) throws Exception {
        Friend f=this.findEdgeEntity(u1, u2, Friend.class);
        if(f!=null)
            throw new Exception("You're already friends, why try harder?");
        if(u1.equals(u2)) 
            throw new Exception("Friending yourself...feeling that low huh?");
        f=this.newEdgeEntity(u1, u2, Friend.class);
        f.setDateOfFriendship(new Date());
        this.commit();
        
        return f;
    }
    
    public Iterable getFriends(User u) {
        return u.getEdges(Direction.OUT, Friend.class);
    }
    
    public void deleteFriendship(User u1, User u2) throws EntityCreationException, EntityNotFoundException {
        Friend f=this.getEdgeEntity(u1, u2, Friend.class);
        f.remove();
        this.commit();
    }
}
