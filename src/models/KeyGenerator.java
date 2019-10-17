package models;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Application;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.*;

@Path("/generate")
//@ApplicationPath("rest")
public class KeyGenerator {
	
	static Connection c= null;
	
	/*public static void main(String[] args) {
       
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		
			String url="jdbc:mysql://localhost:3306/keydb?useSSL=false&useLegacyDatetimeCode=false&useJDBCCompliantTimezoneShift=true&serverTimezone=UTC";
		
			c=DriverManager.getConnection(url,"root","password");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        save(0,"aaaaaaaaa");

    }*/
	
	public static void connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		
			String url="jdbc:mysql://localhost:3306/keydb?useSSL=false&useLegacyDatetimeCode=false&useJDBCCompliantTimezoneShift=true&serverTimezone=UTC";
			
			c=DriverManager.getConnection(url,"root","password");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@GET
	//@Produces("application/json;charset=UTF-8")
	@Produces({MediaType.TEXT_PLAIN})
    public String createLicenseKey() {
    	//generate a random string
		byte[] array = new byte[7]; // length is bounded by 7
    	    new Random().nextBytes(array);
    	    String generatedString = new String(array, Charset.forName("UTF-8"));
    	    System.out.println("random :"+generatedString);
    	//use the random string to generate another random string that contains only numbers and capital letters to generate the key
        final HashFunction hashFunction = Hashing.sha1();
		final HashCode hashCode = hashFunction.hashString(generatedString);
        final String upper = hashCode.toString().toUpperCase();
        System.out.println(upper);
        return group(upper);
        
    }

    private String group(String s) {
        String result = "";
        for (int i=0; i < s.length()-4; i++) {
            if (i%6==0 && i > 0) {
                result += '-';
            }
            result += s.charAt(i);
        }
        
        //Gson gson =new GsonBuilder().setPrettyPrinting().create();
        
        	//return gson.toJson(result);	
        return result;
    }
    
    
    @GET
	@Path("/show/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getKeys(@PathParam("id") String id){
    	connect();
    	List<Key> keys= new ArrayList<Key>();
    	String sql="Select * from cles where id=\""+id+"\"";
    	try {
			Statement st=c.createStatement();
			ResultSet r= st.executeQuery(sql);
			
			while(r.next()) {
				//System.out.println(r.getString(1));
				keys.add(new Key(r.getString(1),r.getString(2),r.getString(3)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Gson gson = new Gson();
		String result =gson.toJson(keys);
		
    	return "{\"keys\":"+result+"}";
    }
    
    
    @GET
   	@Path("/delete/{id}/{key}")
    @Produces(MediaType.TEXT_PLAIN)
	public int delete(@PathParam("id") String id,@PathParam("key")String key) {
    	connect();
		int r=0;
		String sql="Delete from cles where id=\""+id+"\" and cle=\""+key+"\"";
    	try {
			Statement st=c.createStatement();
			r=st.executeUpdate(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return r;
	}
	
	@GET
	@Path("/save/{id}/{name}/{key}")
	@Produces({MediaType.TEXT_PLAIN})
	public static int save(@PathParam("id") String id,@PathParam("name")String name,@PathParam("key")String key) {
		int r=0;
		try {
			connect();
			System.out.println(key);
			String sql="Insert into cles values(\""+id+"\",\""+name+"\",\""+key+"\");";
	    	System.out.println(sql);
				Statement st=c.createStatement();
				r=st.executeUpdate(sql);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
    	return r;
	}
	
	
}
