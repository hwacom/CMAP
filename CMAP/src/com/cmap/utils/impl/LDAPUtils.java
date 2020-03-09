package com.cmap.utils.impl;

import java.security.MessageDigest;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpServletRequest;

import com.cmap.Env;

public class LDAPUtils {

	public static String getSha256(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(value.getBytes());
			return bytesToHex(md.digest());

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte b : bytes)
			result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}

	// public static void main(String[] args)
	// {
	// String ldapURL = "ldap://192.168.1.1:389";
	// String account = "cool";
	// String password = "cool";
	// try{
	// LDAP_AUTH_AD(ldapURL, account, password);
	// System.out.println("認證成功!");
	// } catch (Exception e) {
	// System.out.println(e.getMessage());
	// }
	// }

	/******************************
	 * LDAP認證
	 * 
	 * @throws Exception
	 ******************************/
	public boolean LDAP_AUTH_AD(HttpServletRequest request, String account, String password) throws Exception {
		if (account.isEmpty() || password.isEmpty())
			throw new Exception("認證失敗!");

		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, Env.LDAP_URL);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, account + "@" + Env.LDAP_DOMAIN);
		env.put(Context.SECURITY_CREDENTIALS, password);

		LdapContext ctx = null;
		try {
			ctx = new InitialLdapContext(env, null);
		} catch (javax.naming.AuthenticationException e) {
			throw new javax.naming.AuthenticationException("認證失敗!");
		} catch (javax.naming.CommunicationException e) {
			throw new javax.naming.CommunicationException("找不到伺服器!");
		} catch (Exception e) {
			throw new Exception("發生未知的錯誤!");
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
				}
			}
			
		}
		return true;
	}
}
