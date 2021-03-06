package com.forum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.forum.database.DBConnect;
import com.forum.model.Authority;
import com.forum.model.User;
import com.forum.util.DateToString;
import com.forum.util.Encipher;

public class UserDaoImp implements UserDao {
	
	private User user;
	private Authority authority;
	private AuthorityDao ad;
	public void setUser(User user) {
		this.user = user;
	}	
	public void setAuthority(Authority authority) {
		this.authority = authority;
	}
	public void setAd(AuthorityDao ad) {
		this.ad = ad;
	}

	private Connection con=null;
	private PreparedStatement prepStmt=null;
	private PreparedStatement prepStmt1=null;
	private ResultSet rs=null;
	
	public int pageSize = 5;

	//"id,name,sex,password,email,authority_id,last_login_ime";
	protected static String UPDATE_AUTHORITY_SQL="update forum_user set authority_id=? where id=?";
	
	@Override
	public User create(User newUser) throws Exception {
		try{
	    	  con=DBConnect.getDBconnection();
	    	  String sql="insert into forum_user values(?,?,?,?,?,?,?)";
	    	  prepStmt =con.prepareStatement(sql); 
	    	  prepStmt1 = con.prepareStatement("select id from forum_user order by id desc limit 0,1");
	          rs = prepStmt1.executeQuery();
	          while(rs.next()) {
	              user.setId((rs.getInt(1))); 
	          }
	    	  prepStmt.setInt(1,user.getId()+1);//id
	    	  System.out.println(user.getId());
	    	  prepStmt.setString(2,newUser.getName());//name
	    	  System.out.println(newUser.getName());
	    	  prepStmt.setString(3, newUser.getSex());//sex
	    	  prepStmt.setString(4,Encipher.MD5(newUser.getPassword()));//password
	    	  System.out.println(newUser.getPassword());
	    	  prepStmt.setString(5,newUser.getEmail());//email
	    	  authority.setName(newUser.getAuthority());
	    	  prepStmt.setInt(6, ad.findId(authority).getId());//authority_id
	    	  prepStmt.setString(7, newUser.getLastTime());//last_login_time
	          prepStmt.executeUpdate();
	      } catch(Exception e){
	    	  e.printStackTrace();
	      } finally{
	    	  DBConnect.closeDB(con, prepStmt, rs);
	      }
	     return newUser;
	}

	@Override
	public void remove(User user) throws Exception {
		try {
	    	con=DBConnect.getDBconnection();
	    	String sql="delete from forum_user where name=?";
	    	prepStmt = con.prepareStatement(sql);
	        prepStmt.setInt(1,user.getId());
	        prepStmt.executeUpdate();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    } finally{
	    	 DBConnect.closeDB(con, prepStmt, rs);
	    }
	}

	@Override
	public User find(User newUser) throws Exception {
	    try {
	        con=DBConnect.getDBconnection();
	        String sql ="select * from forum_user where name=? and password=?";
            prepStmt = con.prepareStatement(sql);
            
    		String time = DateToString.strTimeCurrent();
    		user.setLastTime(time);
    		new UserDaoImp().updateDate(user);
    		
            prepStmt.setString(1,newUser.getName());
            System.out.println("find name:"+newUser.getName());
            prepStmt.setString(2,Encipher.MD5(newUser.getPassword()));
            System.out.println("find pwd:"+newUser.getPassword());
            rs = prepStmt.executeQuery();
            if (rs.next()){
            	user.setId(rs.getInt(1));
            	user.setName(rs.getString(2)); 
            	user.setSex(rs.getString(3));
            	user.setPassword(rs.getString(4));
            	user.setEmail(rs.getString(5));
            	authority.setId(rs.getInt(6));
            	user.setAuthority(ad.findName(authority).getName());
            	user.setLastTime(rs.getString(7));
            	return user;
           }
      } catch (Exception e) {
    	  e.printStackTrace();
      } finally {
    	     DBConnect.closeDB(con, prepStmt, rs);
      }
		return null;
	}
	
