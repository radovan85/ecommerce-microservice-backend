package com.radovan.spring.utils;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private String email;
	private String password;
	private byte enabled;
	private Collection<GrantedAuthority> authorities;
	// Dodaj dodatne atribute i metode po potrebi

	// Implementiraj metode iz UserDetails interfejsa
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities; // Postavi odgovarajuće role ili dozvole
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public byte getEnabled() {
		return enabled;
	}

	public void setEnabled(byte enabled) {
		this.enabled = enabled;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

}
