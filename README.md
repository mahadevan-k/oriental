# Oriental

Oriental is a Java framework for using OrientDB. I built Oriental because we needed a convenient way to use OrientDB to create a backend REST API for a social network app, and so Oriental tends to provide functionality that better suits building REST APIs with it. I used Oriental with Java Spring, so if you're a Spring developer, Oriental's design will fit more naturally with your code.

## Guide Assumptions

Before you read this document, please familiarize yourself with the following

- [OrientDB Graph API](http://orientdb.com/docs/2.1/Graph-Database-Tinkerpop.html)
- [Gremlin Graph Traversal Library](https://github.com/tinkerpop/gremlin/wiki)

## Including Oriental in your project

### Maven

To include Oriental in your project, add the following to your `pom.xml` under the `<dependencies>` tag

    <dependency>
        <groupId>io.github.bitonator</groupId>
        <artifactId>oriental</artifactId>
        <version>0.0.1</version>
    </dependency>

## Tutorial

Lets walk through the code necessary to setup a network of friends.

The entire code for the tutorial is at [examples/socialnetwork](https://github.com/bitonator/oriental/tree/master/src/main/java/io/github/bitonator/oriental/examples/socialnetwork). 

You can either create the project, run it and refer to this doc, or learn from this doc and compare your code to the example project.

### Define the data model with entities

We'll start off by creating a `User` vertex entity. Vertex Entities are JSON serializable vertices and extend the class `VertexEntity`. The Vertex entity class itself, implements the [Blueprints Vertex](http://www.tinkerpop.com/docs/javadocs/blueprints/2.0.0/com/tinkerpop/blueprints/Vertex.html) interface, so most blueprints vertex functions are directly accessible from any vertex entity. In our case, we'll be using the `getProperty` and `setProperty` functions to get and set the name and email of the user.

**User.java**

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
    
Next, lets create a `Friend` edge entity. Edge entites are also JSON serializable and derive from `EdgeEntity` and implement the [Blueprints Edge](http://www.tinkerpop.com/docs/javadocs/blueprints/2.0.0/com/tinkerpop/blueprints/Edge.html) interface. We'll use the `getInVertex` and `getOutVertex` functions of the `EdgeEntity` to get the vertices attached to the friend edge. Edges can also have properties, and the `setProperty` and `getProperty` methods can be used to access and modify them.
    
**friend.java**

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
    
A lot of functions that deal with vertex entities and edge entites in Oriental take the class of the entity as a parameter, to return the appropriate entity type. Hence the usage `getInVertex(User.class)`

Since the `User` vertex entity is serializable, when the `Friend` edge entity is serialized, it will contain a `fromUser` and `toUser` field that in-turn contain serialized `User` entities.
   
Now that we're done implementing the basic entities we need for our system, its time to define some business logic around them.

We do this in Oriental using entity managers. Entity managers make it easy to create, find and delete entities, and managing transactions.

Lets create a user manager to deal with CRUD operations for a user.

### Write business logic with managers

Entity Managers are used to write business-logic in Oriental. All entity managers extend the `EntityManager` class, and should implement the function `createEntityTypes` to create vertex and edge entity types required by the manager. Basically this involves calls to `VertexEntity.createVertexType` and `EdgeEntity.createEdgeType`, which in turn, register the vertex and edge types with the OrientDB database.

Entity Managers are not tied to specific vertex entities or edge entities, since they are meant to provide a higher abstraction layer of business functions. An entity manager can deal with one or more entities, or choose not to deal with an entity at all, though the latter is only useful for logical consistency in your system.

**UserManager.java**

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
    
And lets create a corresponding friend manager that can deal with managing friend connections

**FriendManager.java**

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
	


The main functions used in creating the business logic are `getVertexEntity`, `findVertexEntity`, `getEdgeEntity` and `findEdgeEntity`. Of these, `findEdgeEntity` is interesting because OrientDB doesn't provide a way to find edges based on the vertices. Oriental does this by adding a `tag` property to the edge that contains the ids of the vertices that the edge connects to. This has one implication which is that when an edge is connected to vertices, those vertices must already be committed to the database, since the correct ids are assigned by OrientDB only after saving the vertex to the database.

Long story short, create two vertices, commit, then create the edge between them, then commit again to ensure everything is stored safe and sound in the database. If the vertices you need to connect are already in the database then just go ahead and create the edge and commit it.

### Tie them together with a controller

Lets now get everything together, so we have something that works.

Oriental provides a `ThreadSafeGraph` class which is a wrapper around `OrientGraph` that deals with a small quirk required during initialization to make sure the graph works across threads.

We write operations in Oriental in the following sequence:

- First, create the `OrientGraphFactory`
- then create the managers, which in turn creates a non-transactional graph and registers database types
- then create the `ThreadSafeGraph` instance
- assign the `ThreadSafeGraph` instance to the managers
- call manager functions
- shutdown the thread safe graph

This ensures that non-transactional operations required to register vertex and edge types run before any transactional operation, and ensures that all data is committed to the database at the end of the operation.
    
**App.java**

	public class App {
	    public static void main(String[] args) throws Exception {
	        OrientGraphFactory factory=new OrientGraphFactory("memory:testdb").setupPool(1,10);
	    
	        UserManager userManager=new UserManager(factory);
	        FriendManager friendManager=new FriendManager(factory);
	        
	        ThreadSafeGraph tsg=new ThreadSafeGraph(factory.getTx());
	
	        userManager.setTsg(tsg);
	        friendManager.setTsg(tsg);
	    
	        User u1=userManager.createUser("tom", "tom@gmail.com");
	        User u2=userManager.createUser("harry", "harry@gmail.com");
	        
	        Friend f=friendManager.createFriendship(u1, u2);
	        
	        ObjectMapper mapper=new ObjectMapper();
	        System.out.println(mapper.writeValueAsString(f));
	        
	        System.out.println(mapper.writeValueAsString(friendManager.getFriends(u1)));
	        
	        tsg.shutdown();
	    }
	}

Now you should be able to compile and run the program and see Oriental in action.

## Gremlin Queries and Pagination

To run a generic query that returns simpler objects like counts etc. use the `query` function. For example in the User entity, we could add a friend count as follows:

**User.java**


    public long getFriendCount() {
		HashMap<String, Object> map=new HashMap<String, Object>();
		map.put("user", this.getId().toString());
		long result=this.query("g.v(user).outE('Friend').count()", map);
		return result;
    }
    
Note the use of `map` to add parameters to the query. `g.v(user)` will use the `user` parameter added in `map`.
    
For graph traversal queries from the current vertex, use either `gremlinVertexEntity` or `gremlinVertexEntityList` or `grenlinPaginateVertexEntityList`, or use the Edge equivalents(e.g. `gremlinEdgeEntity`) for Edge entity lists.

Lets add a function that returns a list of friends of the current user.
 
    public Iterable<User> getFriends() {
        HashMap<String, Object> map=new HashMap<String, Object>();
        return this.gremlinVertexEntityList("in('Friend')", map, User.class);
    }
    
Typically though, you don't want to do this because the user may have a large number of friends. Instead you'd return a paginated list from the entity manager instead.

Lets rewrite the `FriendManager`'s `getFriend` function to handle this:

	public PaginatedEdgeEntityIterable<Friend> getFriends(Date start, int limit) throws EntityCreationException {
		HashMap<String, Object> map=new HashMap<String, Object>();
		String firstPageQuery="outE('Friend')"+
				".order({it.b.getProperty('date_of_friendship') <=> it.a.getProperty('date_of_friendship')})";
		String nextPageQuery=firstPageQuery;
		if(start!=null) {
			nextPageQuery=firstPageQuery+".filter({it.date_of_friendship < start})";
			map.put("start", start);
		}
		PaginatedEdgeEntityIterable<Friend> result=
				this.gremlinPaginatedEdgeEntityList(firstPageQuery, nextPageQuery, 
				map, start, limit, Friend.class);
		if(!result.isLastPage()) {
			result.setNext(result.getLastEntity().getDateOfFriendship());
		}
		
		return result;
	}  

Since skipping is a slow process in OrientDB, it is not very well suited to limit/offset pagination. What is better suited for OrientDB is what I like to call start/limit pagination. In start/limit pagination, the first record of the page is identified by a field value on which the result is sorted. Once the first record is found, then the next `limit` records are returned. The next page is identified by the value of the same field in the last record of the returned result set.

So in our above example, we sort our list of friends by the date of friendship, and use that as our field for pagination. So the function accepts a date which identifies the first record with the given date, and a limit, which is used to specify the number of results we want in the page.

If the date is not provided, we assume that the first page is being requested, and so don't add the pagination parts to the gremlin query.

Then we call `gremlinPaginatedVertexEntityList` to compile the list of friends, and finally, set the next page value to the date of the last entity in our query result.

## Call for maintainers/contributions

The good news about Oriental is that I am going to be using it heavily for a project that I"m currently working on.

The bad news is that I probably won't have enough time to fix bugs/issues or implement features as quickly as you may want it.

Sure, you can fork the project and do what you want with it, but in the interest of giving back to the project and for the benefit of all, please consider maintaining this project.

Reach out to me at mahadevan.k@gmail.com if you want to help.

## Stuff that needs to be implemented/improved


- Vertex Views and Edge Views need to be documented
- unit tests need to be written, currently I know that everything works because the API that I've built using this library has a bunch of unit tests and those work, but they aren't specifically made to verify Oriental itself.
- I'm not very happy about the edge tags requiring vertices to be committed for them to work properly
- SQL interface to execute SQL queries against OrientDB, this is supposed to be much faster than Gremlin, so I"m pretty sure folks will need this on projects as they scale.

## Authors/Maintainers

Mahadevan K
(mahadevan.k@gmail.com, @tunescavenger)

## License

MIT


 



    