	@Override
	public User findAdmin(User newUser) throws Exception {
	    try {
	        con=DBConnect.getDBconnection();
	        String sql="select * from forum_user where name=? and password=? and authority_id=?";
            prepStmt = con.prepareStatement(sql);
            
    		String time = DateToString.strTimeCurrent();
    		user.setLastTime(time);
    		new UserDaoImp().updateDate(user);
            
            prepStmt.setString(1,newUser.getName());
            System.out.println("find name:"+newUser.getName());
            prepStmt.setString(2,Encipher.MD5(newUser.getPassword()));
            System.out.println("find pwd:"+newUser.getPassword());
            authority.setName(newUser.getAuthority());
            prepStmt.setInt(3,ad.findId(authority).getId());
            rs = prepStmt.executeQuery();
            if (rs.next()){
            	user.setId(rs.getInt(1));
            	user.setName(rs.getString(2)); 
            	user.setSex(rs.getString(3));
            	user.setPassword(rs.getString(4));
            	user.setEmail(rs.getString(5));
            	authority.setId(rs.getInt(6));
            	user.setAuthority(ad.findName(authority).getName());
            	user.setLastTime(rs.getString(7));
            	return user;
           }
      } catch (Exception e) {
    	  e.printStackTrace();
      } finally {
    	     DBConnect.closeDB(con, prepStmt, rs);
      }
		return null;
	}

	@Override
	public void updatePassword(User newUser) throws Exception {
		try {
	    	con=DBConnect.getDBconnection();
	    	String sql="update forum_user set password=? where name=?";
			prepStmt = con.prepareStatement(sql);
	    	prepStmt.setString(1,Encipher.MD5(newUser.getPassword()));
	    	prepStmt.setString(2,newUser.getName());
	    	int rowCount=prepStmt.executeUpdate();
            if (rowCount == 0) {
            	throw new Exception("UpdatePassword Error:User Id:" + newUser.getName());
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	 DBConnect.closeDB(con, prepStmt, rs);
        }
	}
	
	@Override
	public void updateDate(User newUser) throws Exception{
		try {
	    	con=DBConnect.getDBconnection();
	    	String sql="update forum_user set last_login_time=? where name=?";
			prepStmt = con.prepareStatement(sql);
	    	prepStmt.setString(1,newUser.getLastTime());
	    	prepStmt.setString(2,newUser.getName());
	    	int rowCount=prepStmt.executeUpdate();
            if (rowCount == 0) {
                   throw new Exception("UpdateDate Error:User Name:" + newUser.getName());
            }
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	 DBConnect.closeDB(con, prepStmt, rs);
        }
	}

	@Override
	public int getPageCount() throws SQLException {

		int recordCount=0,t1=0,t2=0;
		try {
			con = DBConnect.getDBconnection();
			String sql = "select count(*) from forum_user";
			prepStmt=con.prepareStatement(sql);
			rs=prepStmt.executeQuery();
			rs.next();
			recordCount=rs.getInt(1);
			t1=recordCount%pageSize;
			t2=recordCount/pageSize;
		}finally {
			rs.close();
			prepStmt.close();
			DBConnect.closeDB(con, prepStmt, rs);;
		}
		return t1==0?t2:t2+1;
	}

	@Override
	public List<User> listUser(int pageNo) throws Exception {
		
		int startRecno=(pageNo-1)*pageSize;
		ArrayList<User> userList=new ArrayList<User>();
		try {
			con = DBConnect.getDBconnection();
			String sql = "select * from forum_user order by id limit ?,?";
			prepStmt=con.prepareStatement(sql);
			prepStmt.setInt(1,startRecno);
			prepStmt.setInt(2,pageSize);
			rs=prepStmt.executeQuery();
			while(rs.next()) {
				User newUser = new User();
				newUser.setId(rs.getInt(1));
	        	System.out.println("rs.getInt:"+rs.getInt(1));
	        	System.out.println("user.getId:"+newUser.getId());
	        	newUser.setName(rs.getString(2)); 
	        	newUser.setSex(rs.getString(3));
	        	newUser.setPassword(rs.getString(4));
	        	newUser.setEmail(rs.getString(5));
	        	authority.setId(rs.getInt(6));
	        	newUser.setAuthority(ad.findName(authority).getName());
	        	newUser.setLastTime(rs.getString(7));
	            userList.add(newUser);
	        }
		}finally {
			rs.close();
			prepStmt.close();
			DBConnect.closeDB(con, prepStmt, rs);;
		}
		return userList;
	}

}
