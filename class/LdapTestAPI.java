package dasan.dis.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dasan.dis.bean.InfoBean;
import dasan.dis.code.MISErrorCode;
import dasan.dis.dao.UserDao;

/**
 * <pre>
 * dasan.dis.test
 * LdapConfig.java
 * </pre>
 *
 * @author  saein.lee
 * @date    2019. 2. 11.
 * @version 
 * 
 */
public class LdapTestAPI {
	
	private static final Logger logger = LoggerFactory.getLogger(LdapTestAPI.class);
	private String type= "test.ad.";
	private Properties properties = new Properties();
	private UserDao dao = new UserDao();
	
	public LdapTestAPI(){
		
		String path = "/dasan/dis/config/application.properties";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream s = classLoader.getResourceAsStream(path);
		try{
			 properties.load(s);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public InfoBean connectLdap(InfoBean info){
	    return this.isUserByEmail(this.envLdap(info),  info);
	}
	public Hashtable<String, Object> envLdap(InfoBean info){
		Hashtable<String, Object> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, properties.getProperty(type + "url"));
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PROTOCOL, "ssl");
		env.put(Context.SECURITY_PRINCIPAL, properties.getProperty(type + "domain") + "\\" + properties.getProperty(type + "adminId"));
		env.put(Context.SECURITY_CREDENTIALS, properties.getProperty(type + "adminPwd"));
		return env;
	}
	
	/**
	@Method Name : isUserByEmail
	* Comment  : 
	* @author  : saein.lee
	* @date    : 2019. 2. 12.
	*/
	public InfoBean isUserByEmail(Hashtable<String, Object> env, InfoBean info){
		
		try{
			if(this.ldapsIsUserTemplate(env, info) == true){
				this.ldapUpdateTemplate(env, info, this.setModificationItems(env,info,2));
			}
			return info;
		} catch (NamingException e) {
			e.printStackTrace();
			this.reportLog(env, info,MISErrorCode.SERVER_ERROR, env.get(Context.PROVIDER_URL) + " 서버의 정보가 존재하지 않습니다.");
		    return info;
		}
	}
		
	/**
	@Method Name : ldapsUserTemplate
	* Comment  : 
	* @author  : saein.lee
	* @date    : 2019. 2. 18.
	*/
	public boolean ldapsIsUserTemplate(Hashtable<String, Object> env, InfoBean info) throws NamingException{
		  LdapContext ctx = new InitialLdapContext(env, null);
		  SearchControls sc = new SearchControls();
		  sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		  sc.setReturningAttributes(new String[] {"distinguishedName"});
		  NamingEnumeration results = ctx.search(properties.getProperty(type + "searchBase"), "mail=" + info.getReqEmail(), sc);
			  if (results.hasMoreElements()) {
				   this.reportLog(env, info,MISErrorCode.SUCESS, this.dnFromAd(results, info) + " 님의 사용자 정보가 존재합니다.");
				   ctx.close();
				   return true;
			  }else{
				  this.reportLog(env, info,MISErrorCode.AUTH_ERROR, info.getReqEmail() + " 사용자의 정보가 존재하지 않습니다.");
				  return false;
			  }
	}
	
	
	/**
	@Method Name : ldapTemplate
	* Comment  : 
	* @author  : saein.lee
	* @date    : 2019. 2. 14.
	*/
	private boolean ldapUpdateTemplate(Hashtable<String, Object> env, InfoBean info, ModificationItem[] mods){
		try {
			DirContext ctx = new InitialDirContext(env);
			ctx.modifyAttributes( info.getDistinguishedname() , mods);
			ctx.close();
			this.reportLog(env, info,MISErrorCode.SUCESS, info.getReqEmail() + " 의 비밀번호 변경이 완료되었습니다.");
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
			this.reportLog(env, info,MISErrorCode.REQ_ATTR_ERROR, "비밀번호 정책을 위반하여, " + info.getReqEmail() + " 의 비밀번호 변경이 실패하였습니다.");
			return false;
		}
	}
	
	/**
	@Method Name : setModificationItems
	* Comment  : 
	* @author  : saein.lee
	* 
	* @date    : 2019. 2. 14.
	*/
	private ModificationItem[] setModificationItems(Hashtable<String, Object> env, InfoBean info, int size){
		
		ModificationItem[] mods = new ModificationItem[size]; 
		String[] qPwd = new String[mods.length];
		qPwd[0] = "\"" + info.getOldPwd() + "\"";
		qPwd[1] = "\"" + info.getNewPwd() + "\"";
		
		try {
			for(int i = 0; i < mods.length; i++ ){
				mods[i] =new ModificationItem(setAttributeType(i),getBassicAttribute("unicodePwd", qPwd[i].getBytes("UTF-16LE")));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return mods;
		
	}
	

	
	/**
	@Method Name : getBassicAttribute
	* Comment  : 
	* @author  : saein.lee
	* @date    : 2019. 2. 14.
	*/
	private BasicAttribute getBassicAttribute(String key, byte[] uniPwd){
		BasicAttribute attr= new BasicAttribute(key, uniPwd);
		return attr;
	}
	
	/**
	@Method Name : dnFromAd
	* Comment  : 
	* @author  : saein.lee
	* @date    : 2019. 2. 21.
	*/
	public String dnFromAd(NamingEnumeration results, InfoBean info){
		try {
			SearchResult sr = (SearchResult) results.next();
			String[] disArr = StringUtils.split(sr.getAttributes().toString(), ":");
			info.setDistinguishedname(disArr[1].replaceAll(" ", "").replaceAll("}", ""));
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return info.getDistinguishedname();

	}
	
	/**
	@Method Name : reportErrorLog
	* Comment  : 
	* @author  : saein.lee
	* @date    : 2019. 2. 22.
	*/
	public boolean reportLog(Hashtable<String, Object> env, InfoBean info, MISErrorCode code, String msg){
		switch (code) {
		case SERVER_ERROR:
			info.ofErr(MISErrorCode.SERVER_ERROR.getCode(), msg);
			logger.error(msg);
			break;
		case AUTH_ERROR:
			info.ofErr(MISErrorCode.AUTH_ERROR.getCode(), msg);
			logger.error(msg);			
			break;
		case REQ_ATTR_ERROR:
			info.ofErr(MISErrorCode.REQ_ATTR_ERROR.getCode(), msg);
			logger.error(msg);			
			break;
		case SUCESS:
			info.ofErr(MISErrorCode.SUCESS.getCode(), msg);
			logger.info(msg);			
			break;					
		default:
			break;
		}
		return dao.sqlAdPasswordHistory(info);
	}
	
	/**
	@Method Name : setAttributeType
	* Comment  : 
	* @author  : saein.lee
	* @date    : 2019. 2. 14.
	*/
	protected int setAttributeType(int i){
		int attrType = 0;
		switch(i){
			case 0:
				attrType = DirContext.REMOVE_ATTRIBUTE;
				break;
			case 1:
				attrType = DirContext.ADD_ATTRIBUTE;
				break;
			case 2:	
				attrType = DirContext.REPLACE_ATTRIBUTE;
				break;
		}
		return attrType;
	}
	
}
